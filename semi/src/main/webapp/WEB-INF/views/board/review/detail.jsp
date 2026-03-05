<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%-- [후기] 상세 화면 --%>

<c:set var="post" value="${boardDto}" scope="request"/>
<c:set var="showAnimalHeader" value="${false}" scope="request"/>
<c:set var="showTypeHeader" value="${true}" scope="request"/>
<c:set var="pageCss" value="/css/board_detail.css,/css/reply.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-800 board-detail" data-board-no="${boardDto.boardNo}" data-reply-category-no="${boardDto.boardCategoryNo}" data-login-id="${sessionScope.loginId}">
	<h1>[${boardDto.animalHeaderName}] ${boardDto.boardTitle}</h1>

	<jsp:include page="/WEB-INF/views/board/fragment/detail-meta.jsp" />

	<div class="content">
		<img src="${cp}/board/review/image?boardNo=${boardDto.boardNo}" alt="게시글 이미지" onerror="this.onerror=null; this.remove();">
		<c:out value="${boardDto.boardContent}" escapeXml="false" />
	</div>

	<div class="board-like-area">
		<i id="board-like" class="fa-regular fa-thumbs-up board-like-icon${empty sessionScope.loginId ? ' is-disabled' : ''}"></i>
		<span id="board-like-count" class="board-like-count">0</span>
	</div>

	<jsp:include page="/WEB-INF/views/board/fragment/detail-actions.jsp" />
	<jsp:include page="/WEB-INF/views/board/fragment/reply-section.jsp" />

	<script src="${cp}/js/board-detail.js"></script>
</div>
<jsp:include page="/WEB-INF/views/template/footer.jsp" />
