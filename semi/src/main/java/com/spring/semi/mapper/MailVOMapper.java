package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.MailVO;


/**
 * MailVOMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class MailVOMapper implements RowMapper<MailVO> {

	@Override
	public MailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MailVO.builder()
				.mailNo(rs.getInt("mail_no"))
				.mailOwner(rs.getString("mail_owner"))
				.mailTitle(rs.getString("mail_title"))
				.mailWtime(rs.getTimestamp("mail_wtime"))
				.senderNickname(rs.getString("sender_nickname"))
				.targetNickname(rs.getString("target_nickname"))
				.build();
	}
}
