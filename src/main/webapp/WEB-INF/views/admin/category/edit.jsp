<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- [관리자] 카테고리 관리 수정 --%>

<c:set var="pageTitle" value="카테고리 수정" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-400 admin-page">
	<div class="admin-header">
		<h1>카테고리 수정</h1>
	</div>

	<form action="edit" method="post">
		<input type="hidden" name="categoryNo" value="${categoryDto.categoryNo}" />

		<div class="cell">
			<label for="categoryName">카테고리 이름</label>
			<input type="text" id="categoryName" name="categoryName" value="${categoryDto.categoryName}" class="field w-100p" required autocomplete="off" />
		</div>

		<div class="admin-actions">
			<button type="submit" class="btn btn-positive">수정 완료</button>
			<a href="list" class="btn btn-neutral">취소</a>
		</div>
	</form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
