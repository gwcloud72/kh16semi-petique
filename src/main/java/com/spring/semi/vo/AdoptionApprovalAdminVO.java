package com.spring.semi.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * AdoptionApprovalAdminVO - 화면/쿼리 결과용 VO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionApprovalAdminVO {
	private int applyNo;
	private int boardNo;
	private int animalNo;
	private String applicantId;
	private String applicantNickname;
	private String boardTitle;
	private String boardWriter;
	private String boardWriterNickname;
	private String animalName;
	private String applyStatus;
	private Timestamp applyWtime;
	private Timestamp applyEtime;
}
