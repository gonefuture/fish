/**
 * Copyright www.cnjson.com &copy; 
 * http://www.cnjson.com
 * 2015-2017 All rights reserved.
 */
package com.cnjson.sensor;

import com.cnjson.sensor.modules.sys.entity.Student;
import com.cnjson.sensor.modules.sys.service.StudentService;

import junit.framework.TestCase;

/**
* @author 作者 E-mail:cgli@qq.com
* @version 创建时间：Dec 24, 2016 11:05:25 AM
* 类说明
*/
/**
 * @author cgli
 *
 */
public class StudentTest extends TestCase {

	/**
	 * 
	 */
	public StudentTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public StudentTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public void testAdd() {
		StudentService service = new StudentService();
		Student entity = new Student();
		entity.setId("1");
		entity.setName("name");
		try {
			service.add(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
