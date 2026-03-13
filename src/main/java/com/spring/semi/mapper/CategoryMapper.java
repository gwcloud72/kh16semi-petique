package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.CategoryDto;


/**
 * CategoryMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class CategoryMapper implements RowMapper<CategoryDto> {

	@Override
	public CategoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return CategoryDto.builder()
				.categoryNo(rs.getInt("category_no"))
				.categoryName(rs.getString("category_name"))
				.build();
	}
}
