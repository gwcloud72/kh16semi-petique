package com.spring.semi.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.BoardDto;
import com.spring.semi.mapper.BoardDetailVOMapper;
import com.spring.semi.mapper.BoardListVOMapper;
import com.spring.semi.mapper.BoardMapper;
import com.spring.semi.mapper.BoardVOMapper;
import com.spring.semi.mapper.MemberBoardListVOMapper;
import com.spring.semi.vo.BoardDetailVO;
import com.spring.semi.vo.BoardListVO;
import com.spring.semi.vo.BoardVO;
import com.spring.semi.vo.MemberBoardListVO;
import com.spring.semi.vo.PageVO;


/**
 * BoardDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class BoardDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private BoardMapper boardMapper;
	@Autowired
	private BoardListVOMapper boardListVOMapper;
	@Autowired
	private BoardVOMapper boardVOMapper;
	@Autowired
	private BoardDetailVOMapper boardDetailVOMapper;
	@Autowired
	private MemberBoardListVOMapper memberBoardListVOMapper;

	public int sequence() {
		String sql = "select board_seq.nextval from dual";
		return jdbcTemplate.queryForObject(sql, int.class);
	}


	public void insert(BoardDto boardDto, int boardType) {
		String sql = "insert into board ("
				+ "board_category_no, board_no, board_writer, board_title, board_content, board_type_header, board_animal_header"
				+ ") values (?, ?, ?, ?, ?, ?, ?)";

		Object[] params = { boardType, boardDto.getBoardNo(), boardDto.getBoardWriter(), boardDto.getBoardTitle(),
				boardDto.getBoardContent(), boardDto.getBoardTypeHeader(), boardDto.getBoardAnimalHeader() };

		jdbcTemplate.update(sql, params);
	}

	public void insertForReview(BoardDto boardDto, int boardType) {
		String sql = "insert into board (" + "board_category_no, board_no, board_writer, board_title, board_content, "
				+ "board_animal_header, board_type_header, board_score" + ") values (?, ?, ?, ?, ?, ?, ?, ?)";

		Object[] params = { boardType, boardDto.getBoardNo(), boardDto.getBoardWriter(), boardDto.getBoardTitle(),
				boardDto.getBoardContent(), boardDto.getBoardAnimalHeader(), boardDto.getBoardTypeHeader(),
				boardDto.getBoardScore() };

		jdbcTemplate.update(sql, params);
	}


	public List<BoardDto> searchList(String column, String keyword) {
		Set<String> allowList = Set.of("board_title", "board_writer", "board_content");
		if (!allowList.contains(column)) return List.of();
		if (keyword == null) return List.of();

		String sql;
		if ("board_title".equals(column)) {
			sql = "select * from board where instr(board_title, ?) > 0 and deleted = 0 order by board_no desc";
		}
		else if ("board_writer".equals(column)) {
			sql = "select * from board where instr(board_writer, ?) > 0 and deleted = 0 order by board_no desc";
		}
		else {
			sql = "select * from board where instr(board_content, ?) > 0 and deleted = 0 order by board_no desc";
		}

		Object[] params = { keyword };
		return jdbcTemplate.query(sql, boardMapper, params);
	}


	public BoardDto selectOne(int boardNo) {
		String sql = "SELECT * FROM board WHERE board_no=? AND deleted = 0";
		Object[] params = { boardNo };
		List<BoardDto> list = jdbcTemplate.query(sql, boardMapper, params);
		return list.isEmpty() ? null : list.get(0);
	}


	public boolean delete(int boardNo) {
		String sql = "update board set deleted = 1 where board_no = ?";
		Object[] params = { boardNo };
		return jdbcTemplate.update(sql, params) > 0;
	}


	public boolean mypageDelete(int boardNo) {
		return delete(boardNo);
	}


	public List<BoardListVO> selectDeletedByMemberId(String login_id) {
		String sql = "select board_no, board_title, board_wtime, board_view, category_name from board "
				+ "join category on category_no = board_category_no " + "where board_writer = ? and deleted = 1 "
				+ "order by board_wtime desc";
		Object[] params = { login_id };
		return jdbcTemplate.query(sql, boardListVOMapper, params);
	}


	public boolean update(BoardDto boardDto) {
		String sql = "update board set board_title=?, board_content=?, board_type_header=?, board_animal_header=?, board_etime=systimestamp "
				+ "where board_no=?";
		Object[] params = { boardDto.getBoardTitle(), boardDto.getBoardContent(), boardDto.getBoardTypeHeader(),
				boardDto.getBoardAnimalHeader(), boardDto.getBoardNo() };
		return jdbcTemplate.update(sql, params) > 0;
	}

	public boolean updateForReview(BoardDto boardDto) {
		String sql = "update board set board_title=?, board_content=?, board_etime=systimestamp, "
				+ "board_animal_header=?, board_type_header=?, board_score=? " + "where board_no=?";
		Object[] params = { boardDto.getBoardTitle(), boardDto.getBoardContent(), boardDto.getBoardAnimalHeader(),
				boardDto.getBoardTypeHeader(), boardDto.getBoardScore(), boardDto.getBoardNo() };
		return jdbcTemplate.update(sql, params) > 0;
	}

	private String allowedSearchColumn(String column) {
		if ("board_title".equals(column)) return "board_title";
		if ("board_writer".equals(column)) return "board_writer";
		if ("board_content".equals(column)) return "board_content";
		if ("header_name".equals(column)) return "header_name";
		return null;
	}


	public int count(PageVO pageVO, int pageType) {
		if (pageVO == null || pageVO.isList()) {
			String sql = "select count(*) from board where board_category_no = ? and deleted = 0";
			Object[] params = { pageType };
			return jdbcTemplate.queryForObject(sql, int.class, params);
		}

		String keyword = pageVO.getKeyword();
		if (keyword == null || keyword.isBlank()) {
			String sql = "select count(*) from board where board_category_no = ? and deleted = 0";
			Object[] params = { pageType };
			return jdbcTemplate.queryForObject(sql, int.class, params);
		}

		if ("header_name".equalsIgnoreCase(pageVO.getColumn())) {
			String sql = "select count(*) from board b "
					+ "left join header h on b.board_header = h.header_no "
					+ "where b.board_category_no = ? and b.deleted = 0 and instr(h.header_name, ?) > 0";
			Object[] params = { pageType, keyword };
			return jdbcTemplate.queryForObject(sql, int.class, params);
		}

		String column = allowedSearchColumn(pageVO.getColumn());
		if (column == null) {
			String sql = "select count(*) from board where board_category_no = ? and deleted = 0";
			Object[] params = { pageType };
			return jdbcTemplate.queryForObject(sql, int.class, params);
		}

		String sql = "select count(*) from board "
				+ "where board_category_no = ? and deleted = 0 "
				+ "and instr(" + column + ", ?) > 0";
		Object[] params = { pageType, keyword };
		return jdbcTemplate.queryForObject(sql, int.class, params);
	}


	public List<BoardVO> selectListWithPaging(PageVO pageVO, int pageType) {
		if (pageVO == null) return List.of();

		String column = allowedSearchColumn(pageVO.getColumn());
		String keyword = pageVO.getKeyword();
		boolean hasKeyword = !pageVO.isList() && column != null && keyword != null && !keyword.isBlank();

		StringBuilder sql = new StringBuilder();
		java.util.List<Object> params = new java.util.ArrayList<>();

		sql.append("select * from (");
		sql.append("  select rownum rn, TMP.* from (");
		sql.append("    select * from board_header_view ");
		sql.append("    where board_category_no = ? and deleted = 0 ");
		params.add(pageType);

		if (hasKeyword) {
			sql.append("      and instr(").append(column).append(", ?) > 0 ");
			params.add(keyword);
		}

		sql.append("    order by board_no desc ");
		sql.append("  ) TMP");
		sql.append(") where rn between ? and ?");
		params.add(pageVO.getBegin());
		params.add(pageVO.getEnd());
		return jdbcTemplate.query(sql.toString(), boardVOMapper, params.toArray());
	}


	public boolean updateBoardLike(int boardNo, int boardLike) {
		String sql = "update board set board_like = ? where board_no=?";
		Object[] params = { boardLike, boardNo };
		return jdbcTemplate.update(sql, params) > 0;
	}


    public boolean updateBoardLike(int board_no) {
		String sql = "update board "
							+ "set board_like = (select count(*) from board_like where board_no = ?) "
							+ "where board_no = ?";
		Object[] params = {board_no, board_no};
		return jdbcTemplate.update(sql, params) > 0;
	}


    public boolean updateBoardView(int boardNo) {
        String sql = "update board set board_view=board_view+1 where board_no=?";
        Object[] params = { boardNo };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public List<BoardListVO> selectListByWriteTime(int min, int max) {
        String sql = "select * from (" +
                     "select rownum rn, TMP.* from (" +
                     "select board_no, board_title, board_wtime, board_view, category_name " +
                     "from board join category on category_no = board_category_no " +
                     "where deleted = 0 " +
                     "order by board_wtime desc" +
                     ") TMP) where rn between ? and ?";
        Object[] params = { min, max };
        return jdbcTemplate.query(sql, boardListVOMapper, params);
    }


    public List<BoardDto> selectList(int min, int max, String orderBy, int categoryNo) {
        String orderColumn;
        switch (orderBy) {
        case "view":
            orderColumn = "board_view";
            break;
        case "like":
            orderColumn = "board_like";
            break;
        case "wtime":
        default:
            orderColumn = "board_wtime";
            break;
        }

        String sql = "SELECT * FROM ( " +
                     "  SELECT rownum rn, TMP.* FROM ( " +
                     "    SELECT * FROM board " +
                     "    WHERE board_category_no = ? AND deleted = 0 " +
                     "    ORDER BY " + orderColumn + " DESC " +
                     "  ) TMP " +
                     ") WHERE rn BETWEEN ? AND ?";

        Object[] params = { categoryNo, min, max };
        return jdbcTemplate.query(sql, boardMapper, params);
    }

    public List<BoardVO> selectList2(int min, int max, String orderBy, int categoryNo) {
        List<String> allows = List.of("view", "like", "wtime");
        if (!allows.contains(orderBy)) return List.of();

        String orderColumn;
        switch (orderBy) {
        case "view":
            orderColumn = "board_view";
            break;
        case "like":
            orderColumn = "board_like";
            break;
        case "wtime":
        default:
            orderColumn = "board_wtime";
            break;
        }

        String sql = "SELECT * FROM ( " +
                     "    SELECT rownum rn, TMP.* FROM ( " +
                     "        select * from board_header_view " +
                     "        WHERE board_category_no = ? AND deleted = 0 " +
                     "        ORDER BY " + orderColumn + " DESC " +
                     "    ) TMP " +
                     ") WHERE rn BETWEEN ? AND ?";

        Object[] params = { categoryNo, min, max };
        return jdbcTemplate.query(sql, boardVOMapper, params);
    }


    public void connect(int boardNo, int mediaNo) {
        String sql = "insert into board_image (board_no, media_no) values (?, ?)";
        Object[] params = { boardNo, mediaNo };
        jdbcTemplate.update(sql, params);
    }

    public void connect_video(int boardNo, int videoNo) {
        String sql = "insert into board_video (board_no, video_no) values (?, ?)";
        Object[] params = { boardNo, videoNo };
        jdbcTemplate.update(sql, params);
    }

    public int findMedia(int boardNo) {
        String sql = "select media_no from board_image where board_no = ?";
        Object[] params = { boardNo };
        return jdbcTemplate.queryForObject(sql, int.class, params);
    }

    public int findVideo(int boardNo) {
        String sql = "select video_no from board_video where board_no = ?";
        Object[] params = { boardNo };
        return jdbcTemplate.queryForObject(sql, int.class, params);
    }


    public List<BoardVO> selectListWithPagingForMainPage(int pageType, int min, int max) {
        String sql = "select * from (" +
                     "  select rownum rn, TMP.* from (" +
                     "   select * from board_header_view " +
                     "    where board_category_no=? AND deleted = 0 " +
                     "    order by board_no desc" +
                     "  ) TMP" +
                     ") where rn between ? and ?";
        Object[] params = { pageType, min, max };
        return jdbcTemplate.query(sql, boardVOMapper, params);
    }


    public List<BoardListVO> selectByMemberId(String login_id) {
        String sql = "select board_no, board_title, board_wtime, board_view, category_name from board " +
                     "join category on category_no = board_category_no " +
                     "where board_writer = ? and deleted = 0 " +
                     "order by board_wtime desc";
        Object[] params = { login_id };
        return jdbcTemplate.query(sql, boardListVOMapper, params);
    }

	private String allowedMemberBoardColumn(String column) {
		if ("board_title".equals(column)) return "b.board_title";
		return null;
	}

	public int countByMemberId(PageVO pageVO, String memberId, boolean deleted) {
		int deletedValue = deleted ? 1 : 0;
		String column = allowedMemberBoardColumn(pageVO.getColumn());
		if (pageVO.isList() || column == null || pageVO.getKeyword() == null) {
			String sql = "select count(*) from board b where b.board_writer = ? and b.deleted = ?";
			Object[] params = { memberId, deletedValue };
			return jdbcTemplate.queryForObject(sql, int.class, params);
		}
		String sql = "select count(*) from board b where b.board_writer = ? and b.deleted = ? and instr(" + column + ", ?) > 0";
		Object[] params = { memberId, deletedValue, pageVO.getKeyword() };
		return jdbcTemplate.queryForObject(sql, int.class, params);
	}

	public List<MemberBoardListVO> selectByMemberIdWithPaging(PageVO pageVO, String memberId, boolean deleted) {
		int deletedValue = deleted ? 1 : 0;
		String column = allowedMemberBoardColumn(pageVO.getColumn());

		StringBuilder sql = new StringBuilder();
		sql.append("select * from (");
		sql.append("  select rownum rn, TMP.* from (");
		sql.append("    select b.board_no, b.board_category_no, c.category_name, ");
		sql.append("           case b.board_category_no ");
		sql.append("             when 1 then 'community' ");
		sql.append("             when 2 then 'info' ");
		sql.append("             when 3 then 'petfluencer' ");
		sql.append("             when 4 then 'adoption' ");
		sql.append("             when 5 then 'review' ");
		sql.append("             when 7 then 'animal' ");
		sql.append("             when 24 then 'fun' ");
		sql.append("             else 'community' ");
		sql.append("           end as category_key, ");
		sql.append("           b.board_title, b.board_wtime, b.board_view ");
		sql.append("    from board b join category c on c.category_no = b.board_category_no ");
		sql.append("    where b.board_writer = ? and b.deleted = ? ");
		List<Object> params = new ArrayList<>();
		params.add(memberId);
		params.add(deletedValue);

		if (!pageVO.isList() && column != null && pageVO.getKeyword() != null) {
			sql.append(" and instr(" + column + ", ?) > 0 ");
			params.add(pageVO.getKeyword());
		}

		sql.append("    order by b.board_wtime desc, b.board_no desc");
		sql.append("  ) TMP");
		sql.append(") where rn between ? and ?");
		params.add(pageVO.getBegin());
		params.add(pageVO.getEnd());

		return jdbcTemplate.query(sql.toString(), memberBoardListVOMapper, params.toArray());
	}


  public BoardDetailVO selectOneDetail(int boardNo) {
	    String sql = "SELECT b.board_category_no, b.board_no, b.board_title, b.board_content, b.board_writer, "
	            + "b.board_wtime, b.board_etime, b.board_like, b.board_view, b.board_reply, "
	            + "ah.header_name AS animal_header_name, "
	            + "th.header_name AS type_header_name, "
	            + "b.board_score, b.deleted, "
	            + "m.member_nickname, ml.level_name, ml.badge_image "
	            + "FROM board b "
	            + "LEFT JOIN member m ON b.board_writer = m.member_id "
	            + "LEFT JOIN member_level_table ml ON m.member_level = ml.level_no "
	            + "LEFT JOIN animal_header ah ON b.board_animal_header = ah.header_no "
	            + "LEFT JOIN type_header th ON b.board_type_header = th.header_no "
	            + "WHERE b.board_no = ? AND b.deleted = 0";

	    Object[] params = { boardNo };
	    List<BoardDetailVO> list = jdbcTemplate.query(sql, boardDetailVOMapper, params);
	    return list.isEmpty() ? null : list.get(0);
	}


	public List<BoardDetailVO> selectListDetail(int min, int max, int categoryNo, String orderBy) {
		String orderColumn;
		switch (orderBy) {
		case "view":
			orderColumn = "b.board_view";
			break;
		case "like":
			orderColumn = "b.board_like";
			break;
		case "wtime":
		default:
			orderColumn = "b.board_wtime";
			break;
		}

		String sql = "SELECT * FROM ("
		        + "  SELECT rownum rn, TMP.* FROM ("
		        + "    SELECT b.board_category_no, b.board_no, b.board_title, b.board_content, b.board_writer, "
		        + "           b.board_wtime, b.board_etime, b.board_like, b.board_view, b.board_reply, "
		        + "           ah.header_name AS animal_header_name, "
		        + "           th.header_name AS type_header_name, "
		        + "           b.board_score, b.deleted, "
		        + "           m.member_nickname, ml.level_name, ml.badge_image "
		        + "    FROM board b "
		        + "    LEFT JOIN member m ON b.board_writer = m.member_id "
		       + "    LEFT JOIN member_level_table ml ON m.member_level = ml.level_no "
		        + "    LEFT JOIN animal_header ah ON b.board_animal_header = ah.header_no "
		        + "    LEFT JOIN type_header th ON b.board_type_header = th.header_no "
		        + "    WHERE b.board_category_no = ? AND b.deleted = 0 "
		        + "    ORDER BY " + orderColumn + " DESC"
		        + "  ) TMP"
		        + ") WHERE rn BETWEEN ? AND ?";


		Object[] params = { categoryNo, min, max };
		return jdbcTemplate.query(sql, boardDetailVOMapper, params);
	}


	public List<BoardDetailVO> selectNoticeTop3(int categoryNo) {
		String sql = "select * from ("
				+ "select TMP.* from ("
				+ "select b.board_category_no, b.board_no, b.board_title, b.board_content, b.board_writer, "
				+ "b.board_wtime, b.board_etime, b.board_like, b.board_view, b.board_reply, "
				+ "ah.header_name AS animal_header_name, "
				+ "th.header_name AS type_header_name, "
				+ "b.board_score, b.deleted, "
				+ "m.member_nickname, ml.level_name, ml.badge_image "
				+ "from board b "
				+ "left join notice_pin np on b.board_no = np.board_no "
				+ "left join member m on b.board_writer = m.member_id "
				+ "left join member_level_table ml on m.member_level = ml.level_no "
				+ "left join animal_header ah on b.board_animal_header = ah.header_no "
				+ "left join type_header th on b.board_type_header = th.header_no "
				+ "where b.board_category_no = ? and b.deleted = 0 and th.header_name = '공지' "
				+ "and (np.pin_start is null or np.pin_start <= systimestamp) "
				+ "and (np.pin_end is null or np.pin_end >= systimestamp) "
				+ "order by nvl(np.pin_order, 9999) asc, b.board_wtime desc, b.board_no desc"
				+ " ) TMP where rownum <= 3"
				+ " )";
		Object[] params = { categoryNo };
		return jdbcTemplate.query(sql, boardDetailVOMapper, params);
	}

	public int countWithoutNotice(int categoryNo) {
		String sql = "select count(*) "
				+ "from board b "
				+ "left join type_header th on b.board_type_header = th.header_no "
				+ "where b.board_category_no = ? and b.deleted = 0 "
				+ "and (th.header_name is null or th.header_name <> '공지')";
		Object[] params = { categoryNo };
		return jdbcTemplate.queryForObject(sql, int.class, params);
	}

	public List<BoardDetailVO> selectListDetailWithoutNotice(int min, int max, int categoryNo, String orderBy) {
		String orderColumn;
		switch (orderBy) {
		case "view":
			orderColumn = "b.board_view";
			break;
		case "like":
			orderColumn = "b.board_like";
			break;
		case "wtime":
		default:
			orderColumn = "b.board_wtime";
			break;
		}

		String sql = "SELECT * FROM ("
				+ "  SELECT rownum rn, TMP.* FROM ("
				+ "    SELECT b.board_category_no, b.board_no, b.board_title, b.board_content, b.board_writer, "
				+ "           b.board_wtime, b.board_etime, b.board_like, b.board_view, b.board_reply, "
				+ "           ah.header_name AS animal_header_name, "
				+ "           th.header_name AS type_header_name, "
				+ "           b.board_score, b.deleted, "
				+ "           m.member_nickname, ml.level_name, ml.badge_image "
				+ "    FROM board b "
				+ "    LEFT JOIN member m ON b.board_writer = m.member_id "
				+ "    LEFT JOIN member_level_table ml ON m.member_level = ml.level_no "
				+ "    LEFT JOIN animal_header ah ON b.board_animal_header = ah.header_no "
				+ "    LEFT JOIN type_header th ON b.board_type_header = th.header_no "
				+ "    WHERE b.board_category_no = ? AND b.deleted = 0 "
				+ "      AND (th.header_name is null or th.header_name <> '공지') "
				+ "    ORDER BY " + orderColumn + " DESC, b.board_no desc"
				+ "  ) TMP"
				+ ") WHERE rn BETWEEN ? AND ?";

		Object[] params = { categoryNo, min, max };
		return jdbcTemplate.query(sql, boardDetailVOMapper, params);
	}

}
