<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [메일] list 템플릿 --%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="pageCss" value="/css/board_list.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-800">
	<div class="cell center">
		<h2>우편함</h2>
	</div>
	<div class="cell right">
		<a class="btn btn-positive" href="${cp}/mail/send">우편 보내기</a>
	</div>
	<div class="cell flex-box">
		<a class="btn btn-neutral w-50p" href="send">보낸 우편함</a> <a
			class="btn btn-neutral w-50p" href="receive">받은 우편함</a>
	</div>

	<div class="cell">
		<c:choose>
			<c:when test="${empty mailList}">
				<div class="no-posts">
					<c:choose>
						<c:when test="${type == 'send'}">보낸 우편이 없습니다.</c:when>
						<c:otherwise>받은 우편이 없습니다.</c:otherwise>
					</c:choose>
				</div>
			</c:when>

			<c:otherwise>
				<div class="cell">
					<table>
						<thead>
							<tr>
								<th>제목</th>
								<c:choose>
									<c:when test="${type == 'send'}">
										<th>받은이</th>
										<th>보낸 일자</th>
									</c:when>
									<c:otherwise>
										<th>보낸이</th>
										<th>받은 일자</th>
									</c:otherwise>
								</c:choose>
							</tr>
						</thead>

						<tbody>
							<c:forEach var="mailDto" items="${mailList}">
								<tr>
									<td class="center"><a
										href="${cp}/mail/detail?mailNo=${mailDto.mailNo}">
											${mailDto.mailTitle} </a></td>

									<c:choose>
										<c:when test="${type == 'send'}">
											<td>${mailDto.targetNickname}</td>
										</c:when>
										<c:otherwise>
											<td>${mailDto.senderNickname}</td>
										</c:otherwise>
									</c:choose>

									<td>${mailDto.mailWtime}</td>
								</tr>
							</c:forEach>
						</tbody>

						<tfoot>
							<tr>
								<td colspan="7">검색결과 : ${pageVO.begin} - ${pageVO.end} /
									${pageVO.dataCount}개</td>
							</tr>
							<tr>
								<td colspan="7" class="center"><jsp:include
										page="/WEB-INF/views/template/pagination.jsp" /></td>
							</tr>
						</tfoot>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
