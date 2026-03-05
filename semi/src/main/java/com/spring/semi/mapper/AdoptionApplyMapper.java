package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.AdoptionApplyDto;


/**
 * AdoptionApplyMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class AdoptionApplyMapper implements RowMapper<AdoptionApplyDto> {

    @Override
    public AdoptionApplyDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AdoptionApplyDto.builder()
                .applyNo(rs.getInt("apply_no"))
                .boardNo(rs.getInt("board_no"))
                .animalNo(rs.getInt("animal_no"))
                .applicantId(rs.getString("applicant_id"))
                .applyContent(rs.getString("apply_content"))
                .applyStatus(rs.getString("apply_status"))
                .applyWtime(rs.getTimestamp("apply_wtime"))
                .applyEtime(rs.getTimestamp("apply_etime"))
                .build();
    }
}
