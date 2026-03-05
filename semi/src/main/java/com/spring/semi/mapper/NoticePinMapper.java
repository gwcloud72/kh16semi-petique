package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.NoticePinDto;


/**
 * NoticePinMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class NoticePinMapper implements RowMapper<NoticePinDto> {
	@Override
	public NoticePinDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return NoticePinDto.builder()
				.boardNo(rs.getInt("board_no"))
				.pinStart(rs.getTimestamp("pin_start"))
				.pinEnd(rs.getTimestamp("pin_end"))
				.pinOrder(rs.getInt("pin_order"))
				.build();
	}
}
