<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>


<%-- [게시판 공통] 화면 --%>

<div class="action-buttons">
  <a href="list" class="btn btn-neutral">목록으로</a>
  <c:if test="${sessionScope.loginId == adoptDetailVO.boardWriter || sessionScope.loginLevel == 0}">
    <a href="edit?boardNo=${adoptDetailVO.boardNo}" class="btn btn-edit">수정하기</a>
    <form method="post" action="delete" onsubmit="return confirm('정말 삭제하시겠습니까?');" class="d-inline">
      <input type="hidden" name="boardNo" value="${adoptDetailVO.boardNo}">
      <button type="submit" class="btn btn-delete">삭제하기</button>
    </form>
  </c:if>
</div>
