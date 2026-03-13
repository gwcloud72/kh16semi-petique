package com.spring.semi.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * MemberPointHistoryDto - 요청/응답 데이터 전달 객체.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPointHistoryDto {
    private int historyNo;
    private String memberId;
    private int historyAmount;
    private String historyType;
    private String historyMemo;
    private Integer historyRefNo;
    private Timestamp historyWtime;
}
