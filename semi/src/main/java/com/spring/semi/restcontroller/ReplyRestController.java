package com.spring.semi.restcontroller;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spring.semi.dao.BoardDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dao.ReplyDao;
import com.spring.semi.dao.ReplyLikeDao;
import com.spring.semi.dto.BoardDto;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.dto.ReplyDto;
import com.spring.semi.error.NeedPermissionException;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.vo.ReplyLikeVO;
import com.spring.semi.vo.ReplyListResponseVO;
import com.spring.semi.vo.ReplyListVO;
import jakarta.servlet.http.HttpSession;


/**
 * ReplyRestController - 비동기/REST 요청을 처리하는 컨트롤러.
 */
@CrossOrigin
@RestController
@RequestMapping("/rest/reply")
public class ReplyRestController {
	@Autowired
	private ReplyDao replyDao;
	@Autowired
	private BoardDao boardDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private ReplyLikeDao replyLikeDao;

	@GetMapping("/list")
	public ReplyListResponseVO list(
	           @RequestParam int replyTarget,
	           @RequestParam(defaultValue = "time") String sort,

	           HttpSession session) {


			String loginId = (String) session.getAttribute("loginId");
	        if (loginId == null) {

	            loginId = "";
	        }

			BoardDto boardDto = boardDao.selectOne(replyTarget);
			if (boardDto == null)
				throw new TargetNotfoundException("존재하지 않는 게시글");


			List<ReplyListVO> result = replyDao.selectListWithLike(replyTarget, sort, loginId);


	       for (ReplyListVO reply : result) {

	           boolean isBoardWriter = boardDto.getBoardWriter() != null &&
	                                   reply.getReplyWriter() != null &&
	                                   boardDto.getBoardWriter().equals(reply.getReplyWriter());
	           reply.setWriter(isBoardWriter);


	           boolean isOwner = !loginId.isEmpty() &&
	                             reply.getReplyWriter() != null &&
	                             loginId.equals(reply.getReplyWriter());
	           reply.setOwner(isOwner);

	       }

	       int totalReplyCount = replyDao.countByBoardNo(replyTarget);

			return ReplyListResponseVO.builder()
					.boardReply(totalReplyCount)
					.list(result)
					.build();
		}


	@PostMapping("/write")
	public MemberDto write(@ModelAttribute ReplyDto replyDto, HttpSession session) {

		if (replyDto.getReplyCategoryNo() == 0) {
			throw new IllegalArgumentException("댓글 카테고리 번호가 필요합니다.");
		}
		int sequence = replyDao.sequence();
		replyDto.setReplyNo(sequence);
		String loginId = (String) session.getAttribute("loginId");
		replyDto.setReplyWriter(loginId);
		replyDao.insert(replyDto);

		memberDao.addPoint(loginId, 20);
		return memberDao.selectOne(loginId);
	}


	@PostMapping("/delete")
	public void delete(HttpSession session, @RequestParam int replyNo) {

		String loginId = (String) session.getAttribute("loginId");
		ReplyDto replyDto = replyDao.selectOne(replyNo);
		if (replyDto == null)
			throw new TargetNotfoundException("존재하지 않는 댓글");
		if (!loginId.equals(replyDto.getReplyWriter()))
			throw new NeedPermissionException("권한 부족");
		int boardNo = replyDto.getReplyTarget();
		replyDao.delete(replyNo, boardNo);

		memberDao.addPoint(loginId, -20);
	}


	@PostMapping("/edit")
	public void edit(HttpSession session, @ModelAttribute ReplyDto replyDto) {

		String loginId = (String) session.getAttribute("loginId");
		ReplyDto findDto = replyDao.selectOne(replyDto.getReplyNo());
		if (findDto == null)
			throw new TargetNotfoundException("존재하지 않는 댓글");
		if (!loginId.equals(findDto.getReplyWriter()))
			throw new NeedPermissionException("권한 부족");
		replyDao.update(replyDto);
	}


	@PostMapping("/like/action")
	public ReplyLikeVO likeAction(HttpSession session, @RequestParam int replyNo) {
	String memberId = (String) session.getAttribute("loginId");
	if (memberId == null)
	throw new NeedPermissionException("로그인 필요");
	boolean alreadyLiked = replyLikeDao.check(memberId, replyNo);
if (alreadyLiked) {
replyLikeDao.delete(memberId, replyNo);
replyDao.decreaseReplyLike(replyNo); } else {
	replyLikeDao.insert(memberId, replyNo);
	 replyDao.increaseReplyLike(replyNo);
}
int count = replyLikeDao.countByReplyNo(replyNo);
return new ReplyLikeVO(!alreadyLiked, count);
	}


	@GetMapping("/like/check")
	public ReplyLikeVO likeCheck(HttpSession session, @RequestParam int replyNo) {
		String memberId = (String) session.getAttribute("loginId");
		int count = replyLikeDao.countByReplyNo(replyNo);
		if (memberId == null)
			return new ReplyLikeVO(false, count);
		boolean alreadyLiked = replyLikeDao.check(memberId, replyNo);
		return new ReplyLikeVO(alreadyLiked, count);
	}
}
