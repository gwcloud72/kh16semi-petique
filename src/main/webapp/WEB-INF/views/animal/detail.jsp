<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [동물] 동물 상세 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="동물 상세" scope="request"/>
<c:set var="pageCss" value="/css/animal.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-800">
	<div class="cell flex-box flex-wrap flex-middle gap-10">
		<div class="flex-fill">
			<h2 class="m-0">동물 상세</h2>
			<div class="mt-5 gray">등록된 동물 정보를 확인해 주세요</div>
		</div>
		<div class="flex-box flex-middle gap-10">
			<a class="btn btn-neutral" href="list">
				<i class="fa-solid fa-arrow-left"></i>
				<span>목록</span>
			</a>
			<a class="btn btn-menu" href="edit?animalNo=${animalDto.animalNo}">
				<span>수정</span>
			</a>
		</div>
	</div>

	<div class="cell mt-20 animal-detail-card">
		<div class="flex-box flex-wrap gap-20">
			<div>
				<img class="animal-detail-image" src="profile?animalNo=${animalDto.animalNo}">
			</div>
			<div class="flex-fill">
				<div class="flex-box flex-wrap flex-middle gap-10">
					<h3 class="m-0"><c:out value="${animalDto.animalName}"/></h3>
					<c:choose>
						<c:when test="${animalDto.animalPermission == 't'}">
							<span class="badge badge-positive">분양 가능</span>
						</c:when>
						<c:otherwise>
							<span class="badge badge-neutral">분양 불가</span>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="mt-10">
					<c:choose>
						<c:when test="${empty animalDto.animalContent}">
							<span class="gray">(소개 없음)</span>
						</c:when>
						<c:otherwise>
							<pre class="m-0" style="white-space:pre-wrap"><c:out value="${animalDto.animalContent}"/></pre>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>

	<form class="cell mt-15 center" action="delete" method="post" onsubmit="return confirm('삭제하시겠습니까?');">
		<input type="hidden" name="animalNo" value="${animalDto.animalNo}">
		<button class="btn btn-negative" type="submit">
			<i class="fa-solid fa-trash"></i>
			<span>삭제</span>
		</button>
	</form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
