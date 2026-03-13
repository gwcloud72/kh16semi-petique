package com.spring.semi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.semi.dao.AnimalDao;
import com.spring.semi.dao.BoardDao;
import com.spring.semi.dao.BoardLikeDao;
import com.spring.semi.dao.LevelUpdateDao;
import com.spring.semi.dao.MailDao;
import com.spring.semi.dao.MediaDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.AnimalDto;
import com.spring.semi.dto.MailDto;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.vo.LevelUpdateVO;


/**
 * MemberService - 비즈니스 로직을 담당하는 서비스.
 */
@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private MediaDao mediaDao;
    @Autowired
    private BoardLikeDao boardLikeDao;
    @Autowired
    private BoardDao boardDao;
    @Autowired
    private AnimalDao animalDao;
    @Autowired
    private MailDao mailDao;
    @Autowired
    private LevelUpdateDao levelUpdateDao;


    @Transactional
    public boolean deleteMember(String memberId, String memberPw) {
    	MemberDto memberDto = memberDao.selectOne(memberId);

    	if(memberDto.getMemberPw().equals(memberPw) == false) return false;

    	memberDao.delete(memberId);

    	try {
    		int mediaNo = memberDao.findMediaNo(memberId);
    		mediaDao.delete(mediaNo);
    	} catch (Exception e) {}

    	List<AnimalDto> animalList = animalDao.selectList(memberId);
    	for(AnimalDto dto : animalList) {
    		animalDao.delete(dto.getAnimalNo());
    		try {
    			int mediaNo = animalDao.findMediaNo(dto.getAnimalNo());
    			mediaDao.delete(mediaNo);
    		} catch (Exception e) {}
    	}

    	List<MailDto> mailList = mailDao.selectList(memberId);
    	for(MailDto dto : mailList) {
    		mailDao.delete(dto.getMailNo());
    	}


    	List<Integer> board_like_list = boardLikeDao.selectListByMemberId(memberId);
    	for(int like : board_like_list) {
    		boardDao.updateBoardLike(like);
    	}

    	return true;
    }

    public List<LevelUpdateVO> getMembersForLevelUpdate() {
        return levelUpdateDao.selectMembersForLevelUpdate();
    }

    public int updateMemberLevels() {
        return levelUpdateDao.updateMemberLevels();
    }
}
