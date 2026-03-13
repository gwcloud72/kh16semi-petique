package com.spring.semi.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * MailVO - 화면/쿼리 결과용 VO.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MailVO {

	private int mailNo;
	private String mailOwner;
	private String mailTitle;
	private Timestamp mailWtime;
	private String senderNickname;
	private String targetNickname;

}
