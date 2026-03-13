package com.spring.semi.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private int notiNo;
    private String memberId;
    private String notiType;
    private String notiMessage;
    private String notiUrl;
    private String notiRead;
    private Timestamp notiWtime;
}
