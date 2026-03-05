package com.spring.semi.aop;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.spring.semi.error.UnauthorizationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**
 * MemberLoginInterceptor - 공통 처리(AOP).
 */
@Service
public class MemberLoginInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		String loginId = (String) session.getAttribute("loginId");
		if(loginId == null) {
			throw new UnauthorizationException("로그인하셔야합니다.");
		}

		return true;
	}

}
