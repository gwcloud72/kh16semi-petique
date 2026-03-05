package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.BoardVO;


/**
 * BoardVOMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class BoardVOMapper  implements RowMapper<BoardVO> {

	@Override
	public BoardVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return BoardVO.builder()
				.boardCategoryNo(rs.getInt("board_category_no"))
				.boardNo(rs.getInt("board_no"))
				.boardTitle(rs.getString("board_title"))
				.boardWriter(rs.getString("board_writer"))
				.boardWtime(rs.getTimestamp("board_wtime"))
				.boardEtime(rs.getTimestamp("board_etime"))
				.boardLike(rs.getInt("board_like"))
				.boardView(rs.getInt("board_view"))
				.boardReply(rs.getInt("board_reply"))
				.deleted(rs.getInt("deleted"))
				.animalHeaderName(rs.getString("animal_header_name"))
				.typeHeaderName(rs.getString("type_header_name"))
				.boardScore(rs.getInt("board_score"))
				.build();
	}
}
