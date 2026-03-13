package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


/**
 * BoardLikeDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class BoardLikeDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;


	public void insert(String memberId, int boardNo) {

		String sql ="insert into board_like(member_id, board_no) values(?, ?)";
		Object[] params = {memberId, boardNo};
		jdbcTemplate.update(sql, params);

	}

	public boolean check(String memberId, int boardNo) {
		if(memberId==null) return false;

		String sql ="select count(*) from board_like where member_id=? and board_no=?";
		Object[] params = {memberId, boardNo};
		int count = jdbcTemplate.queryForObject(sql, int.class, params);
		return count > 0;

	}

	public boolean delete(String memberId, int boardNo) {
		String sql = "delete from board_like where member_id=? and board_no=?";
		Object[] params = {memberId, boardNo};
		return jdbcTemplate.update(sql, params) > 0;
	}

	public int countByBoardNo(int boardNo) {
		String sql = "select count(*) from board_like where board_no=?";
		Object[] params = {boardNo};
		return jdbcTemplate.queryForObject(sql, int.class, params);

	}

	public List<Integer> selectListByMemberId(String memberId){
		String sql = "select board_no from board_like where member_id=?";
		Object[] params = {memberId};
		return jdbcTemplate.queryForList(sql, int.class, params);

	}

}
