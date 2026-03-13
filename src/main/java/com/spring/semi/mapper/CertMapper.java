package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.CertDto;


/**
 * CertMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class CertMapper implements RowMapper<CertDto>{

	@Override
	public CertDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		CertDto certDto = new CertDto();
		return CertDto.builder()
				.certEmail(rs.getString("cert_email"))
				.certNumber(rs.getString("cert_number"))
				.certTime(rs.getTimestamp("cert_time"))
				.build();

	}

}
