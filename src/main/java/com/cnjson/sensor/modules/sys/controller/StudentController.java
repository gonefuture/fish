/**
 * Copyright www.cnjson.com &copy; 
 * http://www.cnjson.com
 * 2015-2017 All rights reserved.
 */
package com.cnjson.sensor.modules.sys.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cnjson.sensor.config.Global;
import com.cnjson.sensor.modules.sys.entity.Student;
import com.cnjson.sensor.modules.sys.service.StudentService;
import com.cnjson.sensor.web.BaseController;

/**
 * @author cgli, E-mail:cgli@qq.com
 * @version created on ：Jan 6, 2017 4:38:39 PM
 */

@Controller
@RequestMapping("${adminPath}/sys/student")
public class StudentController extends BaseController {

	@Autowired
	private StudentService studentService;

	/**
	 * 
	 */
	public StudentController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取实例对象，一般在JSP修改页面中，如form加上 ModelAttribute即可以获取此实例对象
	 * 
	 * @param id
	 * @return
	 */
	@ModelAttribute
	public Student get(@RequestParam(required = false) String id) {
		Student student = new Student();
		try {
			if (StringUtils.isNoneBlank(id)) {
				student = studentService.get(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return student;
	}

	/**
	 * 请求展示页面。
	 * 
	 * @param request
	 * @return 返回URL的JSP页面
	 */
	@RequestMapping("index")
	public String showPage(HttpServletRequest request) {

		// 这里代表返回前端JSP页面.一般不用写jsp后缀
		return "modules/sys/student";

	}

	/**
	 * 展示表单页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("form")
	public String form(Student student, Model model) {
		model.addAttribute("student", student);
		return "modules/sys/studentForm";
	}

	@RequestMapping(value = { "list" })
	public String list(Student student, HttpServletRequest request, HttpServletResponse response, Model model) {

		// TODO should be pager.....
		List<Student> list;
		try {
			list = studentService.findList(student);
			model.addAttribute("list", list);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "modules/sys/studentList";
	}

	/**
	 * 提交表单请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "save")
	public String save(Student student, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, student)) {
			return form(student, model);
		}
		try {
			studentService.save(student);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		addMessage(redirectAttributes, "保存单表成功");
		return "redirect:" + Global.getAdminPath() + "/sys/student/list?repage";
	}

	@RequestMapping(value = "delete")
	public String delete(Student student, RedirectAttributes redirectAttributes) {
		try {
			studentService.delete(student);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		addMessage(redirectAttributes, "删除单表成功");
		return "redirect:" + Global.getAdminPath() + "/sys/student/list/?repage";
	}

}
