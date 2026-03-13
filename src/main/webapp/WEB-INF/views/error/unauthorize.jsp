<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%-- [에러] unauthorize 화면 --%>

<c:set var="cp" value="${pageContext.request.contextPath}" scope="request"/>


<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>401 - 로그인이 필요합니다</title>
	<link rel="stylesheet" href="${cp}/css/error.css">
</head>
<body>
	<div class="error-page">
		<div class="error-card">
			<img class="error-image" src="${cp}/image/error/401.png" alt="401">
			<h1 class="error-title"><c:out value="${empty title ? '로그인이 필요합니다' : title}"/></h1>
			<p class="error-desc">이 기능을 사용하려면 로그인해 주세요.</p>
			<div class="error-actions">
				<a href="${cp}/member/login" class="btn-home">로그인</a>
				<a href="<c:url value='/' />" class="btn-back">홈으로</a>
			</div>
		</div>
	</div>
</body>
</html>
