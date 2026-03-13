package com.spring.semi.aop;

import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.error.NeedPermissionException;
import com.spring.semi.error.UnauthorizationException;


/**
 * ExceptionControllerAdvice - 공통 처리(AOP).
 */
@ControllerAdvice
public class ExceptionControllerAdvice {
	private static final Logger log = LoggerFactory.getLogger(ExceptionControllerAdvice.class);


	@ExceptionHandler(value = {TargetNotfoundException.class, NoResourceFoundException.class})
	public String notFound(Exception e, Model model) {
		String title;
		if(e instanceof NoResourceFoundException) {
			title = "페이지를 찾을 수 없습니다";
		}
		else {
			title = e.getMessage();
			if(title == null || title.isBlank()) {
				title = "페이지를 찾을 수 없습니다";
			}
		}
		model.addAttribute("title", title);
		return "/WEB-INF/views/error/notFound.jsp";
	}

	@ExceptionHandler(UnauthorizationException.class)
	public String unauthorize(UnauthorizationException e, Model model) {
		model.addAttribute("title", e.getMessage());
		return "/WEB-INF/views/error/unauthorize.jsp";
	}

	@ExceptionHandler(NeedPermissionException.class)
	public String needPermission(NeedPermissionException e, Model model) {
		model.addAttribute("title", e.getMessage());
		return "/WEB-INF/views/error/needPermission.jsp";
	}
	@ExceptionHandler(Exception.class)
	public String all(Exception e, Model model) {
		model.addAttribute("title", "일시적인 오류가 발생했습니다");
		log.error("Unhandled exception", e);
		return "/WEB-INF/views/error/all.jsp";
	}
}
