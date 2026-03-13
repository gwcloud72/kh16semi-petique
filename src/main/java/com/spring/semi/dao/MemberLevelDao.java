package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.MemberLevelDto;
import com.spring.semi.mapper.MemberLevelMapper;


/**
 * MemberLevelDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class MemberLevelDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberLevelMapper memberLevelMapper;


    public void insert(MemberLevelDto level) {
        String sql = "INSERT INTO member_level_table "
                   + "(level_no, level_name, min_point, max_point, description, badge_image) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                level.getLevelNo(),
                level.getLevelName(),
                level.getMinPoint(),
                level.getMaxPoint(),
                level.getDescription(),
                level.getBadgeImage());
    }


    public List<MemberLevelDto> selectAll() {
        String sql = "SELECT l.level_no, l.level_name, l.min_point, l.max_point, "
                   + "l.description, l.badge_image, "
                   + "(SELECT COUNT(*) FROM member m WHERE m.member_level = l.level_no) AS member_count "
                   + "FROM member_level_table l "
                   + "ORDER BY l.level_no";
        return jdbcTemplate.query(sql, memberLevelMapper);
    }


    public MemberLevelDto selectOne(int levelNo) {
        String sql = "SELECT l.level_no, l.level_name, l.min_point, l.max_point, "
                   + "l.description, l.badge_image, "
                   + "(SELECT COUNT(*) FROM member m WHERE m.member_level = l.level_no) AS member_count "
                   + "FROM member_level_table l "
                   + "WHERE l.level_no = ?";
        return jdbcTemplate.queryForObject(sql, memberLevelMapper, levelNo);
    }


    public void update(MemberLevelDto level) {
        String sql = "UPDATE member_level_table SET "
                   + "level_name = ?, min_point = ?, max_point = ?, description = ?, badge_image = ? "
                   + "WHERE level_no = ?";
        jdbcTemplate.update(sql,
                level.getLevelName(),
                level.getMinPoint(),
                level.getMaxPoint(),
                level.getDescription(),
                level.getBadgeImage(),
                level.getLevelNo());
    }


    public void delete(int levelNo) {
        String sql = "DELETE FROM member_level_table WHERE level_no = ?";
        jdbcTemplate.update(sql, levelNo);
    }


    public int countMembersByLevel(int levelNo) {
        String sql = "SELECT COUNT(*) FROM member WHERE member_level = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, levelNo);
    }
}
