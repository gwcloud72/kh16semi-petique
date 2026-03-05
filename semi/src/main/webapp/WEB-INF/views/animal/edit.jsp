<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [동물] 동물 수정 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="동물 수정" scope="request"/>
<c:set var="pageCss" value="/css/animal.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-800">
	<div class="cell flex-box flex-wrap flex-middle gap-10">
		<div class="flex-fill">
			<h2 class="m-0">동물 수정</h2>
			<div class="mt-5 gray">동물 정보를 수정할 수 있습니다</div>
		</div>
		<div>
			<a class="btn btn-neutral" href="detail?animalNo=${animalDto.animalNo}">
				<i class="fa-solid fa-arrow-left"></i>
				<span>돌아가기</span>
			</a>
		</div>
	</div>

	<form class="cell mt-20" action="edit" method="post" enctype="multipart/form-data" autocomplete="off">
		<input type="hidden" name="animalNo" value="${animalDto.animalNo}">
		<div class="cell">
			<label class="label">이름</label>
			<input class="field w-100p" type="text" name="animalName" value="${animalDto.animalName}" required>
		</div>
		<div class="cell mt-10">
			<label class="label">소개</label>
			<textarea class="field w-100p animal-textarea" name="animalContent"><c:out value="${animalDto.animalContent}"/></textarea>
		</div>
		<div class="cell mt-10">
			<label class="label">분양 가능</label>
			<div class="flex-box gap-10 flex-middle">
				<label class="flex-box gap-5 flex-middle">
					<input type="radio" name="animalPermission" value="t" ${animalDto.animalPermission == 't' ? 'checked' : ''}>
					<span>가능</span>
				</label>
				<label class="flex-box gap-5 flex-middle">
					<input type="radio" name="animalPermission" value="f" ${animalDto.animalPermission == 'f' ? 'checked' : ''}>
					<span>불가</span>
				</label>
			</div>
		</div>
		<div class="cell mt-10">
			<label class="label">프로필 이미지 변경</label>
			<div class="flex-box gap-15 flex-middle">
				<img class="thumb-60" src="profile?animalNo=${animalDto.animalNo}">
				<input class="field flex-fill" type="file" name="media" accept="image/*">
			</div>
		</div>
		<div class="cell mt-20 center">
			<button class="btn btn-positive" type="submit">
				<i class="fa-solid fa-check"></i>
				<span>저장</span>
			</button>
			<a class="btn btn-menu" href="detail?animalNo=${animalDto.animalNo}">
				<span>취소</span>
			</a>
		</div>
	</form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
