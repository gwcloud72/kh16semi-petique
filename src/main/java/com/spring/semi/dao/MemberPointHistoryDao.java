package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.MemberPointHistoryDto;
import com.spring.semi.mapper.MemberPointHistoryMapper;
import com.spring.semi.vo.PageVO;


/**
 * MemberPointHistoryDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class MemberPointHistoryDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberPointHistoryMapper mapper;

    public int sequence() {
        String sql = "select member_point_history_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    public void insert(MemberPointHistoryDto dto) {
        String sql = "insert into member_point_history("
                + "history_no, member_id, history_amount, history_type, history_memo, history_ref_no"
                + ") values(?, ?, ?, ?, ?, ?)";
        Object[] params = {
                dto.getHistoryNo(),
                dto.getMemberId(),
                dto.getHistoryAmount(),
                dto.getHistoryType(),
                dto.getHistoryMemo(),
                dto.getHistoryRefNo()
        };
        jdbcTemplate.update(sql, params);
    }

    public int countByMemberId(String memberId) {
        String sql = "select count(*) from member_point_history where member_id = ?";
        Object[] params = { memberId };
        return jdbcTemplate.queryForObject(sql, int.class, params);
    }

    public List<MemberPointHistoryDto> selectListByMemberId(String memberId, PageVO pageVO) {
        String sql = "select * from ("
                + "select rownum rn, TMP.* from ("
                + "select * from member_point_history "
                + "where member_id = ? "
                + "order by history_no desc"
                + ") TMP"
                + ") where rn between ? and ?";
        Object[] params = { memberId, pageVO.getBegin(), pageVO.getEnd() };
        return jdbcTemplate.query(sql, mapper, params);
    }
}
