package com.spring.semi.vo;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * AdoptBoardVO - 화면/쿼리 결과용 VO.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AdoptBoardVO {

    private int boardCategoryNo;
    private int boardNo;
    private String boardTitle;
    private String boardWriter;
    private Timestamp boardWtime;
    private Timestamp boardEtime;
    private int boardLike;
    private int boardView;
    private int boardReply;
    private int boardScore;
    private int deleted;
    private String animalHeaderName;
    private String typeHeaderName;
    private String boardContent;
    private int boardAnimalHeader;
    private int boardTypeHeader;


    private String levelName;
    private String badgeImage;


    private int animalNo;
    private String animalPermission;


    private String memberNickname;


}
