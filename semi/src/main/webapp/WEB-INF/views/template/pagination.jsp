<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [템플릿] pagination --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>


<c:if test="${pageVO != null && pageVO.dataCount > 0 && pageVO.totalPage > 1}">
	<c:set var="baseUri" value="${requestScope['jakarta.servlet.forward.request_uri']}"/>
	<c:if test="${empty baseUri}">
		<c:set var="baseUri" value="${requestScope['javax.servlet.forward.request_uri']}"/>
	</c:if>
	<c:if test="${empty baseUri}">
		<c:set var="baseUri" value="${pageContext.request.requestURI}"/>
	</c:if>
	<c:set var="qs" value=""/>
	<c:forEach var="entry" items="${paramValues}">
		<c:if test="${entry.key ne 'page'}">
			<c:forEach var="val" items="${entry.value}">
				<c:set var="qs" value="${qs}&${entry.key}=${val}"/>
			</c:forEach>
		</c:if>
	</c:forEach>

	<div class="pagination">
		<c:if test="${pageVO.firstBlock == false}">
			<a href="${baseUri}?page=${pageVO.prevPage}${qs}">&lt;</a>
		</c:if>
		<c:forEach var="i" begin="${pageVO.blockStart}" end="${pageVO.blockFinish}" step="1">
			<c:choose>
				<c:when test="${pageVO.page == i}">
					<a class="on">${i}</a>
				</c:when>
				<c:otherwise>
					<a href="${baseUri}?page=${i}${qs}">${i}</a>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<c:if test="${pageVO.lastBlock == false}">
			<a href="${baseUri}?page=${pageVO.nextPage}${qs}">&gt;</a>
		</c:if>
	</div>
</c:if>
