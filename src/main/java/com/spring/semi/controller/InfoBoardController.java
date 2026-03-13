package com.spring.semi.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.semi.dao.BoardDao;
import com.spring.semi.dao.CategoryDao;
import com.spring.semi.dao.HeaderDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.BoardDto;
import com.spring.semi.dto.CategoryDto;
import com.spring.semi.dto.HeaderDto;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.error.NeedPermissionException;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.service.MediaService;
import com.spring.semi.service.NoticeService;
import com.spring.semi.vo.BoardDetailVO;
import com.spring.semi.vo.PageVO;
import com.spring.semi.service.MemberService;

import jakarta.servlet.http.HttpSession;


/**
 * InfoBoardController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/board/info")
public class InfoBoardController {
	 private final MediaService mediaService;
	@Autowired
	private BoardDao boardDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private HeaderDao headerDao;
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private MemberService memberService;
	@Autowired
	private NoticeService noticeService;

	InfoBoardController(MediaService mediaService) {
	        this.mediaService = mediaService;
	    }


	@GetMapping("/write")
	public String writeForm(Model model) {
		List<HeaderDto> animalList = headerDao.selectAll("animal");
		List<HeaderDto> typeList = headerDao.selectAll("type");
		model.addAttribute("animalList", animalList);
	    model.addAttribute("typeList", typeList);
		return "/WEB-INF/views/board/info/write.jsp";
	}

	 @PostMapping("/write")
	   public String write(@ModelAttribute BoardDto boardDto, HttpSession session, Model model,
			@RequestParam(required = false) Integer noticePinOrder,
			@RequestParam(required = false) String noticePinStart,
			@RequestParam(required = false) String noticePinEnd) {

	       String loginId = (String) session.getAttribute("loginId");
	       if (loginId == null) throw new IllegalStateException("로그인 정보가 없습니다.");
	       boardDto.setBoardWriter(loginId);
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		var typeHeader = headerDao.selectOne(boardDto.getBoardTypeHeader(), "type");
		if (typeHeader != null && "공지".equals(typeHeader.getHeaderName())) {
			if (loginLevel == null || loginLevel.intValue() != 0) throw new NeedPermissionException();
		}


	       if (boardDto.getBoardContent() == null || boardDto.getBoardContent().trim().isEmpty()) {
	           boardDto.setBoardContent("(내용 없음)");
	       }

	       boardDto.setBoardNo(boardDao.sequence());
	       int boardType = 2;
	       boardDao.insert(boardDto, boardType);


		noticeService.afterWrite(boardDto.getBoardNo(), boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, 2, null, boardDto.getBoardTitle());
	       memberDao.addPoint(loginId, 70);
	       MemberDto member = memberDao.selectOne(loginId);
	       model.addAttribute("memberPoint", member.getMemberPoint());

		return "redirect:list";

	}

	 @RequestMapping("/list")
	 public String list(
	         Model model,
	         @ModelAttribute("pageVO") PageVO pageVO,
	         @RequestParam(required = false, defaultValue = "wtime") String orderBy
	 ) {
	     int boardType = 2;
	     CategoryDto categoryDto = categoryDao.selectOne(boardType);

	     pageVO.setSize(10);
		List<BoardDetailVO> boardList;
		if (pageVO.isList()) {
			model.addAttribute("noticeList", boardDao.selectNoticeTop3(boardType));
			pageVO.setDataCount(boardDao.countWithoutNotice(boardType));
			boardList = boardDao.selectListDetailWithoutNotice(pageVO.getBegin(), pageVO.getEnd(), boardType, orderBy);
		} else {
			pageVO.setDataCount(boardDao.count(pageVO, boardType));
			boardList = boardDao.selectListDetail(pageVO.getBegin(), pageVO.getEnd(), boardType, orderBy);
		}
	     model.addAttribute("category", categoryDto);
	     model.addAttribute("boardList", boardList);
	     model.addAttribute("pageVO", pageVO);
	     model.addAttribute("orderBy", orderBy);

	     return "/WEB-INF/views/board/info/list.jsp";
	 }


	 @RequestMapping("/detail")
	 public String detail(Model model, @RequestParam int boardNo) {

	     BoardDetailVO boardDto = boardDao.selectOneDetail(boardNo);
	     if (boardDto == null) {
	         throw new TargetNotfoundException("존재하지 않는 글 번호");
	     }
	     model.addAttribute("boardDto", boardDto);


	     return "/WEB-INF/views/board/info/detail.jsp";
	 }


	   @PostMapping("/delete")
	   public String delete(@RequestParam int boardNo, HttpSession session) {
	       BoardDto boardDto = boardDao.selectOne(boardNo);
	       if (boardDto == null) throw new TargetNotfoundException("존재하지 않는 글");

	       String loginId = (String) session.getAttribute("loginId");
	       Integer loginLevel = (Integer) session.getAttribute("loginLevel");
	       boolean isAdmin = loginLevel != null && loginLevel == 0;
	       boolean isOwner = loginId != null && loginId.equals(boardDto.getBoardWriter());
	       if (!isOwner && !isAdmin) throw new NeedPermissionException("삭제 권한이 없습니다.");

	       Document document = Jsoup.parse(boardDto.getBoardContent());
	       Elements elements = document.select(".custom-image");
	       for (Element element : elements) {
	           int attachmentNo = Integer.parseInt(element.attr("data-pk"));
	           mediaService.delete(attachmentNo);
	       }

	       boardDao.delete(boardNo);
			if(boardDto.getBoardWriter() != null) {
				memberDao.minusPoint(boardDto.getBoardWriter(), 70);
				}
	       return "redirect:list";
	   }


	 @GetMapping("/edit")
	 public String edit(Model model, @RequestParam int boardNo, HttpSession session) {
		 BoardDto boardDto = boardDao.selectOne(boardNo);
		 if (boardDto == null) throw new TargetNotfoundException("존재하지 않는 글");
		 String loginId = (String) session.getAttribute("loginId");
		 Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		 boolean isAdmin = loginLevel != null && loginLevel == 0;
		 boolean isOwner = loginId != null && loginId.equals(boardDto.getBoardWriter());
		 if (!isOwner && !isAdmin) throw new NeedPermissionException("수정 권한이 없습니다.");
		 List<HeaderDto> animalList = headerDao.selectAll("animal");
		 List<HeaderDto> typeList = headerDao.selectAll("type");
		 model.addAttribute("animalList", animalList);
		 model.addAttribute("typeList", typeList);
		 model.addAttribute("boardDto", boardDto);
		 var noticePin = noticeService.selectPin(boardNo);
		 if (noticePin != null) {
			 Integer orderValue = noticePin.getPinOrder() == 9999 ? null : noticePin.getPinOrder();
			 model.addAttribute("noticePinOrderValue", orderValue);
			 if (noticePin.getPinStart() != null) model.addAttribute("noticePinStart", noticePin.getPinStart().toLocalDateTime().toLocalDate().toString());
			 if (noticePin.getPinEnd() != null) model.addAttribute("noticePinEnd", noticePin.getPinEnd().toLocalDateTime().toLocalDate().toString());
		 }
		 return "/WEB-INF/views/board/info/edit.jsp";
	 }

	 @PostMapping("/edit")
	 public String edit(@ModelAttribute BoardDto boardDto, HttpSession session,
			@RequestParam(required = false) Integer noticePinOrder,
			@RequestParam(required = false) String noticePinStart,
			@RequestParam(required = false) String noticePinEnd) {
		 BoardDto beforeDto = boardDao.selectOne(boardDto.getBoardNo());
		 if (beforeDto == null) throw new TargetNotfoundException("존재하지 않는 글");

		 String loginId = (String) session.getAttribute("loginId");
		 Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		 boolean isAdmin = loginLevel != null && loginLevel == 0;
		 boolean isOwner = loginId != null && loginId.equals(beforeDto.getBoardWriter());
		 if (!isOwner && !isAdmin) throw new NeedPermissionException("수정 권한이 없습니다.");
		var typeHeader = headerDao.selectOne(boardDto.getBoardTypeHeader(), "type");
		if (typeHeader != null && "공지".equals(typeHeader.getHeaderName()) && !isAdmin) {
			throw new com.spring.semi.error.NeedPermissionException();
		}


		 Set<Integer> before = new HashSet<>();
		 Document beforeDocument = Jsoup.parse(beforeDto.getBoardContent());
		 Elements beforeElements = beforeDocument.select(".custom-image");
		 for (Element element : beforeElements) {
			 int attachmentNo = Integer.parseInt(element.attr("data-pk"));
			 before.add(attachmentNo);
		 }
		 Set<Integer> after = new HashSet<>();
		 Document afterDocument = Jsoup.parse(boardDto.getBoardContent());
		 Elements afterElements = afterDocument.select(".custom-image");
		 for (Element element : afterElements) {
			 int attachmentNo = Integer.parseInt(element.attr("data-pk"));
			 after.add(attachmentNo);
		 }

		 Set<Integer> minus = new HashSet<>(before);
		 minus.removeAll(after);
		 for (int attachmentNo : minus) {
			 mediaService.delete(attachmentNo);
		 }
		 boardDao.update(boardDto);
		 noticeService.afterEdit(boardDto.getBoardNo(), beforeDto.getBoardTypeHeader(), boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, 2, null, boardDto.getBoardTitle());
		 return "redirect:detail?boardNo=" + boardDto.getBoardNo();
	 }

}
