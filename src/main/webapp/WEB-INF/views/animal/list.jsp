<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [동물] 동물 목록 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="동물 관리" scope="request"/>
<c:set var="pageCss" value="/css/animal.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-900">
	<div class="cell flex-box flex-wrap flex-middle gap-10">
		<div class="flex-fill">
			<h2 class="m-0">동물 관리</h2>
			<div class="mt-5 gray">내 동물 등록/수정/삭제 및 분양 상태를 관리할 수 있습니다</div>
		</div>
		<div class="flex-box flex-middle gap-10">
			<a class="btn btn-positive" href="add">
				<i class="fa-solid fa-plus"></i>
				<span>등록</span>
			</a>
			<a class="btn btn-neutral" href="${cp}/member/mypage">
				<i class="fa-solid fa-arrow-left"></i>
				<span>마이페이지</span>
			</a>
		</div>
	</div>

	<div class="cell mt-20">
		<form action="list" method="get" autocomplete="off" class="flex-box flex-wrap gap-10 flex-middle">
			<select name="column" class="field">
				<option value="animal_name" ${pageVO.column == 'animal_name' ? 'selected' : ''}>이름</option>
				<option value="animal_content" ${pageVO.column == 'animal_content' ? 'selected' : ''}>소개</option>
			</select>
			<input type="search" name="keyword" class="field flex-fill" value="${pageVO.keyword}" placeholder="검색어를 입력해 주세요">
			<button type="submit" class="btn btn-neutral">
				<i class="fa-solid fa-magnifying-glass"></i>
				<span>검색</span>
			</button>
			<c:if test="${pageVO.search}">
				<a class="btn btn-menu" href="list">
					<i class="fa-solid fa-rotate-left"></i>
					<span>초기화</span>
				</a>
			</c:if>
		</form>
	</div>

	<div class="cell mt-10">
		<table class="table table-border table-hover">
			<thead>
				<tr>
					<th style="width:90px">사진</th>
					<th style="width:160px">이름</th>
					<th>소개</th>
					<th style="width:110px">분양</th>
					<th style="width:160px">관리</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${empty animalList}">
						<tr>
							<td colspan="5" class="center">등록된 동물이 없습니다</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach var="animalDto" items="${animalList}">
							<tr>
								<td class="center">
									<img class="thumb-60" src="profile?animalNo=${animalDto.animalNo}">
								</td>
								<td>
									<a class="link" href="detail?animalNo=${animalDto.animalNo}">
										<c:out value="${animalDto.animalName}"/>
									</a>
								</td>
								<td>
									<c:choose>
										<c:when test="${empty animalDto.animalContent}">
											<span class="gray">(소개 없음)</span>
										</c:when>
										<c:otherwise>
											<c:out value="${fn:length(animalDto.animalContent) > 80 ? fn:substring(animalDto.animalContent, 0, 80) : animalDto.animalContent}"/>
											<c:if test="${fn:length(animalDto.animalContent) > 80}">...</c:if>
										</c:otherwise>
									</c:choose>
								</td>
								<td class="center">
									<c:choose>
										<c:when test="${animalDto.animalPermission == 't'}">
											<span class="badge badge-positive">가능</span>
										</c:when>
										<c:otherwise>
											<span class="badge badge-neutral">불가</span>
										</c:otherwise>
									</c:choose>
								</td>
								<td class="center">
									<a class="btn btn-menu" href="detail?animalNo=${animalDto.animalNo}">
										<span>상세</span>
									</a>
									<a class="btn btn-neutral" href="edit?animalNo=${animalDto.animalNo}">
										<span>수정</span>
									</a>
									<form action="delete" method="post" class="d-inline" onsubmit="return confirm('삭제하시겠습니까?');">
										<input type="hidden" name="animalNo" value="${animalDto.animalNo}">
										<button type="submit" class="btn btn-negative"><span>삭제</span></button>
									</form>
								</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="5">
						검색결과 : ${pageVO.begin} - ${pageVO.end} / ${pageVO.dataCount}개
					</td>
				</tr>
				<tr>
					<td colspan="5" class="center">
						<jsp:include page="/WEB-INF/views/template/pagination.jsp"></jsp:include>
					</td>
				</tr>
			</tfoot>
		</table>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
