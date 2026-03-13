<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>


<%-- [게시판 공통] 화면 --%>

<div class="cell right">
  <a href="list" class="btn btn-neutral">목록으로</a>
  <c:if test="${boardDto.boardWriter == sessionScope.loginId || sessionScope.loginLevel == 0 }">
    <a href="edit?boardNo=${boardDto.boardNo}" class="btn btn-edit">수정하기</a>
    <form method="post" action="delete" onsubmit="return confirm('정말 삭제하시겠습니까?');" class="d-inline">
      <input type="hidden" name="boardNo" value="${boardDto.boardNo}">
      <button type="submit" class="btn btn-delete">삭제하기</button>
    </form>
  </c:if>
</div>
