package com.spring.semi.vo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * BoardListVO - 화면/쿼리 결과용 VO.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardListVO {
	private int boardNo;
	private String categoryName;
	private String boardTitle;
	private Timestamp boardWtime;
	private int boardView;

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
