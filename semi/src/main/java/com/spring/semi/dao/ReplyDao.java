package com.spring.semi.dao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.spring.semi.dto.ReplyDto;
import com.spring.semi.mapper.ReplyLikeMapper;
import com.spring.semi.mapper.ReplyMapper;
import com.spring.semi.vo.ReplyListVO;


/**
 * ReplyDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class ReplyDao {
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private ReplyMapper replyMapper;
   @Autowired
   private ReplyLikeMapper replyLikeMapper;

   public List<ReplyDto> selectList(String replyWriter) {
       String sql = "SELECT * FROM reply WHERE reply_writer = ? ORDER BY reply_no DESC";
       Object[] params = { replyWriter };
       return jdbcTemplate.query(sql, replyMapper, params);
   }

   public List<ReplyDto> selectList(int replyTarget) {
       String sql = "SELECT * FROM reply WHERE reply_target = ? ORDER BY reply_no DESC";
       Object[] params = { replyTarget };
       return jdbcTemplate.query(sql, replyMapper, params);
   }

   public boolean delete(int replyNo, int boardNo) {
       String sql = "DELETE FROM reply WHERE reply_no = ?";
       Object[] params = { replyNo };
       int result = jdbcTemplate.update(sql, params);
       if (result > 0) {

           String updateSql = "UPDATE board SET board_reply = board_reply - 1 WHERE board_no = ?";
           jdbcTemplate.update(updateSql, boardNo);
       }
       return result > 0;
   }

   public int sequence() {
       String sql = "SELECT reply_seq.NEXTVAL FROM dual";
       return jdbcTemplate.queryForObject(sql, Integer.class);
   }

   public void insert(ReplyDto replyDto) {
       String sql = "INSERT INTO reply (reply_no, reply_writer, reply_target, reply_content, reply_category_no) "
                  + "VALUES (?, ?, ?, ?, ?)";
       Object[] params = {
           replyDto.getReplyNo(),
           replyDto.getReplyWriter(),
           replyDto.getReplyTarget(),
           replyDto.getReplyContent(),
           replyDto.getReplyCategoryNo()
       };
       int result = jdbcTemplate.update(sql, params);

       if (result > 0) {
           String updateSql = "UPDATE board SET board_reply = board_reply + 1 WHERE board_no = ?";
           jdbcTemplate.update(updateSql, replyDto.getReplyTarget());
       }
   }

   public boolean update(ReplyDto replyDto) {
       String sql = "UPDATE reply "
                  + "SET reply_content = ?, reply_etime = SYSTIMESTAMP "
                  + "WHERE reply_no = ?";
       Object[] params = {
           replyDto.getReplyContent(),
           replyDto.getReplyNo()
       };
       return jdbcTemplate.update(sql, params) > 0;
   }

   public ReplyDto selectOne(int replyNo) {
       String sql = "SELECT * FROM reply WHERE reply_no = ?";
       Object[] params = { replyNo };
       List<ReplyDto> list = jdbcTemplate.query(sql, replyMapper, params);
       return list.isEmpty() ? null : list.get(0);
   }


   public void updateReplyLikeCount(int replyNo) {
       String sql = "UPDATE reply "
                  + "SET reply_like = (SELECT COUNT(*) FROM reply_like WHERE reply_no = ?) "
                  + "WHERE reply_no = ?";
       jdbcTemplate.update(sql, replyNo, replyNo);
   }

   public void increaseReplyLike(int replyNo) {
       String sql = "UPDATE reply SET reply_like = reply_like + 1 WHERE reply_no = ?";
       jdbcTemplate.update(sql, replyNo);
   }

   public void decreaseReplyLike(int replyNo) {
       String sql = "UPDATE reply SET reply_like = CASE WHEN reply_like > 0 THEN reply_like - 1 ELSE 0 END WHERE reply_no = ?";
       jdbcTemplate.update(sql, replyNo);
   }

   public List<ReplyDto> selectListByTime(int replyTarget) {
       String sql = "SELECT * FROM reply WHERE reply_target = ? ORDER BY reply_no DESC";
       return jdbcTemplate.query(sql, replyMapper, replyTarget);
   }

   public List<ReplyDto> selectListByLike(int replyTarget) {
       String sql = "SELECT * FROM reply WHERE reply_target = ? ORDER BY reply_like DESC, reply_no DESC";
       return jdbcTemplate.query(sql, replyMapper, replyTarget);
   }

   public int countByBoardNo(int boardNo) {
       String sql = "SELECT COUNT(*) FROM reply WHERE reply_target = ?";
       Integer count = jdbcTemplate.queryForObject(sql, Integer.class, boardNo);
       return count != null ? count.intValue() : 0;
   }
public List<ReplyListVO> selectListWithLike(int replyTarget, String sort, String loginId) {

		String sql =
		    "SELECT "
		    + "    R.*, "

		    + "    CASE WHEN RL.REPLY_NO IS NOT NULL THEN 1 ELSE 0 END AS IS_LIKED "
		    + "FROM "
		    + "    REPLY R "
		    		    + "LEFT JOIN "
		    + "    REPLY_LIKE RL ON R.REPLY_NO = RL.REPLY_NO AND RL.MEMBER_ID = ? "
		    + "WHERE "
		    + "    R.REPLY_TARGET = ? ";


		if ("like".equalsIgnoreCase(sort)) {
			sql += "ORDER BY R.REPLY_LIKE DESC, R.REPLY_WTIME DESC";
		} else {

			sql += "ORDER BY R.REPLY_WTIME DESC";
		}


		Object[] params = {loginId, replyTarget};


		return jdbcTemplate.query(sql, replyLikeMapper, params);
	}
}
