package com.spring.semi.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * ReplyListResponseVO - 화면/쿼리 결과용 VO.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReplyListResponseVO {
    private int boardReply;
    private List<ReplyListVO> list;
}
