package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.BoardListVO;


/**
 * BoardListVOMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class BoardListVOMapper implements RowMapper<BoardListVO> {
    @Override
    public BoardListVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        BoardListVO vo = new BoardListVO();
        vo.setBoardNo(rs.getInt("board_no"));
        vo.setBoardTitle(rs.getString("board_title"));
        vo.setCategoryName(rs.getString("category_name"));
        vo.setBoardWtime(rs.getTimestamp("board_wtime"));
        vo.setBoardView(rs.getInt("board_view"));
        return vo;
    }
}
