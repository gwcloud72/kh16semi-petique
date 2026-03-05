package com.spring.semi.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * MailDto - 요청/응답 데이터 전달 객체.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MailDto {
	private int mailNo;
	private String mailOwner;
	private String mailSender;
	private String mailTarget;
	private String mailTitle;
	private String mailContent;
	private Timestamp mailWtime;
}
