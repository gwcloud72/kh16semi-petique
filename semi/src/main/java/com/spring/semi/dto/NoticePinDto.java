package com.spring.semi.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * NoticePinDto - 요청/응답 데이터 전달 객체.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticePinDto {
	private int boardNo;
	private Timestamp pinStart;
	private Timestamp pinEnd;
	private int pinOrder;
}
