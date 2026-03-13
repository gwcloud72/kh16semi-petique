package com.spring.semi.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.semi.dao.AnimalDao;
import com.spring.semi.dao.BoardDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.AnimalDto;

import com.spring.semi.dto.MemberDto;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.service.EmailService;
import com.spring.semi.service.MemberService;
import com.spring.semi.vo.BoardListVO;
import com.spring.semi.vo.PageVO;

import jakarta.mail.MessagingException;


/**
 * AdminMemberController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {

	@Autowired
	private MemberDao memberDao;
	@Autowired
	private AnimalDao animalDao;
	@Autowired
	private BoardDao boardDao;
	@Autowired
	private EmailService emailService;
	@Autowired
	private MemberService memberService;

	@GetMapping("/list")
	public String list(Model model, @ModelAttribute PageVO pageVO) {
		pageVO.setDataCount(memberDao.count(pageVO));
		List<MemberDto> memberList = memberDao.selectListForPaging(pageVO);

		model.addAttribute("memberList", memberList);
		model.addAttribute("pageVO", pageVO);

		return "/WEB-INF/views/admin/member/list.jsp";
	}

	@GetMapping("/detail")
	public String detail(Model model, @RequestParam String memberId

	) {
		MemberDto memberDto = memberDao.selectOne(memberId);
		List<AnimalDto> animalList = animalDao.selectList(memberId);
		List<BoardListVO> boardListVO = boardDao.selectByMemberId(memberId);

		model.addAttribute("memberDto", memberDto);
		model.addAttribute("animalList", animalList);
		model.addAttribute("boardListVO", boardListVO);

		return "/WEB-INF/views/admin/member/detail.jsp";
	}

	@GetMapping("/edit")
	public String edit(Model model, @RequestParam String memberId) {
		MemberDto memberDto = memberDao.selectOne(memberId);

		model.addAttribute("memberDto", memberDto);

		return "/WEB-INF/views/admin/member/edit.jsp";
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute MemberDto memberDto) {

		memberDao.updateForAdmin(MemberDto.builder().memberNickname(memberDto.getMemberNickname())
				.memberDescription(memberDto.getMemberDescription()).memberPoint(memberDto.getMemberPoint())
				.memberId(memberDto.getMemberId()).build());

		return "redirect:list";
	}

	@GetMapping("/drop")
	public String drop(@RequestParam String memberId) {
		MemberDto memberDto = memberDao.selectOne(memberId);
		memberService.deleteMember(memberDto.getMemberId(), memberDto.getMemberPw());
		return "redirect:list";
	}

	@GetMapping("/password")
	public String password(@RequestParam String memberEmail) throws MessagingException, IOException {
		MemberDto findDto = memberDao.selectForEmail(memberEmail);
		if (findDto == null)
			throw new TargetNotfoundException("해당 이메일이 등록된 회원이 없습니다.");

		emailService.sendEmailForFindPw(findDto);

		return "redirect:list";
	}

}
