package com.spring.semi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * MemberLevelDto - 요청/응답 데이터 전달 객체.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberLevelDto {
private int levelNo;
private String levelName;
private int minPoint;
private int maxPoint;
private String description;
private String badgeImage;
private int memberCount;

}
