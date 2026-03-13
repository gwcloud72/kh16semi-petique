package com.spring.semi.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.NoticePinDto;
import com.spring.semi.mapper.NoticePinMapper;


/**
 * NoticePinDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class NoticePinDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NoticePinMapper noticePinMapper;

	public NoticePinDto selectOne(int boardNo) {
		String sql = "select board_no, pin_start, pin_end, pin_order from notice_pin where board_no = ?";
		List<NoticePinDto> list = jdbcTemplate.query(sql, noticePinMapper, boardNo);
		return list.isEmpty() ? null : list.get(0);
	}

	public void merge(int boardNo, Timestamp pinStart, Timestamp pinEnd, int pinOrder) {
		String sql = "merge into notice_pin np "
				+ "using (select ? board_no, ? pin_start, ? pin_end, ? pin_order from dual) src "
				+ "on (np.board_no = src.board_no) "
				+ "when matched then "
				+ "  update set np.pin_start = src.pin_start, np.pin_end = src.pin_end, np.pin_order = src.pin_order "
				+ "when not matched then "
				+ "  insert (board_no, pin_start, pin_end, pin_order) values (src.board_no, src.pin_start, src.pin_end, src.pin_order)";
		jdbcTemplate.update(sql, boardNo, pinStart, pinEnd, pinOrder);
	}

	public boolean delete(int boardNo) {
		String sql = "delete from notice_pin where board_no = ?";
		return jdbcTemplate.update(sql, boardNo) > 0;
	}
}
