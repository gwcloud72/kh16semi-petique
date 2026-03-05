<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [회원] 회원 탈퇴 화면 --%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<form action="drop" method="post" autocomplete="off">
	<div class="cell">
		<label>
			<span>비밀번호 입력</span>
			<i class="fa-solid fa-asterisk red"></i>
		</label>
		<input class="field w-100p" name="member_pw">
	</div>
	<button type="submit" class="btn btn-negative">
		<i class="fa-solid fa-trash-can"></i>
		<span>탈퇴하기</span>
	</button>
</form>


<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
