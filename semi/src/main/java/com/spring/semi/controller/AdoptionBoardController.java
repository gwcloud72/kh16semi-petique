package com.spring.semi.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
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
import org.springframework.web.multipart.MultipartFile;

import com.spring.semi.dao.AdoptionApplyDao;
import com.spring.semi.dao.AdoptionReviewLinkDao;
import com.spring.semi.dao.AdoptionBoardDao;
import com.spring.semi.dao.AnimalDao;
import com.spring.semi.dao.BoardDao;
import com.spring.semi.dao.CategoryDao;
import com.spring.semi.dao.HeaderDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.AnimalDto;
import com.spring.semi.dto.BoardDto;
import com.spring.semi.dto.CategoryDto;
import com.spring.semi.dto.HeaderDto;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.error.NeedPermissionException;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.service.AdoptionProcessService;
import com.spring.semi.service.MediaService;
import com.spring.semi.service.NoticeService;
import com.spring.semi.util.HtmlTextUtil;
import com.spring.semi.vo.AdoptDetailVO;
import com.spring.semi.vo.AdoptionApplyVO;
import com.spring.semi.vo.PageFilterVO;

import jakarta.servlet.http.HttpSession;


/**
 * AdoptionBoardController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/board/adoption")
public class AdoptionBoardController {

    @Autowired
    private MediaService mediaService;
    @Autowired
    private BoardDao boardDao;
    @Autowired
    private AdoptionBoardDao adoptionBoardDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private HeaderDao headerDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private AnimalDao animalDao;
    @Autowired
    private AdoptionApplyDao adoptionApplyDao;
	@Autowired
	private AdoptionReviewLinkDao adoptionReviewLinkDao;
    @Autowired
    private AdoptionProcessService adoptionProcessService;
    @Autowired
    private NoticeService noticeService;

    @GetMapping("/write")
    public String writeForm(HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        List<HeaderDto> animalList = headerDao.selectAll("animal");
        List<HeaderDto> typeList = headerDao.selectAll("type");
        List<AnimalDto> adoptableAnimalList = animalDao.selectFilterTMaster(loginId);

        model.addAttribute("animalList", animalList);
        model.addAttribute("typeList", typeList);
        model.addAttribute("adoptableAnimalList", adoptableAnimalList);

        return "/WEB-INF/views/board/adoption/write.jsp";
    }

    @PostMapping("/write")
    public String write(
            @ModelAttribute AdoptDetailVO adoptDetailVO,
            @RequestParam(required = false) MultipartFile media,
            HttpSession session,
            Model model,
            @RequestParam(required = false) Integer noticePinOrder,
            @RequestParam(required = false) String noticePinStart,
            @RequestParam(required = false) String noticePinEnd
    ) throws IllegalStateException, IOException {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        int animalNo = adoptDetailVO.getAnimalNo();

        BoardDto boardDto = new BoardDto();
        boardDto.setBoardTitle(adoptDetailVO.getBoardTitle());
        boardDto.setBoardContent(adoptDetailVO.getBoardContent());
        boardDto.setBoardCategoryNo(adoptDetailVO.getBoardCategoryNo());
        boardDto.setBoardAnimalHeader(adoptDetailVO.getBoardAnimalHeader());
        boardDto.setBoardTypeHeader(adoptDetailVO.getBoardTypeHeader());
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
        int boardType = 4;
        boolean isNotice = noticeService.isNotice(boardDto.getBoardTypeHeader());
        boardDao.insert(boardDto, boardType);
        if (!isNotice) {
            adoptionBoardDao.insertAnimalConnect(boardDto.getBoardNo(), animalNo);
        }

        noticeService.afterWrite(boardDto.getBoardNo(), boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, 4, null, boardDto.getBoardTitle());

        memberDao.addPoint(loginId, 60);
        MemberDto member = memberDao.selectOne(loginId);
        model.addAttribute("memberPoint", member.getMemberPoint());

        return "redirect:detail?boardNo=" + boardDto.getBoardNo();
    }

    @GetMapping("/list")
    public String list(@ModelAttribute PageFilterVO pageFilterVO, Model model) {
        final int boardType = 4;
        final int pageSize = 12;

        String orderBy = (pageFilterVO.getOrderBy() == null || pageFilterVO.getOrderBy().isBlank())
                ? "wtime" : pageFilterVO.getOrderBy().trim();
        if (!(orderBy.equals("wtime") || orderBy.equals("view") || orderBy.equals("like"))) {
            orderBy = "wtime";
        }
        pageFilterVO.setOrderBy(orderBy);

        String animalHeaderName = pageFilterVO.getAnimalHeaderName();
        if (animalHeaderName != null) {
            animalHeaderName = animalHeaderName.trim();
            if (animalHeaderName.isEmpty()) animalHeaderName = null;
        }

        String typeHeaderName = pageFilterVO.getTypeHeaderName();
        if (typeHeaderName != null) {
            typeHeaderName = typeHeaderName.trim();
            if (typeHeaderName.isEmpty()) typeHeaderName = null;
        }

        String keyword = pageFilterVO.getKeyword();
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) keyword = null;
        }

        String adoptionStage = pageFilterVO.getAdoptionStage();
        if (adoptionStage != null) {
            adoptionStage = adoptionStage.trim().toUpperCase();
            if (adoptionStage.isEmpty() || "ALL".equals(adoptionStage)) adoptionStage = null;
        }
        if (adoptionStage != null && !("OPEN".equals(adoptionStage) || "APPROVED".equals(adoptionStage) || "COMPLETED".equals(adoptionStage))) {
            adoptionStage = null;
        }

        String column = pageFilterVO.getColumn();
        if (column == null || column.isBlank()) column = "board_title";
        column = column.trim();
        if (!(column.equals("board_title") || column.equals("board_writer") || column.equals("member_nickname")
                || column.equals("animal_header_name") || column.equals("type_header_name"))) {
            column = "board_title";
        }

        pageFilterVO.setAnimalHeaderName(animalHeaderName);
        pageFilterVO.setTypeHeaderName(typeHeaderName);
        pageFilterVO.setKeyword(keyword);
        pageFilterVO.setColumn(column);
        pageFilterVO.setAdoptionStage(adoptionStage);

        int page = (pageFilterVO.getPage() > 0) ? pageFilterVO.getPage() : 1;
        pageFilterVO.setSize(pageSize);

        int begin = (page - 1) * pageSize + 1;
        int end = page * pageSize;

        pageFilterVO.setBegin(begin);
        pageFilterVO.setEnd(end);

        if (keyword == null && adoptionStage == null && (typeHeaderName == null || !"공지".equals(typeHeaderName))) {
            model.addAttribute("noticeList", adoptionBoardDao.selectAdoptNoticeTop3(boardType));
        }

        List<AdoptDetailVO> boardList = adoptionBoardDao.selectFilterListWithPaging(pageFilterVO, boardType);
        int totalCount = adoptionBoardDao.countFilter(pageFilterVO, boardType);

		for (AdoptDetailVO vo : boardList) {
			String stage = vo.getAdoptionStage();
			if (stage == null || stage.isBlank()) {
				stage = "OPEN";
				if ("f".equals(vo.getAnimalPermission())) stage = "COMPLETED";
				vo.setAdoptionStage(stage);
			}

			String summary = HtmlTextUtil.toPlainText(vo.getBoardContent());
			if (summary == null || summary.isBlank()) summary = HtmlTextUtil.toPlainText(vo.getAnimalContent());
			summary = HtmlTextUtil.ellipsis(summary, 80);
			vo.setBoardSummary(summary);
		}

        pageFilterVO.setDataCount(totalCount);

        List<HeaderDto> animalList = headerDao.selectAll("animal").stream()
                .filter(h -> h.getHeaderNo() != 0)
                .collect(Collectors.toList());
        List<HeaderDto> typeList = headerDao.selectAll("type").stream()
                .filter(h -> h.getHeaderNo() != 0)
                .collect(Collectors.toList());
        CategoryDto categoryDto = categoryDao.selectOne(boardType);

        model.addAttribute("boardList", boardList);
        model.addAttribute("animalList", animalList);
        model.addAttribute("typeList", typeList);
        model.addAttribute("category", categoryDto);
        model.addAttribute("boardType", boardType);
        model.addAttribute("pageVO", pageFilterVO);
        model.addAttribute("selectedAnimalHeaderName", pageFilterVO.getAnimalHeaderName());
        model.addAttribute("selectedTypeHeaderName", pageFilterVO.getTypeHeaderName());
        model.addAttribute("selectedOrderBy", orderBy);

        return "/WEB-INF/views/board/adoption/list.jsp";
    }

    @GetMapping("/edit")
    public String editForm(
            Model model,
            @RequestParam int boardNo,
            HttpSession session
    ) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        AdoptDetailVO detailVO = adoptionBoardDao.selectAdoptDetail(boardNo);
        if (detailVO == null) {
            throw new TargetNotfoundException("존재하지 않는 글이거나 동물 정보가 누락되었습니다.");
        }

        Integer loginLevel = (Integer) session.getAttribute("loginLevel");
        boolean isAdmin = loginLevel != null && loginLevel == 0;
        boolean isOwner = loginId != null && loginId.equals(detailVO.getBoardWriter());
        if (!isOwner && !isAdmin) {
            throw new NeedPermissionException("수정 권한이 없습니다.");
        }

        List<HeaderDto> animalList = headerDao.selectAll("animal");
        List<HeaderDto> typeList = headerDao.selectAll("type");
        List<AnimalDto> adoptableAnimalList = animalDao.selectFilterTMaster(loginId);

        AdoptionApplyVO approvedApply = adoptionApplyDao.selectApprovedByBoardNo(boardNo);
        String adoptionStage = "OPEN";
        if ("f".equals(detailVO.getAnimalPermission())) adoptionStage = "COMPLETED";
        else if (approvedApply != null) adoptionStage = "APPROVED";

        model.addAttribute("animalList", animalList);
        model.addAttribute("typeList", typeList);
        model.addAttribute("adoptableAnimalList", adoptableAnimalList);
        model.addAttribute("adoptDetailVO", detailVO);
        model.addAttribute("currentAnimalNo", detailVO.getAnimalNo());
        model.addAttribute("approvedApply", approvedApply);
        model.addAttribute("adoptionStage", adoptionStage);

        var noticePin = noticeService.selectPin(boardNo);
        if (noticePin != null) {
            Integer orderValue = noticePin.getPinOrder() == 9999 ? null : noticePin.getPinOrder();
            model.addAttribute("noticePinOrderValue", orderValue);
            if (noticePin.getPinStart() != null) model.addAttribute("noticePinStart", noticePin.getPinStart().toLocalDateTime().toLocalDate().toString());
            if (noticePin.getPinEnd() != null) model.addAttribute("noticePinEnd", noticePin.getPinEnd().toLocalDateTime().toLocalDate().toString());
        }

        return "/WEB-INF/views/board/adoption/edit.jsp";
    }

    @PostMapping("/edit")
    public String edit(
            @ModelAttribute BoardDto boardDto,
            @RequestParam int animalNo,
            HttpSession session,
            @RequestParam(required = false) Integer noticePinOrder,
            @RequestParam(required = false) String noticePinStart,
            @RequestParam(required = false) String noticePinEnd
    ) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        BoardDto beforeDto = boardDao.selectOne(boardDto.getBoardNo());
        if (beforeDto == null) throw new TargetNotfoundException("존재하지 않는 글");

        Integer loginLevel = (Integer) session.getAttribute("loginLevel");
        boolean isAdmin = loginLevel != null && loginLevel == 0;
        boolean isOwner = loginId != null && loginId.equals(beforeDto.getBoardWriter());
        if (!isOwner && !isAdmin) {
            throw new NeedPermissionException("수정 권한이 없습니다.");
        }
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

        boolean isNotice = noticeService.isNotice(boardDto.getBoardTypeHeader());
        if (animalNo != 0) {
            adoptionBoardDao.updateBoardAnimal(boardDto.getBoardNo(), animalNo);
        } else if (isNotice) {
            adoptionBoardDao.deleteBoardAnimal(boardDto.getBoardNo());
        }

        noticeService.afterEdit(boardDto.getBoardNo(), beforeDto.getBoardTypeHeader(), boardDto.getBoardTypeHeader(), noticePinOrder, noticePinStart, noticePinEnd, loginId, 4, null, boardDto.getBoardTitle());

        return "redirect:detail?boardNo=" + boardDto.getBoardNo();
    }

    @PostMapping("/delete")
    public String delete(@RequestParam int boardNo, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        BoardDto boardDto = boardDao.selectOne(boardNo);
        if (boardDto == null) throw new TargetNotfoundException("존재하지 않는 글입니다.");

        Integer loginLevel = (Integer) session.getAttribute("loginLevel");
        boolean isAdmin = loginLevel != null && loginLevel == 0;
        boolean isOwner = loginId != null && loginId.equals(boardDto.getBoardWriter());
        if (!isOwner && !isAdmin) {
            throw new NeedPermissionException("삭제 권한이 없습니다.");
        }

        deleteAttachmentsFromContent(boardDto.getBoardContent());
        boardDao.delete(boardNo);
        return "redirect:list";
    }

    @RequestMapping("/detail")
    public String detail(HttpSession session, Model model, @RequestParam int boardNo) throws BadRequestException {
        String loginId = (String) session.getAttribute("loginId");

        AdoptDetailVO adoptDetailVO = adoptionBoardDao.selectAdoptDetail(boardNo);
        if (adoptDetailVO == null) throw new BadRequestException("존재하지 않는 글 번호입니다.");

        int animalNo = adoptDetailVO.getAnimalNo();
        try {
            int mediaNo = animalDao.findMediaNo(animalNo);
            adoptDetailVO.setMediaNo(mediaNo);
        } catch (Exception e) {
            adoptDetailVO.setMediaNo(null);
        }

        AdoptionApplyVO approvedApply = adoptionApplyDao.selectApprovedByBoardNo(boardNo);
        AdoptionApplyVO completedApply = adoptionApplyDao.selectCompletedByBoardNo(boardNo);

        String adoptionStage = "OPEN";
        if ("f".equals(adoptDetailVO.getAnimalPermission())) adoptionStage = "COMPLETED";
        else if (approvedApply != null) adoptionStage = "APPROVED";

        boolean isOwner = loginId != null && loginId.equals(adoptDetailVO.getBoardWriter());

        AdoptionApplyVO myApply = null;
        if (loginId != null) {
            myApply = adoptionApplyDao.selectLatestByBoardAndApplicant(boardNo, loginId);
        }

        boolean canApply = loginId != null
                && !isOwner
                && !"COMPLETED".equals(adoptionStage)
                && approvedApply == null
                && (myApply == null
                    || "REJECTED".equals(myApply.getApplyStatus())
                    || "CANCELLED".equals(myApply.getApplyStatus()));

        boolean canCancel = loginId != null
                && myApply != null
                && "APPLIED".equals(myApply.getApplyStatus());

		Integer reviewBoardNo = null;
		try {
			reviewBoardNo = adoptionReviewLinkDao.findReviewBoardNo(boardNo);
		} catch (Exception e) {
			reviewBoardNo = null;
		}
		boolean canWriteReview = loginId != null
				&& "COMPLETED".equals(adoptionStage)
				&& completedApply != null
				&& loginId.equals(completedApply.getApplicantId())
				&& reviewBoardNo == null;

        model.addAttribute("adoptDetailVO", adoptDetailVO);
        model.addAttribute("adoptionStage", adoptionStage);
        model.addAttribute("approvedApply", approvedApply);
        model.addAttribute("completedApply", completedApply);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("myApply", myApply);
        model.addAttribute("canApply", canApply);
        model.addAttribute("canCancel", canCancel);
		model.addAttribute("reviewBoardNo", reviewBoardNo);
		model.addAttribute("canWriteReview", canWriteReview);

        if (isOwner) {
            model.addAttribute("applyList", adoptionApplyDao.selectLatestListByBoardNo(boardNo));
        }

        return "/WEB-INF/views/board/adoption/detail.jsp";
    }

    @PostMapping("/apply")
    public String apply(@RequestParam int boardNo, @RequestParam(required = false) String applyContent, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        boolean ok = adoptionProcessService.apply(boardNo, loginId, applyContent);
        return "redirect:detail?boardNo=" + boardNo + (ok ? "&apply=ok" : "&apply=fail");
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam int applyNo, @RequestParam int boardNo, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        boolean ok = adoptionProcessService.cancel(applyNo, loginId);
        return "redirect:detail?boardNo=" + boardNo + (ok ? "&cancel=ok" : "&cancel=fail");
    }

    @PostMapping("/approve")
    public String approve(@RequestParam int applyNo, @RequestParam int boardNo, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        boolean ok = adoptionProcessService.approve(applyNo, loginId);
        return "redirect:detail?boardNo=" + boardNo + (ok ? "&approve=ok" : "&approve=fail");
    }

    @PostMapping("/reject")
    public String reject(@RequestParam int applyNo, @RequestParam int boardNo, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        boolean ok = adoptionProcessService.reject(applyNo, loginId);
        return "redirect:detail?boardNo=" + boardNo + (ok ? "&reject=ok" : "&reject=fail");
    }

    @PostMapping("/completeAdoption")
    public String completeAdoption(@RequestParam int boardNo, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/member/login";

        boolean ok = adoptionProcessService.complete(boardNo, loginId);
        return "redirect:detail?boardNo=" + boardNo + (ok ? "&complete=ok" : "&complete=fail");
    }

    private Set<Integer> extractAttachmentNos(String content) {
        Set<Integer> result = new HashSet<>();
        if (content == null || content.isBlank()) return result;
        try {
            Document doc = Jsoup.parse(content);
            Elements elements = doc.select(".custom-image[data-pk]");
            for (Element el : elements) {
                String dataPk = el.attr("data-pk");
                if (dataPk != null && dataPk.matches("\\d+")) {
                    result.add(Integer.parseInt(dataPk));
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    private void deleteAttachmentsFromContent(String content) {
        if (content == null || content.isBlank()) return;
        try {
            Set<Integer> attachments = extractAttachmentNos(content);
            for (int attachmentNo : attachments) {
                try {
                    mediaService.delete(attachmentNo);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }
}
