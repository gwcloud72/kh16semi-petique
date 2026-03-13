<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [관리자] 동물 관리 상세 --%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>
<script type="text/javascript">
	$(function(){
		$(".text-summernote-editor").summernote('disable')
	})
</script>

<div class="container w-600 admin-page">
	<div class="cell animal-wrapper mt-20" data-animal-no="new">
		<div class="cell center">
			<img class="img-preview"
				src="${cp}/animal/profile?animalNo=${animalDto.animalNo }" width="100">
		</div>
		<div class="cell">
			<span>No. ${animalDto.animalNo }</span>
		</div>
		<div class="cell">
			<span>${animalDto.animalMaster }님의 반려동물</span>
		</div>
		<div class="cell">
			<label>
				<span>동물이름</span>
				<span>${animalDto.animalName }</span>
			</label>
		</div>
		<div class="cell">
			<label> <span>동물 소개</span>
			</label>
			<textarea class="w-100p text-summernote-editor">${animalDto.animalContent }</textarea>
		</div>
		<div class="cell flex-box">
			<div class="cell left">
				<button class="btn btn-neutral"type="button">
					<i class="fa-solid fa-home"></i> <span>분양${animalDto.animalPermission == 'f'? '불가':'가능'}</span>
				</button>
			</div>
			<div class="cell center">
				<a class="btn btn-neutral" href="edit?animalNo=${animalDto.animalNo }">수정하기</a>
			</div>
			<div class="cell right">
				<a class="btn btn-negative" href="delete?animalNo=${animalDto.animalNo }">삭제하기</a>
			</div>
		</div>
	</div>
</div>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
