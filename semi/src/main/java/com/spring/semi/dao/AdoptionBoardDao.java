package com.spring.semi.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.mapper.AdoptBoardMapper;
import com.spring.semi.mapper.AdoptDetailMapper;
import com.spring.semi.vo.AdoptDetailVO;
import com.spring.semi.vo.PageFilterVO;

/**
 * AdoptionBoardDao - 분양 게시판 전용 조회/연결 로직을 담당합니다.
 */
@Repository
public class AdoptionBoardDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AdoptBoardMapper adoptBoardMapper;

    @Autowired
    private AdoptDetailMapper adoptDetailMapper;

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String orderColumn(String orderBy) {
        if ("view".equalsIgnoreCase(orderBy)) return "b.board_view";
        if ("like".equalsIgnoreCase(orderBy)) return "b.board_like";
        return "b.board_wtime";
    }

    private String searchColumn(String column) {
        if ("board_writer".equalsIgnoreCase(column)) return "b.board_writer";
        if ("member_nickname".equalsIgnoreCase(column)) return "m.member_nickname";
        if ("animal_header_name".equalsIgnoreCase(column)) return "ah.header_name";
        if ("type_header_name".equalsIgnoreCase(column)) return "th.header_name";
        return "b.board_title";
    }

    private boolean excludeNotice(PageFilterVO filter) {
        if (filter == null) return true;
        if (!isBlank(filter.getTypeHeaderName())) return false;
        if (!isBlank(filter.getKeyword())) return false;
        return true;
    }

    public List<AdoptDetailVO> selectAdoptNoticeTop3(int categoryNo) {
        String sql = "select * from (" +
                "  select " +
                "    b.board_category_no, b.board_no, b.board_title, substr(b.board_content, 1, 400) as board_content, b.board_writer, " +
                "    b.board_wtime, b.board_etime, b.board_like, b.board_view, b.board_reply, b.deleted, b.board_score, " +
                "    th.header_name as type_header_name, ah.header_name as animal_header_name, " +
                "    m.member_nickname, ml.level_name, ml.badge_image, " +
                "    a.animal_no as animalNo, a.animal_name, a.animal_permission, substr(a.animal_content, 1, 400) as animal_content, " +
                "    case " +
                "      when a.animal_permission = 'f' then 'COMPLETED' " +
                "      when exists (select 1 from adoption_apply aa where aa.board_no = b.board_no and aa.apply_status in ('APPROVED','COMPLETED')) then 'APPROVED' " +
                "      else 'OPEN' " +
                "    end as adoption_stage " +
                "  from board b " +
                "  left join notice_pin np on b.board_no = np.board_no " +
                "  left join board_animal ba on b.board_no = ba.board_no " +
                "  left join animal a on ba.animal_no = a.animal_no " +
                "  left join member m on b.board_writer = m.member_id " +
                "  left join member_level_table ml on m.member_level = ml.level_no " +
                "  left join animal_header ah on b.board_animal_header = ah.header_no " +
                "  left join type_header th on b.board_type_header = th.header_no " +
                "  where b.board_category_no = ? " +
                "    and b.deleted = 0 " +
                "    and th.header_name = '공지' " +
                "    and (np.pin_start is null or np.pin_start <= systimestamp) " +
                "    and (np.pin_end is null or np.pin_end >= systimestamp) " +
                "  order by nvl(np.pin_order, 9999) asc, b.board_wtime desc, b.board_no desc " +
                ") where rownum <= 3";
        Object[] params = { categoryNo };
        return jdbcTemplate.query(sql, adoptBoardMapper, params);
    }

    public List<AdoptDetailVO> selectFilterListWithPaging(PageFilterVO filter, int categoryNo) {
        if (filter == null) return List.of();

        String orderColumn = orderColumn(filter.getOrderBy());
        String keyword = filter.getKeyword();
        String columnExpr = searchColumn(filter.getColumn());

        StringBuilder inner = new StringBuilder();
        List<Object> params = new ArrayList<>();

        inner.append("select ");
        inner.append("  b.board_category_no, b.board_no, b.board_title, substr(b.board_content, 1, 400) as board_content, b.board_writer, ");
        inner.append("  b.board_wtime, b.board_etime, b.board_like, b.board_view, b.board_reply, b.deleted, b.board_score, ");
        inner.append("  th.header_name as type_header_name, ah.header_name as animal_header_name, ");
        inner.append("  m.member_nickname, ml.level_name, ml.badge_image, ");
        inner.append("  a.animal_no as animalNo, a.animal_name, a.animal_permission, substr(a.animal_content, 1, 400) as animal_content, ");
        inner.append("  case ");
        inner.append("    when a.animal_permission = 'f' then 'COMPLETED' ");
        inner.append("    when exists (select 1 from adoption_apply aa where aa.board_no = b.board_no and aa.apply_status in ('APPROVED','COMPLETED')) then 'APPROVED' ");
        inner.append("    else 'OPEN' ");
        inner.append("  end as adoption_stage ");

        inner.append("from board b ");
        inner.append("left join board_animal ba on b.board_no = ba.board_no ");
        inner.append("left join animal a on ba.animal_no = a.animal_no ");
        inner.append("left join member m on b.board_writer = m.member_id ");
        inner.append("left join member_level_table ml on m.member_level = ml.level_no ");
        inner.append("left join animal_header ah on b.board_animal_header = ah.header_no ");
        inner.append("left join type_header th on b.board_type_header = th.header_no ");
        inner.append("where b.board_category_no = ? and b.deleted = 0 ");
        params.add(categoryNo);

        if (excludeNotice(filter)) {
            inner.append("and (th.header_name is null or th.header_name <> '공지') ");
        }

        if (!isBlank(filter.getAnimalHeaderName())) {
            inner.append("and ah.header_name = ? ");
            params.add(filter.getAnimalHeaderName());
        }

        if (!isBlank(filter.getTypeHeaderName())) {
            inner.append("and th.header_name = ? ");
            params.add(filter.getTypeHeaderName());
        }

        String stage = filter.getAdoptionStage();
        if (!isBlank(stage)) {
            stage = stage.trim().toUpperCase();
            if ("OPEN".equals(stage)) {
                inner.append("and a.animal_no is not null ");
                inner.append("and nvl(a.animal_permission, 't') <> 'f' ");
                inner.append("and not exists (select 1 from adoption_apply aa where aa.board_no = b.board_no and aa.apply_status in ('APPROVED','COMPLETED')) ");
            }
            else if ("APPROVED".equals(stage)) {
                inner.append("and a.animal_no is not null ");
                inner.append("and nvl(a.animal_permission, 't') <> 'f' ");
                inner.append("and exists (select 1 from adoption_apply aa where aa.board_no = b.board_no and aa.apply_status in ('APPROVED','COMPLETED')) ");
            }
            else if ("COMPLETED".equals(stage)) {
                inner.append("and a.animal_no is not null ");
                inner.append("and a.animal_permission = 'f' ");
            }
        }

        if (!isBlank(keyword)) {
            inner.append("and instr(").append(columnExpr).append(", ?) > 0 ");
            params.add(keyword);
        }

        inner.append("order by ").append(orderColumn).append(" desc, b.board_no desc");

        String sql = "select * from (" +
                "  select rownum rn, TMP.* from (" + inner + ") TMP" +
                ") where rn between ? and ?";

        params.add(filter.getBegin());
        params.add(filter.getEnd());

        return jdbcTemplate.query(sql, adoptBoardMapper, params.toArray());
    }

    public int countFilter(PageFilterVO filter, int categoryNo) {
        if (filter == null) return 0;

        String keyword = filter.getKeyword();
        String columnExpr = searchColumn(filter.getColumn());

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("select count(distinct b.board_no) ");
        sql.append("from board b ");
        sql.append("left join board_animal ba on b.board_no = ba.board_no ");
        sql.append("left join animal a on ba.animal_no = a.animal_no ");
        sql.append("left join member m on b.board_writer = m.member_id ");
        sql.append("left join member_level_table ml on m.member_level = ml.level_no ");
        sql.append("left join animal_header ah on b.board_animal_header = ah.header_no ");
        sql.append("left join type_header th on b.board_type_header = th.header_no ");
        sql.append("where b.board_category_no = ? and b.deleted = 0 ");
        params.add(categoryNo);

        if (excludeNotice(filter)) {
            sql.append("and (th.header_name is null or th.header_name <> '공지') ");
        }

        if (!isBlank(filter.getAnimalHeaderName())) {
            sql.append("and ah.header_name = ? ");
            params.add(filter.getAnimalHeaderName());
        }

        if (!isBlank(filter.getTypeHeaderName())) {
            sql.append("and th.header_name = ? ");
            params.add(filter.getTypeHeaderName());
        }

        String stage = filter.getAdoptionStage();
        if (!isBlank(stage)) {
            stage = stage.trim().toUpperCase();
            if ("OPEN".equals(stage)) {
                sql.append("and a.animal_no is not null ");
                sql.append("and nvl(a.animal_permission, 't') <> 'f' ");
                sql.append("and not exists (select 1 from adoption_apply aa where aa.board_no = b.board_no and aa.apply_status in ('APPROVED','COMPLETED')) ");
            }
            else if ("APPROVED".equals(stage)) {
                sql.append("and a.animal_no is not null ");
                sql.append("and nvl(a.animal_permission, 't') <> 'f' ");
                sql.append("and exists (select 1 from adoption_apply aa where aa.board_no = b.board_no and aa.apply_status in ('APPROVED','COMPLETED')) ");
            }
            else if ("COMPLETED".equals(stage)) {
                sql.append("and a.animal_no is not null ");
                sql.append("and a.animal_permission = 'f' ");
            }
        }

        if (!isBlank(keyword)) {
            sql.append("and instr(").append(columnExpr).append(", ?) > 0 ");
            params.add(keyword);
        }

        return jdbcTemplate.queryForObject(sql.toString(), int.class, params.toArray());
    }

    public AdoptDetailVO selectAdoptDetail(int boardNo) {
        String sql =
                "SELECT " +
                "  b.board_category_no, b.board_no, b.board_title, b.board_content, b.board_writer, b.board_wtime, b.board_etime, " +
                "  b.board_like, b.board_view, b.board_reply, b.board_animal_header, b.board_type_header, b.board_score, b.deleted, " +
                "  a.animal_name, a.animal_permission, a.animal_content, a.animal_master, ba.animal_no as animal_no, " +
                "  ah.header_name as animal_header_name, th.header_name as type_header_name, " +
                "  nvl(ap.media_no, -1) as media_no, " +
                "  m.member_nickname, ml.level_name, ml.badge_image " +
                "FROM board b " +
                "LEFT JOIN board_animal ba ON b.board_no = ba.board_no " +
                "LEFT JOIN animal a ON ba.animal_no = a.animal_no " +
                "LEFT JOIN animal_header ah ON b.board_animal_header = ah.header_no " +
                "LEFT JOIN type_header th ON b.board_type_header = th.header_no " +
                "LEFT JOIN animal_profile ap ON a.animal_no = ap.animal_no " +
                "LEFT JOIN member m ON b.board_writer = m.member_id " +
                "LEFT JOIN member_level_table ml ON m.member_level = ml.level_no " +
                "WHERE b.board_no = ? AND b.deleted = 0";

        List<AdoptDetailVO> list = jdbcTemplate.query(sql, adoptDetailMapper, boardNo);
        return list.isEmpty() ? null : list.get(0);
    }

    public void insertAnimalConnect(int boardNo, int animalNo) {
        String sql = "insert into board_animal (board_no, animal_no) values (?, ?)";
        Object[] params = { boardNo, animalNo };
        jdbcTemplate.update(sql, params);
    }

    public boolean updateBoardAnimal(int boardNo, int animalNo) {
        String sql = "update board_animal set animal_no = ? where board_no = ?";
        Object[] params = { animalNo, boardNo };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean deleteBoardAnimal(int boardNo) {
        String sql = "delete from board_animal where board_no = ?";
        Object[] params = { boardNo };
        return jdbcTemplate.update(sql, params) > 0;
    }

    public int updatePermissionToF(int boardNo) {
        String sql =
                "update animal a set a.animal_permission = 'f' " +
                "where a.animal_no = (select ba.animal_no from board_animal ba where ba.board_no = ?)";
        return jdbcTemplate.update(sql, boardNo);
    }
}
