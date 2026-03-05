package com.spring.semi.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.semi.dao.BoardDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.vo.BoardListVO;


/**
 * MainRestController - 비동기/REST 요청을 처리하는 컨트롤러.
 */
@CrossOrigin
@RestController
@RequestMapping("rest/main")
public class MainRestController {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private BoardDao boardDao;

	@PostMapping("/ranking")
	public List<MemberDto> ranking() {
		List<MemberDto> result = memberDao.selectListByMemberPoint(1, 10);
		return result;
	}

	@PostMapping("/newboard")
	public List<BoardListVO> newboard() {
		List<BoardListVO> result = boardDao.selectListByWriteTime(1, 8);
		return result;
	}
}
