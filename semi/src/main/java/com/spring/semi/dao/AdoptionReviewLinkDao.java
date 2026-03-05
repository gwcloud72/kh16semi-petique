package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class AdoptionReviewLinkDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer findReviewBoardNo(int adoptionBoardNo) {
        String sql = "select review_board_no from adoption_review_link where adoption_board_no = ?";
        List<Integer> list = jdbcTemplate.query(sql, (rs, rn) -> rs.getInt("review_board_no"), adoptionBoardNo);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean insert(int adoptionBoardNo, int reviewBoardNo) {
        String sql = "insert into adoption_review_link(adoption_board_no, review_board_no, link_wtime) values(?, ?, systimestamp)";
        return jdbcTemplate.update(sql, adoptionBoardNo, reviewBoardNo) > 0;
    }

    public boolean deleteByAdoptionBoardNo(int adoptionBoardNo) {
        String sql = "delete from adoption_review_link where adoption_board_no = ?";
        return jdbcTemplate.update(sql, adoptionBoardNo) > 0;
    }
}
