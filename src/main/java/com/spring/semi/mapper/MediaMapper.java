package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.MediaDto;


/**
 * MediaMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class MediaMapper implements RowMapper<MediaDto> {

	@Override
	public MediaDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MediaDto.builder()
				.mediaNo(rs.getInt("media_no"))
				.mediaType(rs.getString("media_type"))
				.mediaName(rs.getString("media_name"))
				.mediaWtime(rs.getTimestamp("media_wtime"))
				.mediaSize(rs.getLong("media_size"))
				.build();
	}
}
