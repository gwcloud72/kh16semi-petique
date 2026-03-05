package com.spring.semi.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.semi.dao.MailDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.MailDto;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.error.NeedPermissionException;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.service.MailService;
import com.spring.semi.vo.MailDetailVO;
import com.spring.semi.vo.MailVO;
import com.spring.semi.vo.PageVO;

import jakarta.servlet.http.HttpSession;


/**
 * MailController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/mail")
public class MailController {
	@Autowired
	private MailDao mailDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private MailService mailService;

	@GetMapping("/send")
	public String send(
			HttpSession session,
			Model model
			) {
		String loginId = (String) session.getAttribute("loginId");
		MemberDto memberDto = memberDao.selectOne(loginId);
		if(memberDto == null) throw new TargetNotfoundException("존재하지않는 회원");
		model.addAttribute("memberDto", memberDto);

		return "/WEB-INF/views/mail/send.jsp";
	}

	@PostMapping("/send")
	public String send(
			HttpSession session,
			@ModelAttribute MailDto mailDto,
			@RequestParam String memberNickname
			) {
		String loginId = (String) session.getAttribute("loginId");
		MemberDto senderDto = memberDao.selectOne(loginId);
		if(senderDto == null) throw new NeedPermissionException("잘못된 접근");
		MemberDto targetDto = memberDao.selectForNickname(memberNickname);
		if(targetDto == null) throw new TargetNotfoundException("존재하지않는 회원");

		mailDto.setMailSender(senderDto.getMemberId());
		mailDto.setMailTarget(targetDto.getMemberId());

		mailService.sendMail(mailDto);


		return "redirect:list/send";
	}

	@GetMapping("/list/{type}")
	public String list(
			HttpSession session,
			Model model,
			@ModelAttribute PageVO pageVO,
			@PathVariable String type
			) {

		String loginId = (String) session.getAttribute("loginId");
		MemberDto memberDto = memberDao.selectOne(loginId);
		if(memberDto == null) throw new TargetNotfoundException("존재하지 않는 회원");
		List<MailVO> mailList = new ArrayList<>();
		if(type.equals("send")) {
			mailList = mailDao.selectListForSenderWithPaging(pageVO, memberDto.getMemberId());
		} else if(type.equals("receive")){
			mailList = mailDao.selectListForTargetWithPaging(pageVO, memberDto.getMemberId());
		} else {
			throw new TargetNotfoundException("존재하지않는 경로입니다.");
		}
		model.addAttribute("mailList", mailList);
		pageVO.setDataCount(mailDao.count(pageVO, memberDto.getMemberId()));
		model.addAttribute("pageVO", pageVO);
		model.addAttribute("type", type);

		return "/WEB-INF/views/mail/list.jsp";
	}

	@GetMapping("/detail")
	public String detail(
			HttpSession session,
			Model model,
			@RequestParam int mailNo
			) {
		String loginId = (String) session.getAttribute("loginId");
		MemberDto memberDto = memberDao.selectOne(loginId);
		if(memberDto == null) throw new TargetNotfoundException("존재하지 않는 회원");
		MailDetailVO mailDto = mailDao.selectForDetail(mailNo);
		boolean isSender = mailDto.getMailSender().equals(memberDto.getMemberId());
		boolean isTarget = mailDto.getMailTarget().equals(memberDto.getMemberId());
		if(isSender == false && isTarget == false) throw new NeedPermissionException("권한 부족");
		model.addAttribute("mailDto", mailDto);
		return "/WEB-INF/views/mail/detail.jsp";
	}

	@PostMapping("/delete")
	public String delete(
			@RequestParam int mailNo,
			HttpSession session
			) {
		String loginId = (String) session.getAttribute("loginId");
		MailDto mailDto = mailDao.selectOne(mailNo);
		if(mailDto == null) throw new TargetNotfoundException("없어요");
		if(mailDto.getMailOwner().equals(loginId) == false) throw new NeedPermissionException("권한 부족");
		mailDao.delete(mailNo);
		return "redirect:list/receive";
	}


}
