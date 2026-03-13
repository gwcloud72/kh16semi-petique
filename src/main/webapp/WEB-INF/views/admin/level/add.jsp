<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- [관리자] 레벨/뱃지 관리 등록 --%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-600 mt-50 mb-50 admin-page">
	<h2 class="center mb-20">회원 등급 등록</h2>

	<form action="${pageContext.request.contextPath}/admin/level/add" method="post">
		<div class="cell">
			<label for="levelNo">등급 번호</label>
			<input type="number" id="levelNo" name="levelNo" class="field w-100p" required>
		</div>

		<div class="cell">
			<label for="levelName">등급 이름</label>
			<input type="text" id="levelName" name="levelName" class="field w-100p" required>
		</div>

		<div class="cell">
			<label>설정할 포인트 범위</label>
			<div class="flex-box gap-10">
				<input type="number" name="minPoint" class="field w-50p" placeholder="최소 포인트" required>
				<input type="number" name="maxPoint" class="field w-50p" placeholder="최대 포인트" required>
			</div>
		</div>

		<div class="cell">
			<label for="description">설명</label>
			<textarea id="description" name="description" rows="3" class="field w-100p" placeholder="이 등급에 대한 설명을 입력하세요."></textarea>
		</div>

		<div class="cell">
			<label>뱃지 선택</label>
			<div class="flex-box gap-10 flex-wrap">
				<label>
					<input type="radio" name="badgeImage" value="🐹" required onchange="selectBadge(this)">
					<span class="badge-preview">🐹</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🐰" required onchange="selectBadge(this)">
					<span class="badge-preview">🐰</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🐻" required onchange="selectBadge(this)">
					<span class="badge-preview">🐻</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🐱" required onchange="selectBadge(this)">
					<span class="badge-preview">🐱</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🦊" required onchange="selectBadge(this)">
					<span class="badge-preview">🦊</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🐶" required onchange="selectBadge(this)">
					<span class="badge-preview">🐶</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🐼" required onchange="selectBadge(this)">
					<span class="badge-preview">🐼</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🦄" required onchange="selectBadge(this)">
					<span class="badge-preview">🦄</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🦁" required onchange="selectBadge(this)">
					<span class="badge-preview">🦁</span>
				</label>
				<label>
					<input type="radio" name="badgeImage" value="🐯" required onchange="selectBadge(this)">
					<span class="badge-preview">🐯</span>
				</label>
			</div>
		</div>

		<div class="cell center">
			<button type="submit" class="btn btn-positive me-10">등록</button>
			<a href="${pageContext.request.contextPath}/admin/level/list" class="btn btn-neutral">목록으로</a>
		</div>
	</form>
</div>

<script>
	function selectBadge(radio) {
		document.querySelectorAll('.badge-preview').forEach(span => span.classList.remove('selected'));
		radio.nextElementSibling.classList.add('selected');
	}
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
