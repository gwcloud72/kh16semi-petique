package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.AdoptionApplyDto;
import com.spring.semi.mapper.AdoptionApplyMapper;
import com.spring.semi.mapper.AdoptionApplyVOMapper;
import com.spring.semi.mapper.AdoptionApprovalAdminVOMapper;
import com.spring.semi.vo.AdoptionApplyVO;
import com.spring.semi.vo.AdoptionApprovalAdminVO;
import com.spring.semi.vo.PageVO;


/**
 * AdoptionApplyDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class AdoptionApplyDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AdoptionApplyMapper adoptionApplyMapper;

    @Autowired
    private AdoptionApplyVOMapper adoptionApplyVOMapper;

    @Autowired
    private AdoptionApprovalAdminVOMapper adoptionApprovalAdminVOMapper;

    public int sequence() {
        String sql = "select adoption_apply_seq.nextval from dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    public void insert(AdoptionApplyDto dto) {
        String sql = "insert into adoption_apply("
                + "apply_no, board_no, animal_no, applicant_id, apply_content, apply_status, apply_wtime"
                + ") values(?, ?, ?, ?, ?, 'APPLIED', systimestamp)";
        Object[] params = {
                dto.getApplyNo(),
                dto.getBoardNo(),
                dto.getAnimalNo(),
                dto.getApplicantId(),
                dto.getApplyContent()
        };
        jdbcTemplate.update(sql, params);
    }

    public AdoptionApplyDto selectOne(int applyNo) {
        String sql = "select * from adoption_apply where apply_no = ?";
        List<AdoptionApplyDto> list = jdbcTemplate.query(sql, adoptionApplyMapper, applyNo);
        return list.isEmpty() ? null : list.get(0);
    }

    public AdoptionApplyVO selectLatestByBoardAndApplicant(int boardNo, String applicantId) {
        String sql =
                "select * from ("
                        + "select aa.apply_no, aa.board_no, aa.animal_no, aa.applicant_id, aa.apply_content, aa.apply_status, aa.apply_wtime, aa.apply_etime, "
                        + "m.member_nickname, ml.level_name, ml.badge_image "
                        + "from adoption_apply aa "
                        + "left join member m on aa.applicant_id = m.member_id "
                        + "left join member_level_table ml on m.member_level = ml.level_no "
                        + "where aa.board_no = ? and aa.applicant_id = ? "
                        + "order by aa.apply_no desc"
                        + ") where rownum = 1";
        List<AdoptionApplyVO> list = jdbcTemplate.query(sql, adoptionApplyVOMapper, boardNo, applicantId);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<AdoptionApplyVO> selectLatestListByBoardNo(int boardNo) {
        String sql =
                "select aa.apply_no, aa.board_no, aa.animal_no, aa.applicant_id, aa.apply_content, aa.apply_status, aa.apply_wtime, aa.apply_etime, "
                        + "m.member_nickname, ml.level_name, ml.badge_image "
                        + "from adoption_apply aa "
                        + "join ("
                        + "select applicant_id, max(apply_no) as max_apply_no "
                        + "from adoption_apply "
                        + "where board_no = ? "
                        + "group by applicant_id"
                        + ") x on aa.applicant_id = x.applicant_id and aa.apply_no = x.max_apply_no "
                        + "left join member m on aa.applicant_id = m.member_id "
                        + "left join member_level_table ml on m.member_level = ml.level_no "
                        + "order by aa.apply_wtime desc";
        return jdbcTemplate.query(sql, adoptionApplyVOMapper, boardNo);
    }

    public AdoptionApplyVO selectApprovedByBoardNo(int boardNo) {
        String sql =
                "select * from ("
                        + "select aa.apply_no, aa.board_no, aa.animal_no, aa.applicant_id, aa.apply_content, aa.apply_status, aa.apply_wtime, aa.apply_etime, "
                        + "m.member_nickname, ml.level_name, ml.badge_image "
                        + "from adoption_apply aa "
                        + "left join member m on aa.applicant_id = m.member_id "
                        + "left join member_level_table ml on m.member_level = ml.level_no "
                        + "where aa.board_no = ? and aa.apply_status = 'APPROVED' "
                        + "order by aa.apply_no desc"
                        + ") where rownum = 1";
        List<AdoptionApplyVO> list = jdbcTemplate.query(sql, adoptionApplyVOMapper, boardNo);
        return list.isEmpty() ? null : list.get(0);
    }

    public AdoptionApplyVO selectCompletedByBoardNo(int boardNo) {
        String sql =
                "select * from ("
                        + "select aa.apply_no, aa.board_no, aa.animal_no, aa.applicant_id, aa.apply_content, aa.apply_status, aa.apply_wtime, aa.apply_etime, "
                        + "m.member_nickname, ml.level_name, ml.badge_image "
                        + "from adoption_apply aa "
                        + "left join member m on aa.applicant_id = m.member_id "
                        + "left join member_level_table ml on m.member_level = ml.level_no "
                        + "where aa.board_no = ? and aa.apply_status = 'COMPLETED' "
                        + "order by aa.apply_no desc"
                        + ") where rownum = 1";
        List<AdoptionApplyVO> list = jdbcTemplate.query(sql, adoptionApplyVOMapper, boardNo);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean existsApprovedOrCompleted(int boardNo) {
        String sql = "select count(*) from adoption_apply where board_no = ? and apply_status in ('APPROVED','COMPLETED')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, boardNo);
        return count != null && count > 0;
    }

    public boolean existsActiveByBoardAndApplicant(int boardNo, String applicantId) {
        String sql = "select count(*) from adoption_apply where board_no = ? and applicant_id = ? and apply_status in ('APPLIED','APPROVED','COMPLETED')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, boardNo, applicantId);
        return count != null && count > 0;
    }

    public boolean cancel(int applyNo, String applicantId) {
        String sql = "update adoption_apply "
                + "set apply_status = 'CANCELLED', apply_etime = systimestamp "
                + "where apply_no = ? and applicant_id = ? and apply_status = 'APPLIED'";
        return jdbcTemplate.update(sql, applyNo, applicantId) > 0;
    }

    public boolean reject(int applyNo) {
        String sql = "update adoption_apply "
                + "set apply_status = 'REJECTED', apply_etime = systimestamp "
                + "where apply_no = ? and apply_status = 'APPLIED'";
        return jdbcTemplate.update(sql, applyNo) > 0;
    }

    public boolean approve(int applyNo) {
        String sql = "update adoption_apply "
                + "set apply_status = 'APPROVED', apply_etime = systimestamp "
                + "where apply_no = ? and apply_status = 'APPLIED'";
        return jdbcTemplate.update(sql, applyNo) > 0;
    }

    public int rejectOthersApplied(int boardNo, int applyNo) {
        String sql = "update adoption_apply "
                + "set apply_status = 'REJECTED', apply_etime = systimestamp "
                + "where board_no = ? and apply_status = 'APPLIED' and apply_no <> ?";
        return jdbcTemplate.update(sql, boardNo, applyNo);
    }

    public boolean completeApproved(int boardNo) {
        String sql = "update adoption_apply "
                + "set apply_status = 'COMPLETED', apply_etime = systimestamp "
                + "where board_no = ? and apply_status = 'APPROVED'";
        return jdbcTemplate.update(sql, boardNo) > 0;
    }

    public int countAdminApproval(PageVO pageVO, String status) {
        String base = "from adoption_apply aa "
                + "join board b on aa.board_no = b.board_no "
                + "left join animal an on aa.animal_no = an.animal_no "
                + "left join member am on aa.applicant_id = am.member_id "
                + "left join member wm on b.board_writer = wm.member_id "
                + "where b.board_category_no = 4 ";

        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) ").append(base);

        java.util.List<Object> params = new java.util.ArrayList<>();

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" and aa.apply_status = ? ");
            params.add(status.trim().toUpperCase());
        }

        if (pageVO != null && pageVO.isSearch()) {
            String column = pageVO.getColumn();
            String keyword = pageVO.getKeyword();
            if (keyword != null) keyword = keyword.trim();
            String colExpr = null;

            if ("board_title".equals(column)) colExpr = "b.board_title";
            else if ("board_writer".equals(column)) colExpr = "b.board_writer";
            else if ("applicant_id".equals(column)) colExpr = "aa.applicant_id";
            else if ("applicant_nickname".equals(column)) colExpr = "am.member_nickname";
            else if ("animal_name".equals(column)) colExpr = "an.animal_name";
            else if ("apply_status".equals(column)) colExpr = "aa.apply_status";

            if (colExpr != null && keyword != null && !keyword.isBlank()) {
                sql.append(" and instr(").append(colExpr).append(", ?) > 0 ");
                params.add(keyword);
            }
        }

        return jdbcTemplate.queryForObject(sql.toString(), int.class, params.toArray());
    }

    public List<AdoptionApprovalAdminVO> selectAdminApprovalList(PageVO pageVO, String status) {
        String baseSelect = "select aa.apply_no, aa.board_no, aa.animal_no, aa.applicant_id, "
                + "nvl(am.member_nickname, aa.applicant_id) as applicant_nickname, "
                + "b.board_title, b.board_writer, nvl(wm.member_nickname, b.board_writer) as board_writer_nickname, "
                + "an.animal_name, aa.apply_status, aa.apply_wtime, aa.apply_etime "
                + "from adoption_apply aa "
                + "join board b on aa.board_no = b.board_no "
                + "left join animal an on aa.animal_no = an.animal_no "
                + "left join member am on aa.applicant_id = am.member_id "
                + "left join member wm on b.board_writer = wm.member_id "
                + "where b.board_category_no = 4 ";

        StringBuilder sql = new StringBuilder();
        sql.append("select * from (");
        sql.append("select rownum rn, TMP.* from (");
        sql.append(baseSelect);

        java.util.List<Object> params = new java.util.ArrayList<>();

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" and aa.apply_status = ? ");
            params.add(status.trim().toUpperCase());
        }

        if (pageVO != null && pageVO.isSearch()) {
            String column = pageVO.getColumn();
            String keyword = pageVO.getKeyword();
            if (keyword != null) keyword = keyword.trim();
            String colExpr = null;

            if ("board_title".equals(column)) colExpr = "b.board_title";
            else if ("board_writer".equals(column)) colExpr = "b.board_writer";
            else if ("applicant_id".equals(column)) colExpr = "aa.applicant_id";
            else if ("applicant_nickname".equals(column)) colExpr = "am.member_nickname";
            else if ("animal_name".equals(column)) colExpr = "an.animal_name";
            else if ("apply_status".equals(column)) colExpr = "aa.apply_status";

            if (colExpr != null && keyword != null && !keyword.isBlank()) {
                sql.append(" and instr(").append(colExpr).append(", ?) > 0 ");
                params.add(keyword);
            }
        }

        sql.append(" order by aa.apply_wtime desc, aa.apply_no desc ");
        sql.append(") TMP");
        sql.append(") where rn between ? and ?");

        params.add(pageVO.getBegin());
        params.add(pageVO.getEnd());

        return jdbcTemplate.query(sql.toString(), adoptionApprovalAdminVOMapper, params.toArray());
    }


    public int countLatestByApplicantId(String applicantId) {
        String sql = "select count(*) from ("
                + "select aa.board_no "
                + "from adoption_apply aa "
                + "join board b on aa.board_no = b.board_no "
                + "where b.board_category_no = 4 and aa.applicant_id = ? "
                + "group by aa.board_no"
                + ")";
        return jdbcTemplate.queryForObject(sql, int.class, applicantId);
    }

    public List<AdoptionApprovalAdminVO> selectLatestByApplicantIdWithPaging(PageVO pageVO, String applicantId) {
        String baseSelect = "select aa.apply_no, aa.board_no, aa.animal_no, aa.applicant_id, "
                + "nvl(am.member_nickname, aa.applicant_id) as applicant_nickname, "
                + "b.board_title, b.board_writer, nvl(wm.member_nickname, b.board_writer) as board_writer_nickname, "
                + "an.animal_name, aa.apply_status, aa.apply_wtime, aa.apply_etime "
                + "from adoption_apply aa "
                + "join ("
                + "  select board_no, max(apply_no) as max_apply_no "
                + "  from adoption_apply "
                + "  where applicant_id = ? "
                + "  group by board_no"
                + ") x on aa.board_no = x.board_no and aa.apply_no = x.max_apply_no "
                + "join board b on aa.board_no = b.board_no "
                + "left join animal an on aa.animal_no = an.animal_no "
                + "left join member am on aa.applicant_id = am.member_id "
                + "left join member wm on b.board_writer = wm.member_id "
                + "where b.board_category_no = 4 ";

        String sql = "select * from ("
                + "select rownum rn, TMP.* from ("
                + baseSelect
                + "order by aa.apply_wtime desc, aa.apply_no desc"
                + ") TMP"
                + ") where rn between ? and ?";

        Object[] params = { applicantId, pageVO.getBegin(), pageVO.getEnd() };
        return jdbcTemplate.query(sql, adoptionApprovalAdminVOMapper, params);
    }

}
