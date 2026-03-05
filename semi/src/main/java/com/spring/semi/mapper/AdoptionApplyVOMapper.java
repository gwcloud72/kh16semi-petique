package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.AdoptionApplyVO;


/**
 * AdoptionApplyVOMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class AdoptionApplyVOMapper implements RowMapper<AdoptionApplyVO> {

    @Override
    public AdoptionApplyVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AdoptionApplyVO.builder()
                .applyNo(rs.getInt("apply_no"))
                .boardNo(rs.getInt("board_no"))
                .animalNo(rs.getInt("animal_no"))
                .applicantId(rs.getString("applicant_id"))
                .applicantNickname(rs.getString("member_nickname"))
                .badgeImage(rs.getString("badge_image"))
                .levelName(rs.getString("level_name"))
                .applyContent(rs.getString("apply_content"))
                .applyStatus(rs.getString("apply_status"))
                .applyWtime(rs.getTimestamp("apply_wtime"))
                .applyEtime(rs.getTimestamp("apply_etime"))
                .build();
    }
}
