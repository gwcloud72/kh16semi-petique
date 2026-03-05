package com.spring.semi.mapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import com.spring.semi.vo.ReplyListVO;


/**
 * ReplyLikeMapper - JDBC ResultSet을 VO/DTO로 매핑.
 */
@Component

public class ReplyLikeMapper implements RowMapper<ReplyListVO> {
	@Override

	public ReplyListVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		int isLikedValue = 0;
		try {

			isLikedValue = rs.getInt("IS_LIKED");
		} catch (SQLException e) {

		}
		boolean isLikedBoolean = isLikedValue == 1;

		return ReplyListVO.builder()

				.replyNo(rs.getInt("reply_no"))
				.replyContent(rs.getString("reply_content"))
				.replyWriter(rs.getString("reply_writer"))
				.replyTarget(rs.getInt("reply_target"))
				.replyWtime(rs.getTimestamp("reply_wtime"))
				.replyEtime(rs.getTimestamp("reply_etime"))
				.replyLike(rs.getInt("reply_like"))


				.writer(false)
				.owner(false)

				.isLiked(isLikedBoolean)
				.build();
	}
}
