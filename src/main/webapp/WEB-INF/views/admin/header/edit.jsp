<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- [관리자] 헤더 관리 수정 --%>

<c:set var="pageTitle" value="헤더 수정" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-600 admin-page">
	<div class="admin-header">
		<h1>헤더 수정</h1>
		<div class="admin-actions">
			<a href="list" class="btn btn-neutral">목록</a>
		</div>
	</div>

	<form action="edit" method="post">
		<input type="hidden" name="headerNo" value="${headerDto.headerNo}">
		<div class="cell">
			<label for="headerName">헤더 이름</label>
			<input type="text" id="headerName" name="headerName" value="${headerDto.headerName}" class="field w-100p" required autocomplete="off">
		</div>
		<div class="admin-actions">
			<button type="submit" class="btn btn-positive">수정 완료</button>
			<a href="list" class="btn btn-neutral">취소</a>
		</div>
	</form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
