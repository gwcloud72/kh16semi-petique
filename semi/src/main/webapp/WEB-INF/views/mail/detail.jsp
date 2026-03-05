<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [메일] detail 템플릿 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="pageCss" value="/css/board_send.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-800">
	<h1>${mailDto.mailTitle}</h1>

	<div class="meta">
		<table>

			<tr>
				<th>[작성자] : ${mailDto.senderNickname}</th>
				<td></td>
			</tr>
		</table>
	</div>

	<div class="content">
		<c:out value="${mailDto.mailContent}" escapeXml="false" />
	</div>

	<div class="cell right">
		<a href="list/receive" class="btn btn-neutral">목록으로</a>

		<form method="post" action="delete"
      onsubmit="return confirm('정말 삭제하시겠습니까?');"
      class="d-inline">

			<input type="hidden" name="mailNo" value="${mailDto.mailNo}">
			<button type="submit" class="btn btn-delete">삭제하기</button>
		</form>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
