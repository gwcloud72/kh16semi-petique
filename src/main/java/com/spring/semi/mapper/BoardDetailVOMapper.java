package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.BoardDetailVO;


/**
 * BoardDetailVOMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class BoardDetailVOMapper implements RowMapper<BoardDetailVO> {

    @Override
    public BoardDetailVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return BoardDetailVO.builder()
                .boardCategoryNo(rs.getInt("board_category_no"))
                .boardNo(rs.getInt("board_no"))
                .boardTitle(rs.getString("board_title"))
                .boardWriter(rs.getString("board_writer"))
                .boardContent(rs.getString("board_content"))
                .boardWtime(rs.getTimestamp("board_wtime"))
                .boardEtime(rs.getTimestamp("board_etime"))
                .boardLike(rs.getInt("board_like"))
                .boardView(rs.getInt("board_view"))
                .boardReply(rs.getInt("board_reply"))
                .deleted(rs.getInt("deleted"))
                .typeHeaderName(rs.getString("type_header_name"))
                .animalHeaderName(rs.getString("animal_header_name"))
                .boardScore(rs.getInt("board_score"))
                .memberNickname(rs.getString("member_nickname"))
                .badgeImage(rs.getString("badge_image"))
                .levelName(rs.getString("level_name"))
                .build();
    }
}
