package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.BoardDto;


/**
 * BoardListMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class BoardListMapper  implements RowMapper<BoardDto> {

	@Override
	public BoardDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return BoardDto.builder()
				.boardCategoryNo(rs.getInt("board_category_no"))
				.boardNo(rs.getInt("board_no"))
				.boardTitle(rs.getString("board_title"))

				.boardWriter(rs.getString("board_writer"))
				.boardWtime(rs.getTimestamp("board_wtime"))
				.boardEtime(rs.getTimestamp("board_etime"))
				.boardLike(rs.getInt("board_like"))
				.boardView(rs.getInt("board_view"))
				.build();
	}
}
