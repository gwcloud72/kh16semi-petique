<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [관리자] 분양 관리 화면 --%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-1200 admin-page">
	<div class="cell right">
		<a class="btn btn-neutral" href="${cp}/admin/home">목록으로</a>
	</div>

	<div class="cell center">
		<h2>분양 승인 내역</h2>
	</div>

	<div class="cell">
		<form class="search-bar" action="approval" method="get">
			<select name="status">
				<option value="ALL" ${status == 'ALL' ? 'selected' : ''}>전체</option>
				<option value="APPLIED" ${status == 'APPLIED' ? 'selected' : ''}>신청</option>
				<option value="APPROVED" ${status == 'APPROVED' ? 'selected' : ''}>승인</option>
				<option value="REJECTED" ${status == 'REJECTED' ? 'selected' : ''}>거절</option>
				<option value="COMPLETED" ${status == 'COMPLETED' ? 'selected' : ''}>완료</option>
				<option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>취소</option>
			</select>

			<select name="column">
				<option value="board_title" ${pageVO.column == 'board_title' ? 'selected' : ''}>제목</option>
				<option value="board_writer" ${pageVO.column == 'board_writer' ? 'selected' : ''}>작성자</option>
				<option value="applicant_id" ${pageVO.column == 'applicant_id' ? 'selected' : ''}>신청자ID</option>
				<option value="applicant_nickname" ${pageVO.column == 'applicant_nickname' ? 'selected' : ''}>신청자닉네임</option>
				<option value="animal_name" ${pageVO.column == 'animal_name' ? 'selected' : ''}>동물명</option>
				<option value="apply_status" ${pageVO.column == 'apply_status' ? 'selected' : ''}>상태</option>
			</select>

			<input type="text" name="keyword" value="${pageVO.keyword}" placeholder="검색어를 입력하세요">
			<button type="submit" class="btn btn-neutral">검색</button>
		</form>
	</div>

	<table class="table table-border table-striped table-hover w-100p">
		<thead>
			<tr>
				<th>번호</th>
				<th>게시글</th>
				<th>제목</th>
				<th>동물</th>
				<th>신청자</th>
				<th>상태</th>
				<th>신청일</th>
				<th>처리일</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="row" items="${list}">
				<tr>
					<td>${row.applyNo}</td>
					<td>
						<a class="link" href="${cp}/board/adoption/detail?boardNo=${row.boardNo}">
							${row.boardNo}
						</a>
					</td>
					<td>
						<a class="link" href="${cp}/board/adoption/detail?boardNo=${row.boardNo}">
							${row.boardTitle}
						</a>
					</td>
					<td>
						${row.animalNo}
						<c:if test="${not empty row.animalName}">
							<span>(${row.animalName})</span>
						</c:if>
					</td>
					<td>
						${row.applicantNickname}
						<span>(${row.applicantId})</span>
					</td>
					<td>${row.applyStatus}</td>
					<td><fmt:formatDate value="${row.applyWtime}" pattern="yyyy-MM-dd HH:mm"/></td>
					<td><fmt:formatDate value="${row.applyEtime}" pattern="yyyy-MM-dd HH:mm"/></td>
				</tr>
			</c:forEach>
			<c:if test="${empty list}">
				<tr>
					<td colspan="8" class="center">표시할 내역이 없습니다</td>
				</tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="8" class="center">검색결과 : ${pageVO.begin} - ${pageVO.end} / ${pageVO.dataCount}개</td>
			</tr>
			<tr>
				<td colspan="8" class="center">
					<jsp:include page="/WEB-INF/views/template/pagination.jsp"></jsp:include>
				</td>
			</tr>
		</tfoot>
	</table>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
