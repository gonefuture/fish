/**
 * Copyright www.cnjson.com &copy; 
 * http://www.cnjson.com
 * 2015-2017 All rights reserved.
 */
package com.cnjson.sensor.modules.sys.service;

import org.apache.commons.lang3.StringUtils;

import com.cnjson.sensor.db.dao.AbstractDao;
import com.cnjson.sensor.db.dao.IBaseDao;
import com.cnjson.sensor.modules.sys.entity.Student;

/**
* @author 作者 E-mail:cgli@qq.com
* @version 创建时间：Dec 24, 2016 11:04:08 AM
* 类说明
*/
/**
 * @author cgli
 *
 */
public class StudentService extends AbstractDao<Student> implements IBaseDao<Student> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnjson.sensor.db.dao.IBaseDao#get(java.lang.String)
	 */
	@Override
	public Student get(String id) throws Exception {
		Student entity = new Student();
		entity.setId(id);
		return super.get(entity);
	}

	/**
	 * @param student
	 * @throws Exception
	 */
	public void save(Student student) throws Exception {
		if (StringUtils.isEmpty(student.getId())) {
			super.add(student);
		} else {
			super.update(student);
		}

	}

}
