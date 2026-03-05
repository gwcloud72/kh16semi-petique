package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


/**
 * ReplyLikeDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class ReplyLikeDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void insert(String memberId, int replyNo) {
        String sql = "insert into reply_like(member_id, reply_no) values(?, ?)";
        Object[] params = {memberId, replyNo};
        jdbcTemplate.update(sql, params);
    }


    public boolean check(String memberId, int replyNo) {
        if (memberId == null) return false;

        String sql = "select count(*) from reply_like where member_id=? and reply_no=?";
        Object[] params = {memberId, replyNo};
        int count = jdbcTemplate.queryForObject(sql, int.class, params);
        return count > 0;
    }


    public boolean delete(String memberId, int replyNo) {
        String sql = "delete from reply_like where member_id=? and reply_no=?";
        Object[] params = {memberId, replyNo};
        return jdbcTemplate.update(sql, params) > 0;
    }


    public int countByReplyNo(int replyNo) {
        String sql = "select count(*) from reply_like where reply_no=?";
        Object[] params = {replyNo};
        return jdbcTemplate.queryForObject(sql, int.class, params);
    }


    public List<Integer> selectListByMemberId(String memberId) {
        String sql = "select reply_no from reply_like where member_id=?";
        Object[] params = {memberId};
        return jdbcTemplate.queryForList(sql, int.class, params);
    }
}
