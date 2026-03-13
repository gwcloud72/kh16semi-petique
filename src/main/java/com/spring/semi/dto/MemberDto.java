package com.spring.semi.dto;

import java.sql.Timestamp;

import org.jsoup.Jsoup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * MemberDto - 요청/응답 데이터 전달 객체.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberDto {
	private String memberId;
	private String memberPw;
	private String memberNickname;
	private String memberEmail;
	private String memberDescription;
	private int memberPoint;
	private int memberUsedPoint;
	private int memberLevel;
	private String memberAuth;
	private Timestamp memberJoin;
	private Timestamp memberLogin;
	private Timestamp memberChange;

	public String getMemberDescriptionPlain() {
		if (memberDescription == null) return null;
		return Jsoup.parse(memberDescription).text();
	}
}
