package com.spring.semi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.spring.semi.dto.MemberPointHistoryDto;


/**
 * MemberPointHistoryMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component
public class MemberPointHistoryMapper implements RowMapper<MemberPointHistoryDto> {

    @Override
    public MemberPointHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object refObj = rs.getObject("history_ref_no");
        Integer refNo = null;
        if (refObj != null) {
            if (refObj instanceof Number) {
                refNo = ((Number) refObj).intValue();
            } else {
                refNo = Integer.valueOf(refObj.toString());
            }
        }

        return MemberPointHistoryDto.builder()
                .historyNo(rs.getInt("history_no"))
                .memberId(rs.getString("member_id"))
                .historyAmount(rs.getInt("history_amount"))
                .historyType(rs.getString("history_type"))
                .historyMemo(rs.getString("history_memo"))
                .historyRefNo(refNo)
                .historyWtime(rs.getTimestamp("history_wtime"))
                .build();
    }
}
