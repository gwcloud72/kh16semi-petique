package com.spring.semi.aop;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.spring.semi.error.NeedPermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**
 * AdminInterceptor - 공통 처리(AOP).
 */
@Service
public class AdminInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response,
			Object Handler) throws Exception{
		HttpSession session =request.getSession();
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		int limit = 0;
		if(loginLevel != limit)
			throw new NeedPermissionException("권한 부족");
		return true;
	}
}
