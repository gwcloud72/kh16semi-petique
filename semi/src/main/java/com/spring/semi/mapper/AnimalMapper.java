package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.AnimalDto;


/**
 * AnimalMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class AnimalMapper  implements RowMapper<AnimalDto> {

	@Override
	public AnimalDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return AnimalDto.builder()
				.animalNo(rs.getInt("animal_no"))
				.animalName(rs.getString("animal_name"))
				.animalContent(rs.getString("animal_content"))
				.animalPermission(rs.getString("animal_permission"))
				.animalMaster(rs.getString("animal_master"))
				.build();
	}
}
