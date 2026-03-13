package com.spring.semi.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * AdoptionApplyVO - 화면/쿼리 결과용 VO.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AdoptionApplyVO {
    private int applyNo;
    private int boardNo;
    private int animalNo;
    private String applicantId;
    private String applicantNickname;
    private String badgeImage;
    private String levelName;
    private String applyContent;
    private String applyStatus;
    private Timestamp applyWtime;
    private Timestamp applyEtime;
}
