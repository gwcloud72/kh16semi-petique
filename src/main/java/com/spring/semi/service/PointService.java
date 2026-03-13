package com.spring.semi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.semi.dao.MemberDao;
import com.spring.semi.dao.MemberPointHistoryDao;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.dto.MemberPointHistoryDto;


/**
 * PointService - 비즈니스 로직을 담당하는 서비스.
 */
@Service
public class PointService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private MemberPointHistoryDao historyDao;

    @Transactional
    public void record(String memberId, int delta, String type, String memo, Integer refNo) {
        if (memberId == null || memberId.isBlank()) return;
        memberDao.addPoint(memberId, delta);
        int seq = historyDao.sequence();
        historyDao.insert(MemberPointHistoryDto.builder()
                .historyNo(seq)
                .memberId(memberId)
                .historyAmount(delta)
                .historyType(type)
                .historyMemo(memo)
                .historyRefNo(refNo)
                .build());
    }

    @Transactional
    public int donateAll(String memberId) {
        if (memberId == null || memberId.isBlank()) return 0;
        MemberDto member = memberDao.selectOne(memberId);
        if (member == null) return 0;
        int point = member.getMemberPoint();
        if (point <= 0) return 0;

        memberDao.usePoint(memberId);

        int seq = historyDao.sequence();
        historyDao.insert(MemberPointHistoryDto.builder()
                .historyNo(seq)
                .memberId(memberId)
                .historyAmount(-point)
                .historyType("DONATE")
                .historyMemo("펫콩 기부")
                .historyRefNo(null)
                .build());

        return point;
    }
}
