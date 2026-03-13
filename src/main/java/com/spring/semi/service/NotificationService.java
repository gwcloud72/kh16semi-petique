package com.spring.semi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.semi.dao.NotificationDao;
import com.spring.semi.dto.NotificationDto;


@Service
public class NotificationService {

    @Autowired
    private NotificationDao notificationDao;

    public void notify(String memberId, String type, String message, String url) {
        if (memberId == null || memberId.isBlank()) return;
        if (message == null || message.isBlank()) return;

        String safeUrl = null;
        if (url != null) {
            String trimmed = url.trim();
            if (trimmed.startsWith("/")) safeUrl = trimmed;
        }

        NotificationDto dto = NotificationDto.builder()
                .notiNo(notificationDao.sequence())
                .memberId(memberId)
                .notiType(type == null ? "INFO" : type)
                .notiMessage(message.trim())
                .notiUrl(safeUrl)
                .build();

        notificationDao.insert(dto);
    }
}
