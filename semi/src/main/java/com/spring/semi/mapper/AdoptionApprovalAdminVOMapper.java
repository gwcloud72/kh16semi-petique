package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.AdoptionApprovalAdminVO;


/**
 * AdoptionApprovalAdminVOMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class AdoptionApprovalAdminVOMapper implements RowMapper<AdoptionApprovalAdminVO> {

	@Override
	public AdoptionApprovalAdminVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return AdoptionApprovalAdminVO.builder()
				.applyNo(rs.getInt("apply_no"))
				.boardNo(rs.getInt("board_no"))
				.animalNo(rs.getInt("animal_no"))
				.applicantId(rs.getString("applicant_id"))
				.applicantNickname(rs.getString("applicant_nickname"))
				.boardTitle(rs.getString("board_title"))
				.boardWriter(rs.getString("board_writer"))
				.boardWriterNickname(rs.getString("board_writer_nickname"))
				.animalName(rs.getString("animal_name"))
				.applyStatus(rs.getString("apply_status"))
				.applyWtime(rs.getTimestamp("apply_wtime"))
				.applyEtime(rs.getTimestamp("apply_etime"))
				.build();
	}

}
