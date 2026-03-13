package com.spring.semi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;


/**
 * AdminController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/home")
	public String home() {
		return "/WEB-INF/views/admin/home.jsp";
	}

}
