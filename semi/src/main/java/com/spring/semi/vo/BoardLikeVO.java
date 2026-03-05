package com.spring.semi.vo;

import lombok.Data;


/**
 * BoardLikeVO - 화면/쿼리 결과용 VO.
 */
@Data
public class BoardLikeVO {

	private boolean like;
	private int count;
}
