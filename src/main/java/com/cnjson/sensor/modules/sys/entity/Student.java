/**
 * Copyright www.cnjson.com &copy; 
 * http://www.cnjson.com
 * 2015-2017 All rights reserved.
 */
package com.cnjson.sensor.modules.sys.entity;
/**
* @author 作者 E-mail:cgli@qq.com
* @version 创建时间：Dec 24, 2016 11:00:51 AM
* 类说明
*/
/**
 * @author cgli
 *
 */

import com.cnjson.sensor.db.annotation.FieldMeta;
import com.cnjson.sensor.db.annotation.TableName;
import com.cnjson.sensor.db.entity.BaseEntity;

@TableName("xstudent")
public class Student extends BaseEntity<Student> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8899507237761493441L;
	
	@FieldMeta(name="std_name",description="名称")
	private String name ;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	

}
