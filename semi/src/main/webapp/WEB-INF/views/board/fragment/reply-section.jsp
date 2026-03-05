<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>


<%-- [게시판 공통] 화면 --%>

<div class="reply-header-container">
  <h3 class="reply-section-title">💬 댓글 (<span id="reply-count">0</span>개)</h3>
  <div class="sort-buttons">
    <button class="btn-sort active" data-sort="time" type="button">최신순</button>
    <button class="btn-sort" data-sort="like" type="button">인기순</button>
  </div>
</div>
<div class="reply-list-wrapper"></div>
<c:if test="${sessionScope.loginId != null}">
  <div class="reply-write-wrapper">
    <textarea class="reply-input" rows="4" name="replyContent" placeholder="좋은 댓글을 남겨주세요"></textarea>
    <div class="cell right">
      <button id="emoji-btn" type="button" class="btn btn-emoji">
        <i class="fa-regular fa-face-smile"></i>
      </button>
      <button type="button" class="btn btn-positive reply-btn-write">댓글 작성</button>
      <div id="emoji-picker-container" class="emoji-picker-container"></div>
    </div>
  </div>
</c:if>
