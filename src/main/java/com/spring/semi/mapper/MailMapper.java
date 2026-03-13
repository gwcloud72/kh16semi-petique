package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.MailDto;


/**
 * MailMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class MailMapper implements RowMapper<MailDto> {

	@Override
	public MailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MailDto.builder()
				.mailNo(rs.getInt("mail_no"))
				.mailOwner(rs.getString("mail_owner"))
				.mailSender(rs.getString("mail_sender"))
				.mailTarget(rs.getString("mail_target"))
				.mailTitle(rs.getString("mail_title"))
				.mailContent(rs.getString("mail_content"))
				.mailWtime(rs.getTimestamp("mail_wtime"))
				.build();
	}
}
