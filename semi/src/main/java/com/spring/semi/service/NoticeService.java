package com.spring.semi.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.semi.dao.CategoryDao;
import com.spring.semi.dao.HeaderDao;
import com.spring.semi.dao.MailDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dao.NoticePinDao;
import com.spring.semi.dto.CategoryDto;
import com.spring.semi.dto.HeaderDto;
import com.spring.semi.dto.MailDto;
import com.spring.semi.dto.NoticePinDto;


/**
 * NoticeService - 비즈니스 로직을 담당하는 서비스.
 */
@Service
public class NoticeService {

	@Autowired
	private HeaderDao headerDao;
	@Autowired
	private NoticePinDao noticePinDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private MailDao mailDao;
	@Autowired
	private CategoryDao categoryDao;

	public boolean isNotice(Integer typeHeaderNo) {
		if (typeHeaderNo == null) return false;
		HeaderDto header = headerDao.selectOne(typeHeaderNo, "type");
		return header != null && "공지".equals(header.getHeaderName());
	}

	public NoticePinDto selectPin(int boardNo) {
		return noticePinDao.selectOne(boardNo);
	}

	public void afterWrite(int boardNo, Integer typeHeaderNo, Integer pinOrder, String pinStart, String pinEnd,
			String senderId, int categoryNo, String categoryName, String boardTitle) {
		boolean notice = isNotice(typeHeaderNo);
		if (!notice) {
			noticePinDao.delete(boardNo);
			return;
		}
		applyPin(boardNo, pinOrder, pinStart, pinEnd);
		sendNoticeMail(senderId, categoryNo, categoryName, boardNo, boardTitle);
	}

	public void afterEdit(int boardNo, Integer beforeTypeHeaderNo, Integer afterTypeHeaderNo, Integer pinOrder,
			String pinStart, String pinEnd, String senderId, int categoryNo, String categoryName, String boardTitle) {
		boolean beforeNotice = isNotice(beforeTypeHeaderNo);
		boolean afterNotice = isNotice(afterTypeHeaderNo);

		if (!afterNotice) {
			noticePinDao.delete(boardNo);
			return;
		}

		applyPin(boardNo, pinOrder, pinStart, pinEnd);
		if (!beforeNotice) {
			sendNoticeMail(senderId, categoryNo, categoryName, boardNo, boardTitle);
		}
	}

	private void applyPin(int boardNo, Integer pinOrder, String pinStart, String pinEnd) {
		int order = normalizeOrder(pinOrder);
		Timestamp startTs = parseDate(pinStart, false);
		Timestamp endTs = parseDate(pinEnd, true);

		if (startTs != null && endTs != null && startTs.after(endTs)) {
			endTs = Timestamp.valueOf(startTs.toLocalDateTime().toLocalDate().atTime(LocalTime.MAX));
		}

		boolean noValue = startTs == null && endTs == null && order == 9999;
		if (noValue) {
			noticePinDao.delete(boardNo);
			return;
		}
		noticePinDao.merge(boardNo, startTs, endTs, order);
	}

	private int normalizeOrder(Integer pinOrder) {
		if (pinOrder == null) return 9999;
		int v = pinOrder.intValue();
		if (v < 1) return 9999;
		if (v > 9999) return 9999;
		return v;
	}

	private Timestamp parseDate(String value, boolean endOfDay) {
		if (value == null) return null;
		String v = value.trim();
		if (v.isEmpty()) return null;
		LocalDate date;
		try {
			date = LocalDate.parse(v);
		} catch (Exception e) {
			return null;
		}
		if (endOfDay) {
			return Timestamp.valueOf(date.atTime(LocalTime.MAX));
		}
		return Timestamp.valueOf(date.atStartOfDay());
	}

	private void sendNoticeMail(String senderId, int categoryNo, String categoryName, int boardNo, String boardTitle) {
		String name = categoryName;
		if (name == null || name.isBlank()) {
			CategoryDto dto = categoryDao.selectOne(categoryNo);
			name = dto != null ? dto.getCategoryName() : "";
		}

		String path = buildDetailPath(categoryNo, categoryName, boardNo);
		String title = "[공지] " + name;
		String content;
		if (boardTitle == null || boardTitle.isBlank()) {
			content = path;
		} else {
			content = boardTitle + "\n" + path;
		}

		List<String> targets = memberDao.selectIdListForNotice(senderId);
		for (String target : targets) {
			MailDto dto = MailDto.builder()
					.mailNo(mailDao.sequence())
					.mailSender(senderId)
					.mailTarget(target)
					.mailTitle(title)
					.mailContent(content)
					.build();
			mailDao.insertForTarget(dto);
		}
	}

	private String buildDetailPath(int categoryNo, String categoryName, int boardNo) {
		if (categoryName != null && !categoryName.isBlank()) {
			String encoded = URLEncoder.encode(categoryName, StandardCharsets.UTF_8);
			return "/board/" + encoded + "/detail?boardNo=" + boardNo;
		}
		switch (categoryNo) {
		case 1:
			return "/board/community/detail?boardNo=" + boardNo;
		case 2:
			return "/board/info/detail?boardNo=" + boardNo;
		case 3:
			return "/board/petfluencer/detail?boardNo=" + boardNo;
		case 4:
			return "/board/adoption/detail?boardNo=" + boardNo;
		case 5:
			return "/board/review/detail?boardNo=" + boardNo;
		case 7:
			return "/board/animal/detail?boardNo=" + boardNo;
		case 24:
			return "/board/fun/detail?boardNo=" + boardNo;
		default:
			return "/board/community/detail?boardNo=" + boardNo;
		}
	}
}
