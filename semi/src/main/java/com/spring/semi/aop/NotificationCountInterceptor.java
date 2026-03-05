package com.spring.semi.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.spring.semi.dao.NotificationDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Service
public class NotificationCountInterceptor implements HandlerInterceptor {

    @Autowired
    private NotificationDao notificationDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null) return true;

        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return true;

        try {
            int unread = notificationDao.countUnreadByMemberId(loginId);
            request.setAttribute("unreadNotiCount", unread);
        } catch (Exception e) {
            request.setAttribute("unreadNotiCount", 0);
        }

        return true;
    }
}
