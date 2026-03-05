package com.spring.semi.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * ReplyLikeDto - 요청/응답 데이터 전달 객체.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReplyLikeDto {

	private int replyNo;


	private String memberId;
}
