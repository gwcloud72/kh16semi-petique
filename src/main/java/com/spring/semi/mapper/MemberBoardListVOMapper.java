package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.vo.MemberBoardListVO;


/**
 * MemberBoardListVOMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class MemberBoardListVOMapper implements RowMapper<MemberBoardListVO> {
	@Override
	public MemberBoardListVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		MemberBoardListVO vo = new MemberBoardListVO();
		vo.setBoardNo(rs.getInt("board_no"));
		vo.setBoardCategoryNo(rs.getInt("board_category_no"));
		vo.setCategoryName(rs.getString("category_name"));
		vo.setCategoryKey(rs.getString("category_key"));
		vo.setBoardTitle(rs.getString("board_title"));
		vo.setBoardWtime(rs.getTimestamp("board_wtime"));
		vo.setBoardView(rs.getInt("board_view"));
		return vo;
	}
}
