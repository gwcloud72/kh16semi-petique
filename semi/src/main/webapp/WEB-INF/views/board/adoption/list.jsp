<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [분양] 목록 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<c:set var="pageCss" value="/css/board_list.css,/css/adoption.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-1200 board-page adoption-list-page">
	<div class="page-header-area">
		<h1>${category.categoryName}</h1>
		<div class="page-subtitle gray">새 가족을 기다리는 아이들을 찾아보세요</div>
	</div>

	<jsp:include page="/WEB-INF/views/board/fragment/notice-top.jsp"></jsp:include>

	<div class="adopt-stage-tabs">
		<c:url var="stageAllUrl" value="list">
			<c:if test="${not empty selectedAnimalHeaderName}"><c:param name="animalHeaderName" value="${selectedAnimalHeaderName}"/></c:if>
			<c:if test="${not empty selectedTypeHeaderName}"><c:param name="typeHeaderName" value="${selectedTypeHeaderName}"/></c:if>
			<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}"><c:param name="orderBy" value="${selectedOrderBy}"/></c:if>
			<c:if test="${not empty pageVO.keyword}">
				<c:param name="column" value="${pageVO.column}"/>
				<c:param name="keyword" value="${pageVO.keyword}"/>
			</c:if>
		</c:url>
		<c:url var="stageOpenUrl" value="list">
			<c:param name="adoptionStage" value="OPEN"/>
			<c:if test="${not empty selectedAnimalHeaderName}"><c:param name="animalHeaderName" value="${selectedAnimalHeaderName}"/></c:if>
			<c:if test="${not empty selectedTypeHeaderName}"><c:param name="typeHeaderName" value="${selectedTypeHeaderName}"/></c:if>
			<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}"><c:param name="orderBy" value="${selectedOrderBy}"/></c:if>
			<c:if test="${not empty pageVO.keyword}">
				<c:param name="column" value="${pageVO.column}"/>
				<c:param name="keyword" value="${pageVO.keyword}"/>
			</c:if>
		</c:url>
		<c:url var="stageApprovedUrl" value="list">
			<c:param name="adoptionStage" value="APPROVED"/>
			<c:if test="${not empty selectedAnimalHeaderName}"><c:param name="animalHeaderName" value="${selectedAnimalHeaderName}"/></c:if>
			<c:if test="${not empty selectedTypeHeaderName}"><c:param name="typeHeaderName" value="${selectedTypeHeaderName}"/></c:if>
			<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}"><c:param name="orderBy" value="${selectedOrderBy}"/></c:if>
			<c:if test="${not empty pageVO.keyword}">
				<c:param name="column" value="${pageVO.column}"/>
				<c:param name="keyword" value="${pageVO.keyword}"/>
			</c:if>
		</c:url>
		<c:url var="stageCompletedUrl" value="list">
			<c:param name="adoptionStage" value="COMPLETED"/>
			<c:if test="${not empty selectedAnimalHeaderName}"><c:param name="animalHeaderName" value="${selectedAnimalHeaderName}"/></c:if>
			<c:if test="${not empty selectedTypeHeaderName}"><c:param name="typeHeaderName" value="${selectedTypeHeaderName}"/></c:if>
			<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}"><c:param name="orderBy" value="${selectedOrderBy}"/></c:if>
			<c:if test="${not empty pageVO.keyword}">
				<c:param name="column" value="${pageVO.column}"/>
				<c:param name="keyword" value="${pageVO.keyword}"/>
			</c:if>
		</c:url>

		<a class="stage-tab ${empty pageVO.adoptionStage ? 'active' : ''}" href="${stageAllUrl}">전체</a>
		<a class="stage-tab ${pageVO.adoptionStage eq 'OPEN' ? 'active' : ''}" href="${stageOpenUrl}">분양 가능</a>
		<a class="stage-tab ${pageVO.adoptionStage eq 'APPROVED' ? 'active' : ''}" href="${stageApprovedUrl}">승인 완료</a>
		<a class="stage-tab ${pageVO.adoptionStage eq 'COMPLETED' ? 'active' : ''}" href="${stageCompletedUrl}">분양 완료</a>
	</div>

	<div class="adopt-filter-card">
		<form action="list" method="get" class="adopt-filter-form" id="adoptFilterForm" autocomplete="off">
			<c:if test="${not empty pageVO.adoptionStage}">
				<input type="hidden" name="adoptionStage" value="${pageVO.adoptionStage}">
			</c:if>
			<div class="adopt-filter-row">
				<div class="adopt-filter-group">
					<label class="adopt-filter-label">동물</label>
					<select name="animalHeaderName" class="field">
						<option value="">전체</option>
						<c:forEach var="animal" items="${animalList}">
							<option value="${animal.headerName}" <c:if test="${selectedAnimalHeaderName eq animal.headerName}">selected</c:if>>
								${animal.headerName}
							</option>
						</c:forEach>
					</select>
				</div>

				<div class="adopt-filter-group">
					<label class="adopt-filter-label">타입</label>
					<select name="typeHeaderName" class="field">
						<option value="">전체</option>
						<c:forEach var="type" items="${typeList}">
							<option value="${type.headerName}" <c:if test="${selectedTypeHeaderName eq type.headerName}">selected</c:if>>
								${type.headerName}
							</option>
						</c:forEach>
					</select>
				</div>

				<div class="adopt-filter-group">
					<label class="adopt-filter-label">정렬</label>
					<select name="orderBy" class="field">
						<option value="wtime" ${selectedOrderBy eq 'wtime' ? 'selected' : ''}>최신순</option>
						<option value="view" ${selectedOrderBy eq 'view' ? 'selected' : ''}>조회순</option>
						<option value="like" ${selectedOrderBy eq 'like' ? 'selected' : ''}>추천순</option>
					</select>
				</div>

				<div class="adopt-filter-group adopt-filter-search">
					<label class="adopt-filter-label">검색</label>
					<div class="adopt-filter-searchbox">
						<select name="column" class="field" title="검색 기준">
							<option value="board_title" ${pageVO.column eq 'board_title' ? 'selected' : ''}>제목</option>
							<option value="board_writer" ${pageVO.column eq 'board_writer' ? 'selected' : ''}>아이디</option>
							<option value="member_nickname" ${pageVO.column eq 'member_nickname' ? 'selected' : ''}>닉네임</option>
						</select>
						<input type="search" name="keyword" class="field flex-fill" value="${pageVO.keyword}" placeholder="검색어를 입력해 주세요">
						<button type="submit" class="btn btn-positive">
							<i class="fas fa-search"></i>
							<span>검색</span>
						</button>
						<a href="list" class="btn btn-menu">
							<i class="fa-solid fa-rotate-left"></i>
							<span>초기화</span>
						</a>
					</div>
				</div>
			</div>
		</form>

		<div class="adopt-filter-state">
			<c:choose>
				<c:when test="${pageVO.search or (not empty selectedOrderBy and selectedOrderBy ne 'wtime')}">
					<div class="adopt-chips">
						<c:if test="${not empty pageVO.adoptionStage}">
							<c:url var="removeStage" value="list">
								<c:if test="${not empty selectedAnimalHeaderName}"><c:param name="animalHeaderName" value="${selectedAnimalHeaderName}"/></c:if>
								<c:if test="${not empty selectedTypeHeaderName}"><c:param name="typeHeaderName" value="${selectedTypeHeaderName}"/></c:if>
								<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}"><c:param name="orderBy" value="${selectedOrderBy}"/></c:if>
								<c:if test="${not empty pageVO.keyword}">
									<c:param name="column" value="${pageVO.column}"/>
									<c:param name="keyword" value="${pageVO.keyword}"/>
								</c:if>
							</c:url>
							<a class="adopt-chip" href="${removeStage}" title="상태 필터 해제">
								상태:
								<c:choose>
									<c:when test="${pageVO.adoptionStage eq 'OPEN'}">분양 가능</c:when>
									<c:when test="${pageVO.adoptionStage eq 'APPROVED'}">승인 완료</c:when>
									<c:when test="${pageVO.adoptionStage eq 'COMPLETED'}">분양 완료</c:when>
									<c:otherwise><c:out value="${pageVO.adoptionStage}"/></c:otherwise>
								</c:choose>
								<span class="chip-x">×</span>
							</a>
						</c:if>

						<c:if test="${not empty selectedAnimalHeaderName}">
							<c:url var="removeAnimal" value="list">
								<c:if test="${not empty pageVO.adoptionStage}"><c:param name="adoptionStage" value="${pageVO.adoptionStage}"/></c:if>
								<c:if test="${not empty selectedTypeHeaderName}"><c:param name="typeHeaderName" value="${selectedTypeHeaderName}"/></c:if>
								<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}"><c:param name="orderBy" value="${selectedOrderBy}"/></c:if>
								<c:if test="${not empty pageVO.keyword}">
									<c:param name="column" value="${pageVO.column}"/>
									<c:param name="keyword" value="${pageVO.keyword}"/>
								</c:if>
							</c:url>
							<a class="adopt-chip" href="${removeAnimal}" title="동물 필터 해제">동물: ${selectedAnimalHeaderName} <span class="chip-x">×</span></a>
						</c:if>

						<c:if test="${not empty selectedTypeHeaderName}">
							<c:url var="removeType" value="list">
								<c:if test="${not empty pageVO.adoptionStage}"><c:param name="adoptionStage" value="${pageVO.adoptionStage}"/></c:if>
								<c:if test="${not empty selectedAnimalHeaderName}"><c:param name="animalHeaderName" value="${selectedAnimalHeaderName}"/></c:if>
								<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}"><c:param name="orderBy" value="${selectedOrderBy}"/></c:if>
								<c:if test="${not empty pageVO.keyword}">
									<c:param name="column" value="${pageVO.column}"/>
									<c:param name="keyword" value="${pageVO.keyword}"/>
								</c:if>
							</c:url>
							<a class="adopt-chip" href="${removeType}" title="타입 필터 해제">타입: ${selectedTypeHeaderName} <span class="chip-x">×</span></a>
						</c:if>

						<c:if test="${not empty pageVO.keyword}">
							<c:url var="removeKeyword" value="list">
								<c:if test="${not empty pageVO.adoptionStage}"><c:param name="adoptionStage" value="${pageVO.adoptionStage}"/></c:if>
								<c:if test="${not empty selectedAnimalHeaderName}"><c:param name="animalHeaderName" value="${selectedAnimalHeaderName}"/></c:if>
								<c:if test="${not empty selectedTypeHeaderName}"><c:param name="typeHeaderName" value="${selectedTypeHeaderName}"/></c:if>
								<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}"><c:param name="orderBy" value="${selectedOrderBy}"/></c:if>
							</c:url>
							<a class="adopt-chip" href="${removeKeyword}" title="검색 해제">검색: '${pageVO.keyword}' <span class="chip-x">×</span></a>
						</c:if>

						<c:if test="${not empty selectedOrderBy and selectedOrderBy ne 'wtime'}">
							<c:url var="removeOrder" value="list">
								<c:if test="${not empty pageVO.adoptionStage}"><c:param name="adoptionStage" value="${pageVO.adoptionStage}"/></c:if>
								<c:if test="${not empty selectedAnimalHeaderName}"><c:param name="animalHeaderName" value="${selectedAnimalHeaderName}"/></c:if>
								<c:if test="${not empty selectedTypeHeaderName}"><c:param name="typeHeaderName" value="${selectedTypeHeaderName}"/></c:if>
								<c:if test="${not empty pageVO.keyword}">
									<c:param name="column" value="${pageVO.column}"/>
									<c:param name="keyword" value="${pageVO.keyword}"/>
								</c:if>
							</c:url>
							<a class="adopt-chip" href="${removeOrder}" title="정렬 초기화">
								정렬:
								<c:choose>
									<c:when test="${selectedOrderBy eq 'view'}">조회순</c:when>
									<c:when test="${selectedOrderBy eq 'like'}">추천순</c:when>
									<c:otherwise>최신순</c:otherwise>
								</c:choose>
								<span class="chip-x">×</span>
							</a>
						</c:if>
					</div>
				</c:when>
				<c:otherwise>
					<div class="adopt-filter-hint">전체 목록을 보고 있어요</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<div class="cell flex-box flex-wrap flex-middle gap-10 adopt-list-topbar">
		<div class="flex-fill">
			<c:if test="${not empty pageVO.keyword}">
				<div class="keyword-result">'${pageVO.keyword}' 검색 결과</div>
			</c:if>
		</div>
		<div>
			<c:choose>
				<c:when test="${sessionScope.loginId != null}">
					<a href="write" class="btn btn-positive">
						<i class="fas fa-pencil-alt"></i>
						<span>글쓰기</span>
					</a>
				</c:when>
				<c:otherwise>
					<p class="login-required">
						<a href="${cp}/member/login">로그인</a>을 해야 글을 작성할 수 있습니다
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<c:choose>
		<c:when test="${empty boardList}">
			<div class="no-posts">
				<div class="empty-icons">
					<i class="fas fa-dog"></i>
					<i class="fas fa-cat"></i>
				</div>
				현재 등록된 귀여운 아가들이 없어요 😭
			</div>
		</c:when>
		<c:otherwise>
			<div class="adopt-card-grid">
				<c:forEach var="boardDto" items="${boardList}">
					<c:url var="detailUrl" value="detail">
						<c:param name="boardNo" value="${boardDto.boardNo}"/>
					</c:url>
					<c:url var="writerUrl" value="/member/detail">
						<c:param name="memberNickname" value="${boardDto.memberNickname}"/>
					</c:url>
					<article class="adopt-card js-adopt-card" data-href="${detailUrl}">
						<div class="adopt-card__thumb">
							<a class="adopt-card__thumbLink" href="${detailUrl}">
								<img class="adopt-card__img" src="${cp}/animal/profile?animalNo=${boardDto.animalNo}" onerror="this.onerror=null;this.src='${cp}/image/error/no-image.png';">
							</a>
							<div class="adopt-card__badges">
								<c:if test="${not empty boardDto.typeHeaderName and boardDto.typeHeaderName ne '전체'}">
									<span class="adopt-pill"><c:out value="${boardDto.typeHeaderName}"/></span>
								</c:if>
								<c:if test="${not empty boardDto.animalHeaderName and boardDto.animalHeaderName ne '전체'}">
									<span class="adopt-pill"><c:out value="${boardDto.animalHeaderName}"/></span>
								</c:if>
							</div>
							<div class="adopt-card__status">
								<c:choose>
									<c:when test="${boardDto.adoptionStage eq 'COMPLETED' or boardDto.animalPermission eq 'f'}">
										<span class="adopt-status completed"><i class="fa-solid fa-circle-xmark"></i> 분양 완료</span>
									</c:when>
									<c:when test="${boardDto.adoptionStage eq 'APPROVED'}">
										<span class="adopt-status approved"><i class="fa-solid fa-hourglass-half"></i> 승인 완료</span>
									</c:when>
									<c:otherwise>
										<span class="adopt-status open"><i class="fa-solid fa-circle-check"></i> 분양 가능</span>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="adopt-card__body">
							<h3 class="adopt-card__title">
								<a class="adopt-card__titleLink" href="${detailUrl}">
									<c:out value="${boardDto.boardTitle}"/>
								</a>
								<c:if test="${boardDto.boardReply > 0}">
									<span class="adopt-card__count">(${boardDto.boardReply})</span>
								</c:if>
							</h3>
							<c:if test="${not empty boardDto.animalName}">
								<div class="adopt-card__subtitle">
									<i class="fa-solid fa-paw"></i>
									<span class="adopt-card__animalName"><c:out value="${boardDto.animalName}"/></span>
								</div>
							</c:if>
							<c:if test="${not empty boardDto.boardSummary}">
								<div class="adopt-card__summary"><c:out value="${boardDto.boardSummary}"/></div>
							</c:if>
							<div class="adopt-card__meta">
								<a class="meta-item adopt-writer-link" href="${writerUrl}" title="작성자 프로필">
									<i class="fa-solid fa-user"></i>
									<c:out value="${boardDto.memberNickname}"/>
								</a>
								<span class="meta-sep">•</span>
								<span class="meta-item"><i class="fa-solid fa-eye"></i> ${boardDto.boardView}</span>
								<span class="meta-sep">•</span>
								<span class="meta-item"><i class="fa-regular fa-heart"></i> ${boardDto.boardLike}</span>
								<span class="meta-sep">•</span>
								<span class="meta-item"><i class="fa-solid fa-clock"></i> <fmt:formatDate value="${boardDto.boardWtime}" pattern="yy.MM.dd" /></span>
							</div>
						</div>
					</article>
				</c:forEach>
			</div>

			<c:set var="endNo" value="${pageVO.end}"/>
			<c:if test="${endNo > pageVO.dataCount}">
				<c:set var="endNo" value="${pageVO.dataCount}"/>
			</c:if>

			<div class="adopt-list-footer">
				<div class="adopt-list-info">
					<span class="adoption-tfoot-label"><i class="fas fa-list-alt"></i> 검색결과 :</span>
					${pageVO.begin} - ${endNo} / 총 ${pageVO.dataCount}개
				</div>
				<div class="center">
					<c:if test="${pageVO.totalPage > 1}">
						<jsp:include page="/WEB-INF/views/template/pagination.jsp" />
					</c:if>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</div>

<script src="${cp}/js/adoption-list.js"></script>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
