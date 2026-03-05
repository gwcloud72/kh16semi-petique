package com.spring.semi.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.MailDto;
import com.spring.semi.mapper.MailVOMapper;
import com.spring.semi.mapper.MailDetailMapper;
import com.spring.semi.mapper.MailMapper;
import com.spring.semi.vo.MailDetailVO;
import com.spring.semi.vo.MailVO;
import com.spring.semi.vo.PageVO;


/**
 * MailDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class MailDao {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	private MailMapper mailMapper;
	@Autowired
	private MailVOMapper mailVOMapper;
	@Autowired
	private MailDetailMapper mailDetailMapper;

	private String resolveSearchColumn(String column) {
		if ("mail_title".equals(column)) return "m.mail_title";
		if ("mail_content".equals(column)) return "m.mail_content";
		if ("mail_sender".equals(column)) return "m.mail_sender";
		if ("mail_target".equals(column)) return "m.mail_target";
		if ("sender_nickname".equals(column)) return "s.member_nickname";
		if ("target_nickname".equals(column)) return "t.member_nickname";
		return "m.mail_title";
	}
	public int sequence() {
		String sql = "select mail_seq.nextval from dual";
		return jdbcTemplate.queryForObject(sql, int.class);
	}


	public void insertForSender(MailDto mailDto) {
		mailDto.setMailOwner(mailDto.getMailSender());
		insert(mailDto);
	}


	public void insertForTarget(MailDto mailDto) {
		mailDto.setMailOwner(mailDto.getMailTarget());
		insert(mailDto);
	}


	public void insert(MailDto mailDto) {
		String sql = "insert into mail(mail_no, mail_owner ,mail_sender, mail_target, mail_title, mail_content) "
				+ "values(?, ?, ?, ?, ?, ?)";

		Object[] params = {
				mailDto.getMailNo(),
				mailDto.getMailOwner(),
				mailDto.getMailSender(),
				mailDto.getMailTarget(),
				mailDto.getMailTitle(),
				mailDto.getMailContent()
		};

		jdbcTemplate.update(sql, params);
	}

	public MailDto selectOne(int mailNo) {
		String sql = "select * from mail where mail_no = ?";
		Object[] params = {mailNo};

		List<MailDto> list = jdbcTemplate.query(sql, mailMapper, params);
		return list.isEmpty()? null : list.get(0);
	}


	public List<MailDto> selectList(String mailOwner){
		String sql = "select * from mail where mail_owner = ?";
		Object[] params = {mailOwner};

		return jdbcTemplate.query(sql, mailMapper, params);

	}


	public boolean delete(int mailNo) {
		String sql = "delete from mail where mail_no = ?";
		Object[] params = {mailNo};

		return jdbcTemplate.update(sql, params) > 0;
	}

	public int count(PageVO pageVO, String mailOwner) {
		if (mailOwner == null) return 0;
		String keyword = (pageVO == null ? null : pageVO.getKeyword());
		boolean hasKeyword = pageVO != null && !pageVO.isList() && keyword != null && !keyword.isBlank();
		String columnExpr = resolveSearchColumn(pageVO == null ? null : pageVO.getColumn());

		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();
		sql.append("select count(*) ");
		sql.append("from mail m ");
		sql.append("join member s on m.mail_sender = s.member_id ");
		sql.append("join member t on m.mail_target = t.member_id ");
		sql.append("where m.mail_owner = ? ");
		params.add(mailOwner);

		if (hasKeyword) {
			sql.append("and instr(").append(columnExpr).append(", ?) > 0 ");
			params.add(keyword);
		}

		return jdbcTemplate.queryForObject(sql.toString(), int.class, params.toArray());
	}

	public List<MailVO> selectListWithPaging(PageVO pageVO, String mailOwner) {
		if (pageVO == null) return List.of();
		if (mailOwner == null) return List.of();

		String keyword = pageVO.getKeyword();
		boolean hasKeyword = !pageVO.isList() && keyword != null && !keyword.isBlank();
		String columnExpr = resolveSearchColumn(pageVO.getColumn());

		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();

		sql.append("select * from (");
		sql.append("  select rownum rn, TMP.* from (");
		sql.append("    select ");
		sql.append("      m.mail_no, m.mail_owner, m.mail_sender, m.mail_target, ");
		sql.append("      m.mail_title, m.mail_wtime, ");
		sql.append("      s.member_nickname as sender_nickname, ");
		sql.append("      t.member_nickname as target_nickname ");
		sql.append("    from mail m ");
		sql.append("    join member s on m.mail_sender = s.member_id ");
		sql.append("    join member t on m.mail_target = t.member_id ");
		sql.append("    where m.mail_owner = ? ");
		params.add(mailOwner);

		if (hasKeyword) {
			sql.append("      and instr(").append(columnExpr).append(", ?) > 0 ");
			params.add(keyword);
		}

		sql.append("    order by m.mail_no desc ");
		sql.append("  ) TMP");
		sql.append(") where rn between ? and ?");
		params.add(pageVO.getBegin());
		params.add(pageVO.getEnd());
		return jdbcTemplate.query(sql.toString(), mailVOMapper, params.toArray());
	}

	public List<MailVO> selectListForSenderWithPaging(PageVO pageVO, String mailSender) {
		if (pageVO == null) return List.of();
		if (mailSender == null) return List.of();

		String keyword = pageVO.getKeyword();
		boolean hasKeyword = !pageVO.isList() && keyword != null && !keyword.isBlank();
		String columnExpr = resolveSearchColumn(pageVO.getColumn());

		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();

		sql.append("select * from (");
		sql.append("  select rownum rn, TMP.* from (");
		sql.append("    select ");
		sql.append("      m.mail_no, m.mail_owner, m.mail_sender, m.mail_target, ");
		sql.append("      m.mail_title, m.mail_wtime, ");
		sql.append("      s.member_nickname as sender_nickname, ");
		sql.append("      t.member_nickname as target_nickname ");
		sql.append("    from mail m ");
		sql.append("    join member s on m.mail_sender = s.member_id ");
		sql.append("    join member t on m.mail_target = t.member_id ");
		sql.append("    where m.mail_sender = ? and m.mail_sender = m.mail_owner ");
		params.add(mailSender);

		if (hasKeyword) {
			sql.append("      and instr(").append(columnExpr).append(", ?) > 0 ");
			params.add(keyword);
		}

		sql.append("    order by m.mail_no desc ");
		sql.append("  ) TMP");
		sql.append(") where rn between ? and ?");
		params.add(pageVO.getBegin());
		params.add(pageVO.getEnd());
		return jdbcTemplate.query(sql.toString(), mailVOMapper, params.toArray());
	}

	public List<MailVO> selectListForTargetWithPaging(PageVO pageVO, String mailTarget) {
		if (pageVO == null) return List.of();
		if (mailTarget == null) return List.of();

		String keyword = pageVO.getKeyword();
		boolean hasKeyword = !pageVO.isList() && keyword != null && !keyword.isBlank();
		String columnExpr = resolveSearchColumn(pageVO.getColumn());

		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();

		sql.append("select * from (");
		sql.append("  select rownum rn, TMP.* from (");
		sql.append("    select ");
		sql.append("      m.mail_no, m.mail_owner, m.mail_sender, m.mail_target, ");
		sql.append("      m.mail_title, m.mail_wtime, ");
		sql.append("      s.member_nickname as sender_nickname, ");
		sql.append("      t.member_nickname as target_nickname ");
		sql.append("    from mail m ");
		sql.append("    join member s on m.mail_sender = s.member_id ");
		sql.append("    join member t on m.mail_target = t.member_id ");
		sql.append("    where m.mail_target = ? and m.mail_sender <> m.mail_owner ");
		params.add(mailTarget);

		if (hasKeyword) {
			sql.append("      and instr(").append(columnExpr).append(", ?) > 0 ");
			params.add(keyword);
		}

		sql.append("    order by m.mail_no desc ");
		sql.append("  ) TMP");
		sql.append(") where rn between ? and ?");
		params.add(pageVO.getBegin());
		params.add(pageVO.getEnd());
		return jdbcTemplate.query(sql.toString(), mailVOMapper, params.toArray());
	}

	public MailDetailVO selectForDetail(int mailNo) {
		String sql = "select mail.*, s.member_nickname as sender_nickname from mail join member s on mail_sender = s.member_id where mail_no = ?";

		Object[] params = {mailNo};
		List<MailDetailVO> list =	jdbcTemplate.query(sql, mailDetailMapper, params);

		return list.isEmpty()? null:list.get(0);
	}

}
