<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [회원] 정보 수정 화면 --%>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>
<script src="${cp}/js/member-check.js"></script>
<script src="${cp}/js/add-animal.js"></script>

<form action="edit" method="post" enctype="multipart/form-data" autocomplete="off">
	<div class="container w-600">
		<div class="cell" >
			<label>
				<span>확인용 비밀번호</span>
				<i class="fa-solid fa-asterisk red"></i>
			</label>
			<input class="field w-100p" type="password" name="memberPw">
		</div>
		<div class="cell">
			<input class="field w-100p" type="text" name="memberNickname" value="${memberDto.memberNickname}">
		</div>
		<div class="cell">
			<div class="flex-box flex-wrap">
				<input class="field flex-fill" type="text" name="memberEmail" value="${memberDto.memberEmail }" readonly="readonly">
				<button class="btn btn-neutral auth-edit-btn d-none" type="button">
					<i class="fa-solid fa-pen"></i>
					<span>수정</span>
				</button>
				<button type="button" class="btn-cert-send btn btn-positive d-none">
					<i class="fa-solid fa-paper-plane"></i>
					<span>인증메일 전송</span>
				</button>
				<div class="fail-feedback w-100p">올바른 이메일 형식이 아닙니다</div>
				<div class="fail2-feedback w-100p">이미 등록된 이메일입니다</div>
			</div>
		</div>
		<div class="cell cert-input-area d-none">
			<div class="flex-box flex-wrap">
				<input type="text" inputmode="numeric"
					class="cert-input field flex-fill" placeholder="인증번호 입력"  size="5">
				<button type="button" class="btn-cert-check btn btn-positive w-75p">
					<i class="fa-solid fa-envelope"></i>
					<span>인증번호 확인</span>
				</button>
				<div class="success-feedback w-100p">이메일 인증이 완료되었습니다</div>
			  	<div class="fail2-feedback w-100p">인증번호가 올바르지 않거나 유효시간이 초과되었습니다</div>
			</div>
		</div>
		<div class="cell">
			<input type="hidden" name="memberAuth" value="${memberDto.memberAuth }">
			<button class="btn auth-btn ms-20 ${memberDto.memberAuth eq 't' ? 'bggreen' : 'bgred'}" type="button">
				<i class="fa-solid fa-check"></i>
			</button>
		</div>
		<div class="cell">
			<textarea class="text-summernote-editor" name="memberDescription">${memberDto.memberDescription }</textarea>
		</div>


		<div class="cell center">
			<button type="submit" class="btn btn-positive w-50p">수정하기</button>
		</div>
	</div>
</form>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
