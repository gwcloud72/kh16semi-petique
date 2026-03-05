package com.spring.semi.controller;


import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import org.springframework.web.util.UriUtils;

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
import com.spring.semi.vo.BoardDetailVO;
import com.spring.semi.vo.PageVO;
import com.spring.semi.service.NoticeService;

import jakarta.servlet.http.HttpSession;


/**
 * CreateBoardController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/board")
public class CreateBoardController {

    @Autowired private BoardDao boardDao;
    @Autowired private CategoryDao categoryDao;
    @Autowired private MemberDao memberDao;
    @Autowired private HeaderDao headerDao;
    @Autowired private NoticeService noticeService;

    @GetMapping("/{categoryName}/list")
    public String list(
            @PathVariable String categoryName,
            @RequestParam(required = false, defaultValue = "wtime") String orderBy,
            @ModelAttribute("pageVO") PageVO pageVO,
            Model model) {


        categoryName = URLDecoder.decode(categoryName, StandardCharsets.UTF_8);


        CategoryDto category = categoryDao.selectOneByName(categoryName);
        if (category == null) {
            throw new TargetNotfoundException("존재하지 않는 게시판입니다.");
        }
        int categoryNo = category.getCategoryNo();


                pageVO.setSize(10);
        List<BoardDetailVO> boardList;
        if (pageVO.isList()) {
            model.addAttribute("noticeList", boardDao.selectNoticeTop3(categoryNo));
            pageVO.setDataCount(boardDao.countWithoutNotice(categoryNo));
            boardList = boardDao.selectListDetailWithoutNotice(pageVO.getBegin(), pageVO.getEnd(), categoryNo, orderBy);
        } else {
            int dataCount = boardDao.count(pageVO, categoryNo);
            pageVO.setDataCount(dataCount);
            boardList = boardDao.selectListDetail(pageVO.getBegin(), pageVO.getEnd(), categoryNo, orderBy);
        }


model.addAttribute("category", category);
        model.addAttribute("boardList", boardList);
        model.addAttribute("pageVO", pageVO);
        model.addAttribute("orderBy", orderBy);

        return "/WEB-INF/views/board/common/list.jsp";
    }


    @GetMapping("/{categoryName}/write")
    public String write(@PathVariable String categoryName, Model model) {
        categoryName = URLDecoder.decode(categoryName, StandardCharsets.UTF_8);

        CategoryDto category = categoryDao.selectOneByName(categoryName);
        if (category == null)
            throw new TargetNotfoundException("존재하지 않는 게시판입니다.");


        List<HeaderDto> headerList = headerDao.selectAll("type");
        model.addAttribute("headerList", headerList);
        model.addAttribute("category", category);
        return "/WEB-INF/views/board/common/write.jsp";
    }


    @PostMapping("/{categoryName}/write")
    public String write(
            @PathVariable String categoryName,
            @ModelAttribute BoardDto boardDto,
            HttpSession session,
            @RequestParam(required = false) Integer noticePinOrder,
            @RequestParam(required = false) String noticePinStart,
            @RequestParam(required = false) String noticePinEnd) {

        CategoryDto category = categoryDao.selectOneByName(categoryName);
        if (category == null)
            throw new TargetNotfoundException("존재하지 않는 게시판입니다.");

        int categoryNo = category.getCategoryNo();

        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) throw new IllegalStateException("로그인 정보가 없습니다.");

        boardDto.setBoardWriter(loginId);
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		var typeHeader = headerDao.selectOne(boardDto.getBoardTypeHeader(), "type");
		if (typeHeader != null && "공지".equals(typeHeader.getHeaderName())) {
			if (loginLevel == null || loginLevel.intValue() != 0) throw new NeedPermissionException();
		}

        boardDto.setBoardCategoryNo(categoryNo);


        int boardNo = boardDao.sequence();
        boardDto.setBoardNo(boardNo);
        boardDao.insert(boardDto, categoryNo);

        noticeService.afterWrite(boardNo, boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, categoryNo, category.getCategoryName(), boardDto.getBoardTitle());


        String encodedCategory = URLEncoder.encode(categoryName, StandardCharsets.UTF_8);
        return "redirect:/board/" + encodedCategory + "/detail?boardNo=" + boardNo;
    }

    @GetMapping("/{categoryName}/detail")
    public String detail(
            @PathVariable String categoryName,
            @RequestParam int boardNo,
            Model model) {

        categoryName = URLDecoder.decode(categoryName, StandardCharsets.UTF_8);

        CategoryDto category = categoryDao.selectOneByName(categoryName);
        if (category == null)
            throw new TargetNotfoundException("존재하지 않는 게시판입니다.");

        BoardDetailVO boardDetail = boardDao.selectOneDetail(boardNo);
        if (boardDetail == null)
            throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

        model.addAttribute("category", category);
        model.addAttribute("boardDto", boardDetail);
        return "/WEB-INF/views/board/common/detail.jsp";
    }


    @PostMapping("/{categoryName}/delete")
    public String delete(
            @PathVariable String categoryName,
            @RequestParam int boardNo,
            HttpSession session) {

        CategoryDto category = categoryDao.selectOneByName(categoryName);
        if (category == null)
            throw new TargetNotfoundException("존재하지 않는 게시판입니다.");

        BoardDto boardDto = boardDao.selectOne(boardNo);
        if (boardDto == null)
            throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

        String loginId = (String) session.getAttribute("loginId");
        Integer loginLevel = (Integer) session.getAttribute("loginLevel");
        boolean isAdmin = loginLevel != null && loginLevel == 0;
        boolean isOwner = loginId != null && loginId.equals(boardDto.getBoardWriter());
        if (!isOwner && !isAdmin) {
            throw new NeedPermissionException("삭제 권한이 없습니다.");
        }

        boardDao.delete(boardNo);

        String encodedCategory = UriUtils.encodePathSegment(categoryName, StandardCharsets.UTF_8);
        return "redirect:/board/" + encodedCategory + "/list";
    }


    @GetMapping("/{categoryName}/edit")
    public String editForm(
            @PathVariable String categoryName,
            @RequestParam int boardNo,
            HttpSession session,
            Model model) {

        categoryName = URLDecoder.decode(categoryName, StandardCharsets.UTF_8);

        CategoryDto category = categoryDao.selectOneByName(categoryName);
        if (category == null)
            throw new TargetNotfoundException("존재하지 않는 게시판입니다.");

        BoardDto boardDto = boardDao.selectOne(boardNo);
        if (boardDto == null)
            throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

        String loginId = (String) session.getAttribute("loginId");
        Integer loginLevel = (Integer) session.getAttribute("loginLevel");
        boolean isAdmin = loginLevel != null && loginLevel == 0;
        boolean isOwner = loginId != null && loginId.equals(boardDto.getBoardWriter());
        if (!isOwner && !isAdmin) {
            throw new NeedPermissionException("수정 권한이 없습니다.");
        }


        List<HeaderDto> headerList = headerDao.selectAll("type");
        model.addAttribute("headerList", headerList);

        model.addAttribute("category", category);
        model.addAttribute("boardDto", boardDto);
        var noticePin = noticeService.selectPin(boardNo);
        if (noticePin != null) {
            Integer orderValue = noticePin.getPinOrder() == 9999 ? null : noticePin.getPinOrder();
            model.addAttribute("noticePinOrderValue", orderValue);
            if (noticePin.getPinStart() != null) model.addAttribute("noticePinStart", noticePin.getPinStart().toLocalDateTime().toLocalDate().toString());
            if (noticePin.getPinEnd() != null) model.addAttribute("noticePinEnd", noticePin.getPinEnd().toLocalDateTime().toLocalDate().toString());
        }
        return "/WEB-INF/views/board/common/edit.jsp";
    }


    @PostMapping("/{categoryName}/edit")
    public String edit(
            @PathVariable String categoryName,
            @ModelAttribute BoardDto boardDto,
            HttpSession session,
            @RequestParam(required = false) Integer noticePinOrder,
            @RequestParam(required = false) String noticePinStart,
            @RequestParam(required = false) String noticePinEnd) {

        categoryName = URLDecoder.decode(categoryName, StandardCharsets.UTF_8);

        BoardDto existing = boardDao.selectOne(boardDto.getBoardNo());
        if (existing == null)
            throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

        String loginId = (String) session.getAttribute("loginId");
        Integer loginLevel = (Integer) session.getAttribute("loginLevel");
        boolean isAdmin = loginLevel != null && loginLevel == 0;
        boolean isOwner = loginId != null && loginId.equals(existing.getBoardWriter());
        if (!isOwner && !isAdmin) {
            throw new NeedPermissionException("수정 권한이 없습니다.");
        }
		var typeHeader = headerDao.selectOne(boardDto.getBoardTypeHeader(), "type");
		if (typeHeader != null && "공지".equals(typeHeader.getHeaderName()) && !isAdmin) {
			throw new NeedPermissionException();
		}


        boardDao.update(boardDto);
        noticeService.afterEdit(boardDto.getBoardNo(), existing.getBoardTypeHeader(), boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, existing.getBoardCategoryNo(), categoryName, boardDto.getBoardTitle());

        String encodedCategory = UriUtils.encodePathSegment(categoryName, StandardCharsets.UTF_8);
        return "redirect:/board/" + encodedCategory + "/detail?boardNo=" + boardDto.getBoardNo();
    }
}
