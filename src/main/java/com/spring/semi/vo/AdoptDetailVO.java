package com.spring.semi.vo;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * AdoptDetailVO - 화면/쿼리 결과용 VO.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AdoptDetailVO {

	private int boardCategoryNo;
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriter;
	private Timestamp boardWtime;
	private Timestamp boardEtime;
	private int boardLike;
	private int boardView;
	private int boardReply;


	private int boardAnimalHeader;
	private int boardTypeHeader;
	private int boardScore;


	private int deleted;
	   private String badgeImage;
   private String levelName;


    private int animalNo;
    private String animalName;
    private String animalPermission;
    private String animalContent;

	private String boardSummary;

    private String animalHeaderName;
    private String typeHeaderName;


    private String memberNickname;
	private String adoptionStage;

    private Integer mediaNo;
}
