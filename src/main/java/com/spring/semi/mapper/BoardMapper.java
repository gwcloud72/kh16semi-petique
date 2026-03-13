package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.BoardDto;


/**
 * BoardMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class BoardMapper  implements RowMapper<BoardDto> {

	@Override
	public BoardDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return BoardDto.builder()
				.boardCategoryNo(rs.getInt("board_category_no"))
				.boardNo(rs.getInt("board_no"))
				.boardTitle(rs.getString("board_title"))
				.boardContent(rs.getString("board_content"))
				.boardWriter(rs.getString("board_writer"))
				.boardWtime(rs.getTimestamp("board_wtime"))
				.boardEtime(rs.getTimestamp("board_etime"))
				.boardLike(rs.getInt("board_like"))
				.boardView(rs.getInt("board_view"))
				.boardReply(rs.getInt("board_reply"))
				.boardAnimalHeader(rs.getInt("board_animal_header"))
				.boardTypeHeader(rs.getInt("board_type_header"))
				.boardScore(rs.getInt("board_score"))
				.deleted(rs.getInt("deleted"))
				.build();
	}
}
