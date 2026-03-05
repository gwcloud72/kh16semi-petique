package com.spring.semi.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.MemberDto;

import jakarta.servlet.http.HttpSession;


/**
 * SidebarAdvice - 공통 처리(AOP).
 */
@ControllerAdvice
public class SidebarAdvice {
	@Autowired
	private MemberDao memberDao;

	@ModelAttribute
	public void addCommonAttributes(
			Model model,
			HttpSession session
			) {
		String loginId = (String) session.getAttribute("loginId");
		if(loginId != null) {
			MemberDto memberDto = memberDao.selectOne(loginId);
			model.addAttribute("sidebarInfo", memberDto);
		}
	}

}
