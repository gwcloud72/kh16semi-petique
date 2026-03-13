package com.spring.semi.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
import com.spring.semi.util.DummyImage;
import com.spring.semi.vo.BoardDetailVO;
import com.spring.semi.vo.PageVO;

import jakarta.servlet.http.HttpSession;


/**
 * BoardController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/board/community")
public class BoardController {
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
    private MainController mainController;
	@Autowired
	private NoticeService noticeService;

    BoardController(MediaService mediaService) {
        this.mediaService = mediaService;
    }


	 @RequestMapping("/list")
	 public String list(
	         Model model,
	         @ModelAttribute("pageVO") PageVO pageVO,
	         @RequestParam(required = false, defaultValue = "wtime") String orderBy
	 ) {
	     int boardType = 1;
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

	     return "/WEB-INF/views/board/community/list.jsp";
	 }

	@GetMapping("/write")
	public String writeForm(Model model) {
		List<HeaderDto> animalList = headerDao.selectAll("animal");
		List<HeaderDto> typeList = headerDao.selectAll("type");
		model.addAttribute("animalList", animalList);
		model.addAttribute("typeList", typeList);

		return "/WEB-INF/views/board/community/write.jsp";

	}

	@PostMapping("/write")
	   public String write(@ModelAttribute BoardDto boardDto,
               HttpSession session, Model model,
			@RequestParam MultipartFile media,
			@RequestParam(required = false) String remove,
			@RequestParam(required = false) Integer noticePinOrder,
			@RequestParam(required = false) String noticePinStart,
			@RequestParam(required = false) String noticePinEnd) throws IllegalStateException, IOException
	{
		String loginId = (String) session.getAttribute("loginId");
		if (loginId == null)
			throw new IllegalStateException("로그인 정보가 없습니다.");
		boardDto.setBoardWriter(loginId);
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		var typeHeader = headerDao.selectOne(boardDto.getBoardTypeHeader(), "type");
		if (typeHeader != null && "공지".equals(typeHeader.getHeaderName())) {
			if (loginLevel == null || loginLevel.intValue() != 0) throw new NeedPermissionException();
		}


		if (boardDto.getBoardContent() == null || boardDto.getBoardContent().trim().isEmpty()) {
			boardDto.setBoardContent("(내용 없음)");
		}

        int boardNo = boardDao.sequence();
        boardDto.setBoardNo(boardNo);

		boardDao.insert(boardDto, 1);
        mainController.clearBoardCache("community_board_list");

        if(!media.isEmpty())
		{
			int mediaNo = mediaService.save(media);
			boardDao.connect(boardNo, mediaNo);
		}


		noticeService.afterWrite(boardNo, boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, 1, null, boardDto.getBoardTitle());

		memberDao.addPoint(loginId, 50);
		MemberDto member = memberDao.selectOne(loginId);
		model.addAttribute("memberPoint", member.getMemberPoint());

		return "redirect:detail?boardNo=" + boardDto.getBoardNo();
	}

	@RequestMapping("/detail")
	public String detail(Model model, @RequestParam int boardNo) {

		BoardDetailVO boardDetail = boardDao.selectOneDetail(boardNo);
        if (boardDetail == null)
            throw new TargetNotfoundException("존재하지 않는 게시글입니다.");
		model.addAttribute("boardDto", boardDetail);
		return "/WEB-INF/views/board/community/detail.jsp";
	}

	@GetMapping("/edit")
	public String edit(Model model, @RequestParam int boardNo, HttpSession session) {
		BoardDto boardDto = boardDao.selectOne(boardNo);
		if (boardDto == null)
			throw new TargetNotfoundException("존재하지 않는 글");
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
		return "/WEB-INF/views/board/community/edit.jsp";
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute BoardDto boardDto, HttpSession session,
			@RequestParam(required = false) Integer noticePinOrder,
			@RequestParam(required = false) String noticePinStart,
			@RequestParam(required = false) String noticePinEnd) {

		BoardDto beforeDto = boardDao.selectOne(boardDto.getBoardNo());
		if (beforeDto == null)
			throw new TargetNotfoundException("존재하지 않는 글");
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
		noticeService.afterEdit(boardDto.getBoardNo(), beforeDto.getBoardTypeHeader(), boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, 1, null, boardDto.getBoardTitle());
		return "redirect:detail?boardNo=" + boardDto.getBoardNo();
	}

	@PostMapping("/delete")
	public String delete(@RequestParam int boardNo, HttpSession session, Model model) {
		BoardDto boardDto = boardDao.selectOne(boardNo);
		if (boardDto == null) throw new TargetNotfoundException("존재하지 않는 게시글 번호");

		String loginId = (String) session.getAttribute("loginId");
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		boolean isAdmin = loginLevel != null && loginLevel == 0;
		boolean isOwner = loginId != null && loginId.equals(boardDto.getBoardWriter());
		if (!isOwner && !isAdmin) throw new NeedPermissionException("삭제 권한이 없습니다.");

		Document document = Jsoup.parse(boardDto.getBoardContent());
		Elements elements = document.select(".custom-image");
		for (Element element : elements) {
			int mediaNo = Integer.parseInt(element.attr("data-pk"));
			mediaService.delete(mediaNo);
		}

		boardDao.delete(boardNo);

		if (boardDto.getBoardWriter() != null) {
			memberDao.minusPoint(boardDto.getBoardWriter(), 50);
			MemberDto member = memberDao.selectOne(loginId);
			model.addAttribute("memberPoint", member.getMemberPoint());
		}

		return "redirect:list";
	}

	@PostMapping("/mypageDelete")
	@ResponseBody
	public String mypageDelete(@RequestParam("boardNo") List<Integer> boardNos, HttpSession session) {
		String loginId = (String) session.getAttribute("loginId");
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		boolean isAdmin = loginLevel != null && loginLevel == 0;

		for (int boardNo : boardNos) {
			BoardDto boardDto = boardDao.selectOne(boardNo);
			if (boardDto == null) continue;

			boolean isOwner = loginId != null && loginId.equals(boardDto.getBoardWriter());
			if (!isOwner && !isAdmin) continue;

			Document document = Jsoup.parse(boardDto.getBoardContent());
			Elements elements = document.select(".custom-image");
			for (Element element : elements) {
				int mediaNo = Integer.parseInt(element.attr("data-pk"));
				mediaService.delete(mediaNo);
			}

			boardDao.delete(boardNo);

			int minusPnt;
			int boardCat = boardDto.getBoardCategoryNo();
			switch (boardCat) {
			case 1:
				minusPnt = 50;
				break;
			case 2:
				minusPnt = 70;
				break;
			case 3:
				minusPnt = 50;
				break;
			case 4:
				minusPnt = 60;
				break;
			default:
				minusPnt = 50;
				break;
			}

			if (boardDto.getBoardWriter() != null) {
				memberDao.minusPoint(boardDto.getBoardWriter(), minusPnt);
			}
		}

		return "success";
	}

	@GetMapping("/image")
	public String image(@RequestParam int boardNo)
	{
		try
		{
			int mediaNo = boardDao.findMedia(boardNo);
			return "redirect:/media/download?mediaNo=" + mediaNo;
		}
		catch(Exception e)
		{
			return "redirect:" + DummyImage.path(boardNo);
		}
	}
}
