<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%-- [에러] notFound 화면 --%>

<c:set var="cp" value="${pageContext.request.contextPath}" scope="request"/>


<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>404 - 페이지를 찾을 수 없습니다</title>
	<link rel="stylesheet" href="${cp}/css/error.css">
</head>
<body>
	<div class="error-page">
		<div class="error-card">
			<img class="error-image" src="${cp}/image/error/404.png" alt="404">
			<h1 class="error-title"><c:out value="${empty title ? '페이지를 찾을 수 없습니다' : title}"/></h1>
			<p class="error-desc">요청하신 페이지가 존재하지 않거나 삭제되었을 수 있습니다.</p>
			<div class="error-actions">
				<a href="<c:url value='/' />" class="btn-home">홈으로</a>
				<a href="javascript:history.back();" class="btn-back">이전</a>
			</div>
		</div>
	</div>
</body>
</html>
