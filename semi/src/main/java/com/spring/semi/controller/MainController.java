package com.spring.semi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.semi.dao.BoardDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.MemberDto;
import com.spring.semi.vo.BoardVO;

import jakarta.servlet.http.HttpSession;


/**
 * MainController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
public class MainController
{

    private final Map<String, List<BoardVO>> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTime = new ConcurrentHashMap<>();
    private static final long CACHE_LIFETIME = 1000 * 60 * 5;

	@Autowired
	private BoardDao boardDao;
	@Autowired
	private MemberDao memberDao;


	@RequestMapping("/")
	public String home(
			Model model,
			HttpSession session
			)
	{
		String loginId = (String) session.getAttribute("loginId");


        String[] boards = {"community_board_list", "petfluencer_board_list", "fun_board_list", "animal_wiki_board_list", "review_board_scroll", "review_board_list"};
        if(loginId != null) {
        	MemberDto memberDto = memberDao.selectOne(loginId);
        	if(memberDto != null) model.addAttribute("memberDto", memberDto);
        }
        for (String boardCategoryName : boards) {
            List<BoardVO> list = getBoardList(boardCategoryName);
            model.addAttribute(boardCategoryName, list);
        }

        return "/WEB-INF/views/home.jsp";
	}

	private List<BoardVO> getBoardList(String boardCategoryName) {
	    if (!isCacheExpired(boardCategoryName)) {

	        return cache.get(boardCategoryName);
	    }


	    List<BoardVO> list;
	    switch (boardCategoryName) {
	        case "community_board_list":
	            list = boardDao.selectListWithPagingForMainPage(1, 1, 8);
	            break;
	        case "petfluencer_board_list":
	            list = boardDao.selectListWithPagingForMainPage(3, 1, 10);
	            break;
	        case "fun_board_list":
	            list = boardDao.selectListWithPagingForMainPage(24, 1, 8);
	            break;
	        case "animal_wiki_board_list":
	            list = boardDao.selectListWithPagingForMainPage(7, 1, 6);
	            break;
	        case "review_board_scroll":
	            List<BoardVO> reviewItems = boardDao.selectListWithPagingForMainPage(5, 1, 3);
	            List<BoardVO> reviewScroll = new ArrayList<>();
	            reviewScroll.addAll(reviewItems);
	            reviewScroll.addAll(reviewItems);
	            list = reviewScroll;
	            break;
	        case "review_board_list":
	            list = boardDao.selectListWithPagingForMainPage(5, 4, 7);
	            break;
	        default:
	            list = new ArrayList<>();
	    }


	    cache.put(boardCategoryName, list);
	    cacheTime.put(boardCategoryName, System.currentTimeMillis());

	    return list;
	}


	private boolean isCacheExpired(String boardCategoryName) {
	    if (!cache.containsKey(boardCategoryName) || !cacheTime.containsKey(boardCategoryName)) {

	        return true;
	    }

	    long now = System.currentTimeMillis();
	    long lastUpdate = cacheTime.get(boardCategoryName);


	    return now - lastUpdate > CACHE_LIFETIME;
	}


	public void clearBoardCache(String boardCategoryName) {
        cache.remove(boardCategoryName);
        cacheTime.remove(boardCategoryName);

    }
}
