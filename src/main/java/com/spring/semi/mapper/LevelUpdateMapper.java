package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.LevelUpdateVO;


/**
 * LevelUpdateMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class LevelUpdateMapper implements RowMapper<LevelUpdateVO> {
    @Override
    public LevelUpdateVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return LevelUpdateVO.builder()
                .memberId(rs.getString("member_id"))
                .memberUsedPoint(rs.getInt("member_used_point"))
                .memberLevel(rs.getInt("member_level"))
                .levelNo(rs.getInt("level_no"))
                .minPoint(rs.getInt("min_point"))
                .maxPoint(rs.getInt("max_point"))
                .build();
    }
}
