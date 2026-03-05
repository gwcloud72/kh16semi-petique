package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.AdoptDetailVO;


/**
 * AdoptBoardMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class AdoptBoardMapper  implements RowMapper<AdoptDetailVO> {

	@Override
	public AdoptDetailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return AdoptDetailVO.builder()

				.boardCategoryNo(rs.getInt("board_category_no"))
				.boardNo(rs.getInt("board_no"))
				.boardTitle(rs.getString("board_title"))
				.boardContent(rs.getString("BOARD_CONTENT"))
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


				.badgeImage(rs.getString("BADGE_IMAGE"))
		        .levelName(rs.getString("level_name"))


		        .animalNo(rs.getInt("animalNo"))
		        .animalName(rs.getString("animal_name"))
		        .animalPermission(rs.getString("animal_permission"))
		        .animalContent(rs.getString("ANIMAL_CONTENT"))


		        .memberNickname(rs.getString("member_nickname"))
		        .adoptionStage(rs.getString("ADOPTION_STAGE"))


				.build();
	}
}
