package com.spring.semi.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * MailDetailVO - 화면/쿼리 결과용 VO.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MailDetailVO {
	private int mailNo;
	private String mailOwner;
	private String mailSender;
	private String mailTarget;
	private String mailTitle;
	private String mailContent;
	private Timestamp mailWtime;
	private String senderNickname;
}
