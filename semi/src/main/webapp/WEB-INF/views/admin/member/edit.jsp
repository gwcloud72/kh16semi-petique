<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [관리자] 회원 관리 수정 --%>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>
<script src="${cp}/js/nickname-check.js"></script>

<form action="edit" method="post" enctype="multipart/form-data" autocomplete="off" class="check-form">
	<div class="container w-600 admin-page">
		<input type="hidden" name="memberId" value="${memberDto.memberId }">
		<div class="cell">
			<input class="field w-100p" type="text" name="memberNickname" value="${memberDto.memberNickname}">
		</div>
		<div class="cell">
			<textarea class="text-summernote-editor" name="memberDescription">${memberDto.memberDescription }</textarea>
		</div>
		<div class="cell">
			<input class="field w-100p" type="number" name="memberPoint" value="${memberDto.memberPoint }">
		</div>
		<div class="cell center">
			<button type="submit" class="btn btn-positive w-50p">수정하기</button>
		</div>
	</div>
</form>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
