package com.spring.semi.vo;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * ReplyLikeVO - 화면/쿼리 결과용 VO.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReplyLikeVO {
	private boolean islike;
	private int count;
}
