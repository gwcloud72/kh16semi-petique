package com.spring.semi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.semi.dao.NotificationDao;
import com.spring.semi.dto.NotificationDto;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationDao notificationDao;

    @GetMapping("/go")
    public String go(@RequestParam int notiNo, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        NotificationDto dto = notificationDao.selectOne(notiNo);
        if (dto == null || dto.getMemberId() == null || !loginId.equals(dto.getMemberId())) {
            return "redirect:/member/mypage?tab=noti";
        }

        notificationDao.readOne(notiNo, loginId);

        String url = dto.getNotiUrl();
        if (url != null && url.startsWith("/")) {
            return "redirect:" + url;
        }
        return "redirect:/member/mypage?tab=noti";
    }

    @PostMapping("/readAll")
    public String readAll(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        notificationDao.readAll(loginId);
        return "redirect:/member/mypage?tab=noti";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam int notiNo, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        notificationDao.deleteOne(notiNo, loginId);
        return "redirect:/member/mypage?tab=noti";
    }
}
