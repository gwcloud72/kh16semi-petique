package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.NotificationDto;
import com.spring.semi.mapper.NotificationMapper;
import com.spring.semi.vo.PageVO;


@Repository
public class NotificationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NotificationMapper notificationMapper;

    public int sequence() {
        String sql = "select notification_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    public void insert(NotificationDto dto) {
        String sql = "insert into notification(noti_no, member_id, noti_type, noti_message, noti_url, noti_read, noti_wtime) "
                + "values(?, ?, ?, ?, ?, 'N', systimestamp)";
        Object[] params = {
                dto.getNotiNo(),
                dto.getMemberId(),
                dto.getNotiType(),
                dto.getNotiMessage(),
                dto.getNotiUrl()
        };
        jdbcTemplate.update(sql, params);
    }

    public NotificationDto selectOne(int notiNo) {
        String sql = "select * from notification where noti_no = ?";
        List<NotificationDto> list = jdbcTemplate.query(sql, notificationMapper, notiNo);
        return list.isEmpty() ? null : list.get(0);
    }

    public int countByMemberId(String memberId) {
        String sql = "select count(*) from notification where member_id = ?";
        return jdbcTemplate.queryForObject(sql, int.class, memberId);
    }

    public int countUnreadByMemberId(String memberId) {
        String sql = "select count(*) from notification where member_id = ? and noti_read = 'N'";
        return jdbcTemplate.queryForObject(sql, int.class, memberId);
    }

    public List<NotificationDto> selectListByMemberId(String memberId, PageVO pageVO) {
        String sql = "select * from ("
                + "select rownum rn, TMP.* from ("
                + "select * from notification where member_id = ? order by noti_wtime desc, noti_no desc"
                + ") TMP"
                + ") where rn between ? and ?";
        Object[] params = { memberId, pageVO.getBegin(), pageVO.getEnd() };
        return jdbcTemplate.query(sql, notificationMapper, params);
    }

    public boolean readOne(int notiNo, String memberId) {
        String sql = "update notification set noti_read='Y' where noti_no = ? and member_id = ?";
        return jdbcTemplate.update(sql, notiNo, memberId) > 0;
    }

    public int readAll(String memberId) {
        String sql = "update notification set noti_read='Y' where member_id = ? and noti_read='N'";
        return jdbcTemplate.update(sql, memberId);
    }

    public boolean deleteOne(int notiNo, String memberId) {
        String sql = "delete from notification where noti_no = ? and member_id = ?";
        return jdbcTemplate.update(sql, notiNo, memberId) > 0;
    }

    public int deleteAllByMemberId(String memberId) {
        String sql = "delete from notification where member_id = ?";
        return jdbcTemplate.update(sql, memberId);
    }
}
