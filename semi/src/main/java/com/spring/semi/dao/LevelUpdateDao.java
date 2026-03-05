package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.mapper.LevelUpdateMapper;
import com.spring.semi.vo.LevelUpdateVO;


/**
 * LevelUpdateDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class LevelUpdateDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LevelUpdateMapper levelUpdateMapper;

    public List<LevelUpdateVO> selectMembersForLevelUpdate() {
        String sql = "select m.member_id, m.member_used_point, m.member_level, "
                + "l.level_no, l.min_point, l.max_point "
                + "from member m "
                + "join member_level_table l "
                + "on m.member_used_point between l.min_point and l.max_point";
        return jdbcTemplate.query(sql, levelUpdateMapper);
    }

    public int updateMemberLevels() {
        String sql = "update member m " +
                     "set member_level = (" +
                     "    select l.level_no " +
                     "    from member_level_table l " +
                     "    where m.member_used_point between l.min_point and l.max_point" +
                     ") " +
                     "where exists (" +
                     "    select 1 " +
                     "    from member_level_table l " +
                     "    where m.member_used_point between l.min_point and l.max_point" +
                     ")";
        return jdbcTemplate.update(sql);
    }

}
