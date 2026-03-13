package com.spring.semi.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.semi.dao.MailDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.MemberDto;

import jakarta.servlet.http.HttpSession;


/**
 * MailRestController - 비동기/REST 요청을 처리하는 컨트롤러.
 */
@RestController
@RequestMapping("/rest/mail")
public class MailRestController {

	@Autowired
	private MemberDao memberDao;

	@PostMapping("/checkMember")
	public int checkMember(
			@RequestParam String memberNickname,
			HttpSession session
			) {
		String loginId = (String) session.getAttribute("loginId");
		MemberDto memberDto = memberDao.selectForNickname(memberNickname);
		if(memberDto == null) return 0;
		if(memberDto.getMemberId().equals(loginId)) return -1;

		return 1;
	}

}
