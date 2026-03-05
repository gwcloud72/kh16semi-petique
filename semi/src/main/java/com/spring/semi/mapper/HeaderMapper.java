package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.HeaderDto;


/**
 * HeaderMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class HeaderMapper implements RowMapper<HeaderDto> {

	@Override
	public HeaderDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return HeaderDto.builder()
				.headerNo(rs.getInt("header_no"))
				.headerName(rs.getString("header_name"))
				.build();
	}
}
