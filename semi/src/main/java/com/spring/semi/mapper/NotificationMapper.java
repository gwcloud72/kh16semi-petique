package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.NotificationDto;


@Component
public class NotificationMapper implements RowMapper<NotificationDto> {

    @Override
    public NotificationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return NotificationDto.builder()
                .notiNo(rs.getInt("noti_no"))
                .memberId(rs.getString("member_id"))
                .notiType(rs.getString("noti_type"))
                .notiMessage(rs.getString("noti_message"))
                .notiUrl(rs.getString("noti_url"))
                .notiRead(rs.getString("noti_read"))
                .notiWtime(rs.getTimestamp("noti_wtime"))
                .build();
    }
}
