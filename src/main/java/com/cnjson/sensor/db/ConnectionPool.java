package com.cnjson.sensor.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用共享模式，创建共享连接池
 * 
 * @author cgli
 *
 */
public class ConnectionPool {

	/**
	 * 连接池大小，可考虑从配置文件中读取。
	 */
	// private final int poolSize = 200;
	private final int initialConnections = 10; // 连接池的初始大小
	private final int autoIncreaseStep = 5;// 连接池自增步进
	private int maxConnections = 50; // 连接池最大的大小
	private final int retryTimes = 20;// 最大的重试次数
	private static AtomicInteger count = new AtomicInteger(0);
	
	private final String driver = JdbcInfo.JDBC_DRIVER;
	private final String url = JdbcInfo.JDBC_URL;
	private final String username = JdbcInfo.JDBC_USER;
	private final String password = JdbcInfo.JDBC_PASSWORD;
	private List<PooledConnection> pool = null;

	/**
	 * 防止被调用者实例化，使用单例模式
	 */
	private ConnectionPool() {
		// Connection conn = null;
		/*
		 * pool = new Vector<>(poolSize); try { for (int i = 0; i < poolSize;
		 * i++) { Class.forName(driver); conn = DriverManager.getConnection(url,
		 * username, password); if (conn != null) { pool.add(conn); } } } catch
		 * (Exception e) { e.printStackTrace(); }
		 */

		try {
			Class.forName(driver);
			pool = new CopyOnWriteArrayList<>();
			create(initialConnections);
			System.out.println(" 数据库连接池创建成功！ ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public synchronized Connection getConnection() { if (pool.size() > 0) {
	 * Connection con = pool.get(0); pool.remove(con); return con; } else {
	 * return null; } }
	 */

	/**
	 * 使用完成之后，交回连接池，并没有真正的关闭连接。
	 * 
	 * @param conn
	 *            连接对象
	 */
	public synchronized void release(Connection conn) {
		returnConnection(conn);
	}

	/**
	 * 关闭所有的连接，释放连接池。
	 * 
	 * @throws SQLException
	 */
	public synchronized void releasePool() throws SQLException {
		if (pool == null) {
			System.out.println(" 连接池不存在，无法关闭 !");
			return;
		}
		for (PooledConnection connection : pool) {
			if (connection.isBusy()) {
				wait(2000); // 等 5 秒
			}
			close(connection.getConnection());
			pool.remove(connection);
		}
		pool = null;
	}

	

	/**
	 * 获取连接对象
	 * 
	 * @return 返回连接对象{@link Connection}
	 */
	public synchronized Connection getConnection() {
		if (pool == null)
			return null;

		Connection conn = null;
		while (true) {
			wait(500);
			try {
				conn = getFreeConnection();
				break;
			} catch (Exception e) {
				count.getAndIncrement();
				System.out.println("没有可用连接，重试！");
				if (count.get() > retryTimes) {
					System.out.println("获取连接已经重试" + retryTimes + "次失败！");
					break;
				}
			}
		}
		return conn;// 返回获得的可用的连接
	}

	/**
	 * 刷新连接池
	 * 
	 * @throws SQLException
	 */
	public synchronized void refreshConnections() throws SQLException {
		if (pool == null) {
			System.out.println(" 连接池不存在，无法刷新 !");
			return;
		}
		count.set(0);
		for (PooledConnection connection : pool) {
			if (connection.isBusy()) {
				wait(2000);
			}
			// 关闭此连接，用一个新的连接代替它。
			close(connection.getConnection());
			connection.setConnection(buildConnection());
			connection.setBusy(false);
		}
	}

	private void returnConnection(Connection conn) {
		if (pool == null) {
			System.out.println(" 连接池不存在，无法返回此连接到连接池中 !");
			return;
		}
		PooledConnection connection = this.pool.stream().filter(x -> x.getConnection() == conn).findAny()
				.orElse(emptyPool);
		connection.setBusy(false);
	}

	private void create(int numConnections) throws SQLException {
		for (int i = 0; i < numConnections; i++) {
			try {
				if (this.maxConnections > 0 && this.pool.size() >= this.maxConnections) {
					break;
				}
				Connection conn = buildConnection();
				pool.add(new PooledConnection(conn));
			} catch (SQLException e) {
				System.out.println(" 创建数据库连接失败！ " + e.getMessage());
				throw new SQLException();
			}
			System.out.println(" 数据库连接己创建 ......");
		}
	}

	private Connection buildConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(url, username, password);
		if (pool.size() == 0) {
			DatabaseMetaData metaData = conn.getMetaData();
			final int driverMaxConnections = metaData.getMaxConnections();
			if (driverMaxConnections > 0) {
				this.maxConnections = Math.min(maxConnections, driverMaxConnections);
			}
		}
		return conn;
	}

	private Connection getFreeConnection() throws Exception {
		Connection conn = findFree();
		if (conn == null) {
			// 如果目前连接池中没有可用的连接,按步进增加连接
			create(autoIncreaseStep);
			conn = findFree();
		}
		return conn;
	}

	private Connection findFree() throws Exception {
		PooledConnection connection = pool.stream().filter(x -> !x.isBusy()).findAny().get();
		connection.setBusy(true);
		return connection.getConnection();
	}

	private void close(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println(" 关闭数据库连接出错： " + e.getMessage());
		}
	}

	private void wait(int mSeconds) {
		try {
			// Thread.sleep(mSeconds);
			TimeUnit.MILLISECONDS.sleep(mSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取自身实例
	 * 
	 * @return {@link ConnectionPool}
	 */
	public static ConnectionPool getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * 使用单例模式创建连接池对象
	 * 
	 * @author cgli
	 *
	 */
	private static class SingletonHolder {
		private static ConnectionPool instance = new ConnectionPool();
	}

	/**
	 * 内部使用的用于保存连接池中连接对象的类
	 */
	private PooledConnection emptyPool = new PooledConnection(null);

	private class PooledConnection {

		private Connection connection = null;// 数据库连接
		private volatile boolean busy = false; // 此连接是否正在使用的标志，默认没有正在使用

		public PooledConnection(Connection connection) {
			this.connection = connection;
		}

		public Connection getConnection() {
			return connection;
		}

		public void setConnection(Connection connection) {
			this.connection = connection;
		}

		public boolean isBusy() {
			return busy;
		}

		public void setBusy(boolean busy) {
			this.busy = busy;
		}

	}

}
