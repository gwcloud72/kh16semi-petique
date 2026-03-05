package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.AdoptDetailVO;


/**
 * AdoptDetailMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class AdoptDetailMapper implements RowMapper<AdoptDetailVO> {

	@Override
	public AdoptDetailVO mapRow(ResultSet rs, int rowNum) throws SQLException {

		AdoptDetailVO vo = new AdoptDetailVO();


		vo.setBoardNo(rs.getInt("board_no"));
		vo.setBoardCategoryNo(rs.getInt("board_category_no"));
		vo.setBoardTitle(rs.getString("board_title"));
		vo.setBoardContent(rs.getString("board_content"));
		vo.setBoardWriter(rs.getString("board_writer"));
		vo.setBoardWtime(rs.getTimestamp("board_wtime"));
		vo.setBoardEtime(rs.getTimestamp("board_etime"));
		vo.setBoardLike(rs.getInt("board_like"));
		vo.setBoardView(rs.getInt("board_view"));
		vo.setBadgeImage(rs.getString("badge_image"));
        vo.setLevelName(rs.getString("level_name"));


        vo.setAnimalNo(rs.getInt("animal_no"));
        vo.setMemberNickname(rs.getString("member_nickname"));
		vo.setAnimalName(rs.getString("animal_name"));
		vo.setAnimalPermission(rs.getString("animal_permission"));
		vo.setAnimalContent(rs.getString("animal_content"));
		 vo.setMediaNo(rs.getInt("media_no"));


        vo.setAnimalHeaderName(rs.getString("animal_header_name"));
        vo.setTypeHeaderName(rs.getString("type_header_name"));

		return vo;
	}
}
