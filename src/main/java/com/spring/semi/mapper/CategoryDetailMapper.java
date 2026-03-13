package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.CategoryDetailVO;


/**
 * CategoryDetailMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class CategoryDetailMapper implements RowMapper<CategoryDetailVO> {
    @Override
    public CategoryDetailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryDetailVO categoryDetailVO = new CategoryDetailVO();
        categoryDetailVO.setCategoryNo(rs.getInt("category_no"));
        categoryDetailVO.setCategoryName(rs.getString("category_name"));
        categoryDetailVO.setBoardCount(rs.getInt("board_count"));


        return categoryDetailVO;
    }
}
