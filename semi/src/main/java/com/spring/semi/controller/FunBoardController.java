package com.spring.semi.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.spring.semi.util.HtmlMediaUtil;
import com.spring.semi.vo.BoardDetailVO;
import com.spring.semi.vo.BoardVO;
import com.spring.semi.vo.PageVO;

import jakarta.servlet.http.HttpSession;


/**
 * FunBoardController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/board/fun")
public class FunBoardController {
	@Autowired
	private MediaService mediaService;
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


	@RequestMapping("/list")
	 public String list(
	         Model model,
	         @ModelAttribute("pageVO") PageVO pageVO,
	         @RequestParam(required = false, defaultValue = "wtime") String orderBy
	 ) {
	     int boardType = 24;
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

	     return "/WEB-INF/views/board/fun/list.jsp";
	 }


	@GetMapping("/write")
	public String writeForm(Model model) {
		       List<HeaderDto> animalList = headerDao.selectAll("animal");
		       List<HeaderDto> typeList = headerDao.selectAll("type");
		       model.addAttribute("animalList", animalList);
		       model.addAttribute("typeList", typeList);

		return "/WEB-INF/views/board/fun/write.jsp";
	}

    @PostMapping("/write")
    public String write(@ModelAttribute BoardDto boardDto,
                        HttpSession session,
            			@RequestParam MultipartFile media,
            			@RequestParam MultipartFile video,
            			@RequestParam(required = false) String remove,
            			@RequestParam(required = false) Integer noticePinOrder,
            			@RequestParam(required = false) String noticePinStart,
            			@RequestParam(required = false) String noticePinEnd) throws IllegalStateException, IOException
    {
        String loginId = (String) session.getAttribute("loginId");
        boardDto.setBoardWriter(loginId);
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		var typeHeader = headerDao.selectOne(boardDto.getBoardTypeHeader(), "type");
		if (typeHeader != null && "공지".equals(typeHeader.getHeaderName())) {
			if (loginLevel == null || loginLevel.intValue() != 0) throw new NeedPermissionException();
		}


        int boardNo = boardDao.sequence();
        boardDto.setBoardNo(boardNo);


        boardDao.insert(boardDto, 24);
        mainController.clearBoardCache("fun_board_list");
		noticeService.afterWrite(boardNo, boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, 24, null, boardDto.getBoardTitle());

		if(!media.isEmpty())
		{
			int mediaNo = mediaService.save(media);
			boardDao.connect(boardNo, mediaNo);
		}
		if(!video.isEmpty()) {
			int videoNo = mediaService.save(video);
			boardDao.connect_video(boardNo, videoNo);
		}

		return "redirect:detail?boardNo=" + boardNo;
	}

	@RequestMapping("/detail")
	public String detail(
			Model model,
			@RequestParam int boardNo) {

		BoardDetailVO boardDetail = boardDao.selectOneDetail(boardNo);
        if (boardDetail == null)
            throw new TargetNotfoundException("존재하지 않는 게시글입니다.");
		model.addAttribute("boardDto", boardDetail);
		boolean hasVideo = false;
		try {
			boardDao.findVideo(boardNo);
			hasVideo = true;
		} catch (Exception e) {
		}
		model.addAttribute("hasVideo", hasVideo);

		return "/WEB-INF/views/board/fun/detail.jsp";
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
		return "/WEB-INF/views/board/fun/edit.jsp";
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute BoardDto boardDto, HttpSession session,
			@RequestParam MultipartFile media,
			@RequestParam(required = false) String remove,
			@RequestParam(required = false) Integer noticePinOrder,
			@RequestParam(required = false) String noticePinStart,
			@RequestParam(required = false) String noticePinEnd) throws IllegalStateException, IOException
	{
		if (!media.isEmpty())
		{
			try
			{
				int mediaNo = boardDao.findMedia(boardDto.getBoardNo());
				mediaService.delete(mediaNo);
			}
			catch(Exception e) {}

			int mediaNo = mediaService.save(media);
			boardDao.connect(boardDto.getBoardNo(), mediaNo);
		}
		else
		{
			if (remove != null)
			{
				try
				{
					int mediaNo = boardDao.findMedia(boardDto.getBoardNo());
					mediaService.delete(mediaNo);
				}
				catch(Exception e) { }
			}
		}

		BoardDto beforeDto = boardDao.selectOne(boardDto.getBoardNo());
		if (beforeDto == null)
			throw new TargetNotfoundException("존재하지 않는 게시글 번호");

		String loginId = (String) session.getAttribute("loginId");
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		boolean isAdmin = loginLevel != null && loginLevel == 0;
		boolean isOwner = loginId != null && loginId.equals(beforeDto.getBoardWriter());
		if (!isOwner && !isAdmin) throw new NeedPermissionException("수정 권한이 없습니다.");
		var typeHeader = headerDao.selectOne(boardDto.getBoardTypeHeader(), "type");
		if (typeHeader != null && "공지".equals(typeHeader.getHeaderName()) && !isAdmin) {
			throw new com.spring.semi.error.NeedPermissionException();
		}


		Set<Integer> before = HtmlMediaUtil.extractMediaNos(beforeDto.getBoardContent());
		Set<Integer> after = HtmlMediaUtil.extractMediaNos(boardDto.getBoardContent());
		Set<Integer> minus = new HashSet<>(before);
		minus.removeAll(after);
		for (int mediaNo : minus) {
			try {
				mediaService.delete(mediaNo);
			}
			catch (Exception e) {
			}
		}

		boardDao.update(boardDto);
		noticeService.afterEdit(boardDto.getBoardNo(), beforeDto.getBoardTypeHeader(), boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, 24, null, boardDto.getBoardTitle());
		return "redirect:detail?boardNo=" + boardDto.getBoardNo();
	}

	@PostMapping("/delete")
	public String delete(@RequestParam int boardNo, HttpSession session)
	{
		BoardDto boardDto = boardDao.selectOne(boardNo);
		if (boardDto == null)
			throw new TargetNotfoundException("존재하지 않는 게시글 번호");

		String loginId = (String) session.getAttribute("loginId");
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		boolean isAdmin = loginLevel != null && loginLevel == 0;
		boolean isOwner = loginId != null && loginId.equals(boardDto.getBoardWriter());
		if (!isOwner && !isAdmin) throw new NeedPermissionException("삭제 권한이 없습니다.");

		Set<Integer> mediaNos = HtmlMediaUtil.extractMediaNos(boardDto.getBoardContent());
		for (int mediaNo : mediaNos) {
			try {
				mediaService.delete(mediaNo);
			}
			catch (Exception e) {
			}
		}
		boardDao.delete(boardNo);
		return "redirect:list";
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
	@GetMapping("/video")
	public String video(@RequestParam int boardNo)
	{
		try
		{
			int mediaNo = boardDao.findVideo(boardNo);
			return "redirect:/media/download?mediaNo=" + mediaNo;
		}
		catch(Exception e)
		{
			return "redirect:/image/error/no-image.png";
		}
	}
}
