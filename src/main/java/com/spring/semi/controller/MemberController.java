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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.semi.dao.AnimalDao;
import com.spring.semi.dao.BoardDao;
import com.spring.semi.dao.AdoptionApplyDao;
import com.spring.semi.dao.MemberPointHistoryDao;
import com.spring.semi.dao.NotificationDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.AnimalDto;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.dto.MemberPointHistoryDto;
import com.spring.semi.dto.NotificationDto;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.service.EmailService;
import com.spring.semi.service.MediaService;
import com.spring.semi.service.MemberService;
import com.spring.semi.service.PointService;
import com.spring.semi.vo.MemberBoardListVO;
import com.spring.semi.vo.AdoptionApprovalAdminVO;
import com.spring.semi.vo.PageVO;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import com.spring.semi.util.DummyAvatar;


/**
 * MemberController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberDao memberDao;
	@Autowired
	private MediaService mediaService;
	@Autowired
	private AnimalDao animalDao;
	@Autowired
	private EmailService emailService;
	@Autowired
	private BoardDao boardDao;
	@Autowired
	private MemberService memberService;
	@Autowired
	private AdoptionApplyDao adoptionApplyDao;
	@Autowired
	private MemberPointHistoryDao memberPointHistoryDao;
	@Autowired
	private NotificationDao notificationDao;
	@Autowired
	private PointService pointService;


	@GetMapping("/join")
	public String join() {
		return "/WEB-INF/views/member/join.jsp";
	}

	@PostMapping("/join")
	public String join(
			@ModelAttribute MemberDto memberDto,
			@RequestParam MultipartFile media
			) throws IllegalStateException, IOException {
		memberDao.insert(memberDto);
		if(media.isEmpty() == false) {
			int media_no = mediaService.save(media);
			memberDao.connect(memberDto.getMemberId(), media_no);
		}

		return "redirect:joinFinish";
	}

	@GetMapping("/joinFinish")
	public String joinFinish() {
		return "/WEB-INF/views/member/joinFinish.jsp";
	}
	@GetMapping("/login")
	public String login() {
		return "/WEB-INF/views/member/login.jsp";
	}

	@PostMapping("/login")
	public String login(
			@ModelAttribute MemberDto memberDto,
			HttpSession session
			) {
		MemberDto findDto = memberDao.selectOne(memberDto.getMemberId());
		if(findDto == null) return "redirect:login?error";
		if(findDto.getMemberPw().equals(memberDto.getMemberPw()) == false) return "redirect:login?error";

		session.setAttribute("loginId", findDto.getMemberId());
		session.setAttribute("loginLevel", findDto.getMemberLevel());
		memberDao.updateForLogin(findDto.getMemberId());

		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(
			HttpSession session
			) {
		session.removeAttribute("loginId");
		session.removeAttribute("loginLevel");

		return "redirect:/";
	}

	@GetMapping("/edit")
	public String edit(
			HttpSession session,
			Model model
			) {
		String loginId = (String) session.getAttribute("loginId");
		MemberDto memberDto = memberDao.selectOne(loginId);
		List<AnimalDto> animalList = animalDao.selectList(loginId);
		model.addAttribute("animalList", animalList);
		model.addAttribute("memberDto", memberDto);
		return "/WEB-INF/views/member/edit.jsp";
	}

	@PostMapping("/edit")
	public String edit(
			HttpSession session,
			@ModelAttribute MemberDto memberDto
			) throws IllegalStateException, IOException {
		String loginId = (String) session.getAttribute("loginId");
		MemberDto originDto = memberDao.selectOne(loginId);
		if(originDto.getMemberPw().equals(memberDto.getMemberPw()) == false) return "redirect:edit?error";


		memberDto.setMemberId(loginId);
		memberDao.updateForUser(memberDto);

		return "redirect:mypage";

	}

	@GetMapping("/password")
	public String password() {
		return "/WEB-INF/views/member/password.jsp";
	}

	@PostMapping("/password")
	public String passwrod(
			HttpSession session,
			@RequestParam(name = "change_pw") String changePw,
			@RequestParam(name = "current_pw") String memberPw
			) {
		String loginId = (String) session.getAttribute("loginId");
		MemberDto findDto = memberDao.selectOne(loginId);
		if(memberPw.equals(findDto.getMemberPw()) == false) return "redirect:password?error";
		memberDao.updateForUserPassword(changePw, findDto.getMemberId());

		return "redirect:mypage";
	}

	@GetMapping("/mypage")
	public String mypage(
			Model model,
			HttpSession session,
			@ModelAttribute("pageVO") PageVO pageVO,
			@RequestParam(required = false, defaultValue = "write") String tab
			) {
		String loginId = (String) session.getAttribute("loginId");
		if (loginId == null) return "redirect:login";

		if (pageVO.getKeyword() != null && pageVO.getKeyword().isBlank()) {
			pageVO.setKeyword(null);
			pageVO.setColumn(null);
		}

		MemberDto memberDto = memberDao.selectOne(loginId);
		model.addAttribute("memberDto", memberDto);
		model.addAttribute("tab", tab);

		PageVO emptyVO = new PageVO();
		int animalCount = animalDao.countByMaster(emptyVO, loginId);
		int writeCount = boardDao.countByMemberId(emptyVO, loginId, false);
		int deletedCount = boardDao.countByMemberId(emptyVO, loginId, true);
		int pointCount = memberPointHistoryDao.countByMemberId(loginId);
		int applyCount = adoptionApplyDao.countLatestByApplicantId(loginId);
		int notiCount = 0;
		try {
			notiCount = notificationDao.countUnreadByMemberId(loginId);
		} catch (Exception e) {
			notiCount = 0;
		}
		model.addAttribute("animalCount", animalCount);
		model.addAttribute("writeCount", writeCount);
		model.addAttribute("deletedCount", deletedCount);
		model.addAttribute("pointCount", pointCount);
		model.addAttribute("applyCount", applyCount);
		model.addAttribute("notiCount", notiCount);

		if ("animals".equals(tab)) {
			pageVO.setSize(8);
			pageVO.setDataCount(animalDao.countByMaster(pageVO, loginId));
			pageVO.fixPageRange();
			List<AnimalDto> animalList = animalDao.selectListByMasterForPaging(pageVO, loginId);
			model.addAttribute("animalList", animalList);
		}
		else if ("deleted".equals(tab)) {
			pageVO.setSize(10);
			pageVO.setDataCount(boardDao.countByMemberId(pageVO, loginId, true));
			pageVO.fixPageRange();
			List<MemberBoardListVO> boardList = boardDao.selectByMemberIdWithPaging(pageVO, loginId, true);
			model.addAttribute("boardList", boardList);
		}
		else if ("applies".equals(tab)) {
			pageVO.setSize(10);
			pageVO.setColumn(null);
			pageVO.setKeyword(null);
			pageVO.setDataCount(adoptionApplyDao.countLatestByApplicantId(loginId));
			pageVO.fixPageRange();
			List<AdoptionApprovalAdminVO> applyList = adoptionApplyDao.selectLatestByApplicantIdWithPaging(pageVO, loginId);
			model.addAttribute("applyList", applyList);
			model.addAttribute("tab", "applies");
		}
		else if ("points".equals(tab)) {
			pageVO.setSize(10);
			pageVO.setColumn(null);
			pageVO.setKeyword(null);
			pageVO.setDataCount(memberPointHistoryDao.countByMemberId(loginId));
			pageVO.fixPageRange();
			List<MemberPointHistoryDto> pointHistoryList = memberPointHistoryDao.selectListByMemberId(loginId, pageVO);
			model.addAttribute("pointHistoryList", pointHistoryList);
			model.addAttribute("tab", "points");
		}
		else if ("noti".equals(tab)) {
			pageVO.setSize(10);
			pageVO.setColumn(null);
			pageVO.setKeyword(null);
			try {
				pageVO.setDataCount(notificationDao.countByMemberId(loginId));
			} catch (Exception e) {
				pageVO.setDataCount(0);
			}
			pageVO.fixPageRange();
			List<NotificationDto> notiList;
			try {
				notiList = notificationDao.selectListByMemberId(loginId, pageVO);
			} catch (Exception e) {
				notiList = List.of();
			}
			model.addAttribute("notiList", notiList);
			model.addAttribute("tab", "noti");
		}

		else {
			pageVO.setSize(10);
			pageVO.setDataCount(boardDao.countByMemberId(pageVO, loginId, false));
			pageVO.fixPageRange();
			List<MemberBoardListVO> boardList = boardDao.selectByMemberIdWithPaging(pageVO, loginId, false);
			model.addAttribute("boardList", boardList);
			model.addAttribute("tab", "write");
		}

		model.addAttribute("pageVO", pageVO);
		return "/WEB-INF/views/member/mypage.jsp";
	}

	@GetMapping("/drop")
	public String drop() {
		return "/WEB-INF/views/member/drop.jsp";
	}

	@PostMapping("/drop")
	public String drop(
			HttpSession session,
			@RequestParam String memberPw
			) {
		String loginId = (String) session.getAttribute("loginId");
	    boolean deleted = memberService.deleteMember(loginId, memberPw);

	    if (!deleted) {
	        return "redirect:drop?error";
	    }
		session.removeAttribute("loginId");
		session.removeAttribute("loginLevel");
		return "/WEB-INF/views/member/thankyou.jsp";
	}

	@GetMapping("/profile")
	public String profile(
			@RequestParam String member_id
			) {
		try {
			int media_no = memberDao.findMediaNo(member_id);
			return "redirect:/media/download?mediaNo=" + media_no;
		} catch(Exception e) {
			return "redirect:" + DummyAvatar.path(member_id);
		}
	}

	@GetMapping("/detail")
	public String detail(
			Model model,
			@RequestParam(required = false) String memberNickname,
			@RequestParam(required = false) String memberId,
			@ModelAttribute("pageVO") PageVO pageVO,
			@RequestParam(required = false, defaultValue = "animals") String tab
			) {
		MemberDto findDto = null;
		if (memberNickname != null && !memberNickname.isBlank()) {
			findDto = memberDao.selectForNickname(memberNickname);
		}
		else if (memberId != null && !memberId.isBlank()) {
			findDto = memberDao.selectOne(memberId);
		}
		if (findDto == null) throw new TargetNotfoundException("존재하지않는 회원");

		if (pageVO.getKeyword() != null && pageVO.getKeyword().isBlank()) {
			pageVO.setKeyword(null);
			pageVO.setColumn(null);
		}

		model.addAttribute("findDto", findDto);
		model.addAttribute("tab", tab);

		PageVO emptyVO = new PageVO();
		int animalCount = animalDao.countByMaster(emptyVO, findDto.getMemberId());
		int postCount = boardDao.countByMemberId(emptyVO, findDto.getMemberId(), false);
		model.addAttribute("animalCount", animalCount);
		model.addAttribute("postCount", postCount);

		if ("posts".equals(tab)) {
			pageVO.setSize(10);
			pageVO.setDataCount(boardDao.countByMemberId(pageVO, findDto.getMemberId(), false));
			pageVO.fixPageRange();
			List<MemberBoardListVO> boardList = boardDao.selectByMemberIdWithPaging(pageVO, findDto.getMemberId(), false);
			model.addAttribute("boardList", boardList);
			model.addAttribute("tab", "posts");
		}
		else {
			pageVO.setSize(8);
			pageVO.setDataCount(animalDao.countByMaster(pageVO, findDto.getMemberId()));
			pageVO.fixPageRange();
			List<AnimalDto> animalList = animalDao.selectListByMasterForPaging(pageVO, findDto.getMemberId());
			model.addAttribute("animalList", animalList);
			model.addAttribute("tab", "animals");
		}

		model.addAttribute("pageVO", pageVO);
		return "/WEB-INF/views/member/detail.jsp";
	}

	@GetMapping("/findId")
	public String findId() {
		return "/WEB-INF/views/member/findId.jsp";
	}

	@PostMapping("/findId")
	public String findId(
			@RequestParam String memberEmail
			) throws MessagingException, IOException {
		MemberDto findDto = memberDao.selectForEmail(memberEmail);
		if(findDto == null) throw new TargetNotfoundException("해당 이메일이 등록된 회원이 없습니다.");
		emailService.sendEmailForFindId(findDto);

		return "redirect:findResult";
	}

	@GetMapping("/findResult")
	public String findIdResult() {
		return "/WEB-INF/views/member/findResult.jsp";
	}

	@GetMapping("/findPw")
	public String findPw() {
		return "/WEB-INF/views/member/findPw.jsp";
	}

	@PostMapping("/findPw")
	public String findPw(
			@RequestParam String memberEmail
			) throws MessagingException, IOException {
		MemberDto findDto = memberDao.selectForEmail(memberEmail);
		if(findDto == null) throw new TargetNotfoundException("해당 이메일이 등록된 회원이 없습니다.");

		emailService.sendEmailForFindPw(findDto);

		return "redirect:findResult";
	}

	@RequestMapping("/donation")
	public String donation(@RequestParam(name = "rewardType", required = false) String rewardType,
	                       Model model,
	                       HttpSession session) {
	    String loginId = (String) session.getAttribute("loginId");

	    if (loginId == null) {
	        return "redirect:/member/join";
	    }

	    MemberDto memberDto = memberDao.selectOne(loginId);


	    model.addAttribute("memberDto", memberDto);
	    model.addAttribute("rewardType", rewardType);

	    return "/WEB-INF/views/member/donation.jsp";
	}

	@GetMapping("/usePoint")
	@ResponseBody
	public String usePoint(HttpSession session) {

		String loginId = (String) session.getAttribute("loginId");
		if(loginId != null) pointService.donateAll(loginId);

		return "success";
	}


}
