package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.MemberDto;


/**
 * MemberMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class MemberMapper implements RowMapper<MemberDto> {

	@Override
	public MemberDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MemberDto.builder()
				.memberId(rs.getString("member_id"))
				.memberPw(rs.getString("member_pw"))
				.memberNickname(rs.getString("member_nickname"))
				.memberEmail(rs.getString("member_email"))
				.memberDescription(rs.getString("member_description"))
				.memberPoint(rs.getInt("member_point"))
				.memberUsedPoint(rs.getInt("member_used_point"))
				.memberLevel(rs.getInt("member_level"))
				.memberAuth(rs.getString("member_auth"))
				.memberJoin(rs.getTimestamp("member_join"))
				.memberLogin(rs.getTimestamp("member_login"))
				.memberChange(rs.getTimestamp("member_change"))
				.build();
	}
}
