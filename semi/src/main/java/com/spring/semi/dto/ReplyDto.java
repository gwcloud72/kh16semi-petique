package com.spring.semi.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * ReplyDto - 요청/응답 데이터 전달 객체.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReplyDto {
	private int replyCategoryNo;
	private int replyTarget;
	private int replyNo;
	private String replyContent;
	private String replyWriter;
	private Timestamp replyWtime;
	private Timestamp replyEtime;
	private int replyLike;
}
