package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.MemberLevelDto;


/**
 * MemberLevelMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class MemberLevelMapper implements RowMapper<MemberLevelDto> {

	@Override
	public MemberLevelDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MemberLevelDto.builder()
				.levelNo(rs.getInt("level_no"))
				.levelName(rs.getString("level_name"))
				.minPoint(rs.getInt("min_point"))
				.maxPoint(rs.getInt("max_point"))
				.description(rs.getString(	"description"))
				.badgeImage(rs.getString("badge_image"))
				.memberCount(rs.getInt("member_count"))
				.build();
	}
}
