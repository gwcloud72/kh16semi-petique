package com.spring.semi.vo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * BoardVO - 화면/쿼리 결과용 VO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardVO {
	private int boardCategoryNo;
	private int boardNo;
	private String boardTitle;
	private String boardWriter;
	private Timestamp boardWtime;
	private Timestamp boardEtime;
	private int boardLike;
	private int boardView;
	private int boardReply;
	private int deleted;
	private String typeHeaderName;
	private String animalHeaderName;
	private int boardScore;

	public String getFormattedWtime() {
		if (boardWtime == null)
			return "";
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
