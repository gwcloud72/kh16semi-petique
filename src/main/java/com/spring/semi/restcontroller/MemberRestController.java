package com.spring.semi.restcontroller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.semi.dao.CertDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.BoardDto;
import com.spring.semi.dto.CertDto;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.service.EmailService;
import com.spring.semi.service.MediaService;

import jakarta.servlet.http.HttpSession;


/**
 * MemberRestController - 비동기/REST 요청을 처리하는 컨트롤러.
 */
@RestController
@RequestMapping("/rest/member")
public class MemberRestController {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private EmailService emailService;
	@Autowired
	private CertDao certDao;
	@Autowired
	private MediaService mediaService;


	@PostMapping("/certSend")
	public boolean certSend(@RequestParam String certEmail) {
		MemberDto findEmail = memberDao.selectForEmail(certEmail);
		if(findEmail == null) {
			emailService.sendCertNumber(certEmail);
			return true;
		}
		return false;
	}

	@PostMapping("/findSend")
	public boolean findSend(@RequestParam String memberEmail) {
		MemberDto findEmail = memberDao.selectForEmail(memberEmail);
		if(findEmail == null) {
			return false;
		}
		return true;
	}


	@PostMapping("/certCheck")
	public boolean certCheck(@ModelAttribute CertDto certDto) {
		CertDto findEmail = certDao.selectOne(certDto.getCertEmail());
		if (findEmail == null)
			return false;

		LocalDateTime current = LocalDateTime.now();
		LocalDateTime sent = findEmail.getCertTime().toLocalDateTime();
		Duration duration = Duration.between(sent, current);
		if (duration.toSeconds() > 300)
			return false;

		boolean isValid = certDto.getCertNumber().trim().equals(findEmail.getCertNumber().trim());
		if (isValid == false)
			return false;

		certDao.delete(certDto.getCertEmail());
		return true;
	}

	@PostMapping("/profile")
	public void profile(HttpSession session, @RequestParam MultipartFile media) throws IllegalStateException, IOException {

		String login_id = (String) session.getAttribute("loginId");
		try {
			int media_no = memberDao.findMediaNo(login_id);
			mediaService.delete(media_no);
		} catch (Exception e) {}


		if(media.isEmpty() == false) {
			int media_no = mediaService.save(media);
			memberDao.connect(login_id, media_no);
		}
	}

	@PostMapping("/delete")
	public void delete(HttpSession session) {
		String login_id = (String) session.getAttribute("loginId");
		try {
			int media_no = memberDao.findMediaNo(login_id);
			mediaService.delete(media_no);
		} catch (Exception e) {}
	}


	@PostMapping("/checkId")
	public boolean checkId(@RequestParam String memberId) {
		MemberDto findDto = memberDao.selectOne(memberId);
		return findDto != null;
	}


	@PostMapping("/checkNickname") //가입용 닉네임 체크
	public boolean checkNickname(@RequestParam String memberNickname) {
		MemberDto findDto = memberDao.selectForNickname(memberNickname);
		return findDto == null? false : true;
	}


	@PostMapping("/checkDuplication")
	public boolean checkDuplication(
	        @RequestParam String memberId,
	        @RequestParam String memberNickname
	        ) {
	    MemberDto nicknameOwner = memberDao.selectForNickname(memberNickname);
	    if (nicknameOwner == null) return false;
	    return !nicknameOwner.getMemberId().equals(memberId);
	}

	@PostMapping("/login")
	public boolean login(
			@RequestParam String memberId,
			@RequestParam String memberPw,
			HttpSession session
			) {
		MemberDto findDto = memberDao.selectOne(memberId);

		if(findDto == null) return false;

		if(findDto.getMemberPw().equals(memberPw) == false) return false;

		session.setAttribute("loginId", findDto.getMemberId());
		session.setAttribute("loginLevel", findDto.getMemberLevel());

		return true;

	}

}
