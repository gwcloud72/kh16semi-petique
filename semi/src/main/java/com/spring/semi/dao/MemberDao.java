package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.MemberDto;
import com.spring.semi.mapper.MemberMapper;
import com.spring.semi.vo.PageVO;


/**
 * MemberDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class MemberDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private MemberMapper memberMapper;

	public void insert(MemberDto memberDto) {
		String sql = "insert into " + "member(member_id, member_pw, member_nickname, member_email, "
				+ "member_description, member_auth) " + "values(?, ?, ?, ?, ?, ?)";

		Object[] params = { memberDto.getMemberId(), memberDto.getMemberPw(), memberDto.getMemberNickname(),
				memberDto.getMemberEmail(), memberDto.getMemberDescription(), memberDto.getMemberAuth() };

		jdbcTemplate.update(sql, params);
	}

	public boolean delete(String member_id) {
		String sql = "delete from member where member_id = ?";
		Object[] params = { member_id };
		return jdbcTemplate.update(sql, params) > 0;
	}

	public boolean updateForUser(MemberDto memberDto) {
		String sql = "update member set member_nickname = ?, member_email = ?, "
				+ "member_description = ?, member_auth = ? "
				+ "where member_id = ?";
		Object[] params = { memberDto.getMemberNickname(), memberDto.getMemberEmail(), memberDto.getMemberDescription(),
				memberDto.getMemberAuth(), memberDto.getMemberId() };

		return jdbcTemplate.update(sql, params) > 0;
	}

	public boolean updateForUserPassword(String member_pw, String member_id) {
		String sql = "update member set member_pw = ?, member_change = systimestamp where member_id = ?";
		Object[] params = { member_pw, member_id };

		return jdbcTemplate.update(sql, params) > 0;
	}

	public void updateForLogin(String member_id) {
		String sql = "update member set member_login = systimestamp where member_id = ?";
		Object[] params = {member_id};
		jdbcTemplate.update(sql, params);
	}

	public void updateForAdmin(MemberDto findDto) {
		String sql = "update member set member_nickname = ?, member_description = ?, member_point = ? "
																+ "where member_id = ?";

		Object[] params = {
				findDto.getMemberNickname(),
				findDto.getMemberDescription(),
				findDto.getMemberPoint(),
				findDto.getMemberId()
				};

		jdbcTemplate.update(sql, params);

	}

	public MemberDto selectOne(String member_id) {
		String sql = "select * from member where member_id = ?";
		Object[] params = { member_id };
		List<MemberDto> list = jdbcTemplate.query(sql, memberMapper, params);
		return list.isEmpty() ? null : list.get(0);
	}

	public MemberDto selectForEmail(String memberEmail) {
		String sql = "select * from member where member_email = ?";
		Object[] params = {memberEmail};
		List<MemberDto> list = jdbcTemplate.query(sql, memberMapper, params);
		return list.isEmpty() ? null : list.get(0);
	}

	public MemberDto selectForNickname(String memberNickname) {
		String sql = "select * from member where member_nickname = ?";
		Object[] params = {memberNickname};
		List<MemberDto> list = jdbcTemplate.query(sql, memberMapper, params);
		return list.isEmpty() ? null : list.get(0);
	}

	public List<MemberDto> selectList(){
		String sql = "select * from member";

		return jdbcTemplate.query(sql, memberMapper);
	}

	public void connect(String member_id, int media_no) {
		String sql = "insert into member_profile values(?, ?)";
		Object[] params = { member_id, media_no };
		jdbcTemplate.update(sql, params);
	}

	public int findMediaNo(String member_id) {
		String sql = "select media_no from member_profile where member_id = ?";
		Object[] params = { member_id };
		return jdbcTemplate.queryForObject(sql, int.class, params);
	}


	public List<MemberDto> selectListByMemberPoint(int min, int max)
	{
		String sql = "select * from ("
				+ "select rownum rn, TMP.* from ("
				+ "select * from member order by member_point desc"
				+ ")TMP) where rn between ? and ?";
		Object[] params = {min, max};
		return jdbcTemplate.query(sql, memberMapper, params);
	}

	private String allowedColumn(String column) {
		if ("member_id".equals(column)) return "member_id";
		if ("member_nickname".equals(column)) return "member_nickname";
		if ("member_email".equals(column)) return "member_email";
		return null;
	}


		public void addPoint(String memberId, int point) {
			String sql = "update member set member_point = member_point + ? "
					+ "where member_id = ?";
			Object[] params = {point, memberId};
			jdbcTemplate.update(sql, params);

		}


		public void minusPoint(String memberId, int point) {
			String sql = "update member set member_point = member_point - ? "
					+ "where member_id = ?";
			Object[] params = {point, memberId};
			jdbcTemplate.update(sql, params);
		}


	public int count(PageVO pageVO) {
		if (pageVO == null || pageVO.isList()) {
			String sql = "select count(*) from member where member_level != 2";
			return jdbcTemplate.queryForObject(sql, int.class);
		}

		String column = allowedColumn(pageVO.getColumn());
		String keyword = pageVO.getKeyword();
		if (column == null || keyword == null || keyword.isBlank()) {
			String sql = "select count(*) from member where member_level != 2";
			return jdbcTemplate.queryForObject(sql, int.class);
		}

		String sql = "select count(*) from member where member_level != 2 and instr(" + column + ", ?) > 0";
		return jdbcTemplate.queryForObject(sql, int.class, keyword);
	}

	public List<MemberDto> selectListForPaging(PageVO pageVO){
		if (pageVO == null) return List.of();

		String column = allowedColumn(pageVO.getColumn());
		String keyword = pageVO.getKeyword();
		boolean hasKeyword = !pageVO.isList() && column != null && keyword != null && !keyword.isBlank();

		StringBuilder sql = new StringBuilder();
		java.util.List<Object> params = new java.util.ArrayList<>();

		sql.append("select * from (");
		sql.append("  select rownum rn, TMP.* from (");
		sql.append("    select * from member ");
		sql.append("    where member_level != 2 ");
		if (hasKeyword) {
			sql.append("      and instr(").append(column).append(", ?) > 0 ");
			params.add(keyword);
		}
		sql.append("    order by member_id asc ");
		sql.append("  ) TMP");
		sql.append(") where rn between ? and ?");
		params.add(pageVO.getBegin());
		params.add(pageVO.getEnd());
		return jdbcTemplate.query(sql.toString(), memberMapper, params.toArray());
	}

	public int usePoint(String loginId) {
		String sql = "update member set member_used_point =  member_used_point + NVL(member_point,0) , member_point = 0 where member_id = ? ";
		Object[] params = {loginId};
		return jdbcTemplate.update(sql, params);

	}


	public List<String> selectIdListForNotice(String excludeId) {
		if (excludeId == null) {
			String sql = "select member_id from member where member_level != 0 and member_level != 2 order by member_id asc";
			return jdbcTemplate.queryForList(sql, String.class);
		}
		String sql = "select member_id from member where member_level != 0 and member_level != 2 and member_id <> ? order by member_id asc";
		Object[] params = { excludeId };
		return jdbcTemplate.queryForList(sql, String.class, params);
	}


}
