package com.spring.semi.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * BoardDto - 요청/응답 데이터 전달 객체.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardDto {
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


	public String getFormattedWtime() {
	    if (boardWtime == null) return "";
	    LocalDateTime wtime = boardWtime.toLocalDateTime();
	    LocalDateTime now = LocalDateTime.now();

	    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
	    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	    if (wtime.toLocalDate().isEqual(now.toLocalDate())) {
	        return wtime.format(timeFmt);
	    } else {
	        return wtime.format(dateFmt);
	    }
	}

}
