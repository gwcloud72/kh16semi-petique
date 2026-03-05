package com.spring.semi.vo;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * CategoryDetailVO - 화면/쿼리 결과용 VO.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CategoryDetailVO {
    private int categoryNo;
    private String categoryName;
    private int boardCount;
    private Timestamp lastUseTime;
    private String lastUser;

	public String getBoardWriteTime() {
		LocalDateTime wtime = lastUseTime.toLocalDateTime();
		LocalDate today = LocalDate.now();
		LocalDate wday = wtime.toLocalDate();

		if(wday.isBefore(today)) {
			return wtime.toLocalDate().toString();
		}
		else {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
			return wtime.toLocalTime().format(fmt);
		}
	}
}
