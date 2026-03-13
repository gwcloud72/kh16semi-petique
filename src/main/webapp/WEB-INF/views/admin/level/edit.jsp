<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- [관리자] 레벨/뱃지 관리 수정 --%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-600 mt-50 mb-50 admin-page">
	<h2 class="center mb-20">회원 등급 수정</h2>

	<form action="${pageContext.request.contextPath}/admin/level/edit" method="post">
		<input type="hidden" name="levelNo" value="${level.levelNo}">

		<div class="cell">
			<label>등급 이름</label>
			<input type="text" name="levelName" value="${level.levelName}" class="field w-100p" required>
		</div>

		<div class="cell">
			<label>포인트 범위</label>
			<div class="flex-box gap-10">
				<input type="number" name="minPoint" value="${level.minPoint}" class="field w-50p" required>
				<input type="number" name="maxPoint" value="${level.maxPoint}" class="field w-50p" required>
			</div>
		</div>

		<div class="cell">
			<label>설명</label>
			<textarea name="description" rows="3" class="field w-100p">${level.description}</textarea>
		</div>

		<div class="cell">
			<label>뱃지 선택 (이모지)</label>
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

		<div class="cell center mt-20">
			<button type="submit" class="btn btn-positive me-10">수정 완료</button>
			<a href="${pageContext.request.contextPath}/admin/level/detail?levelNo=${level.levelNo}" class="btn btn-neutral">취소</a>
		</div>
	</form>
</div>

<script>
	function selectBadge(radio) {
		document.querySelectorAll('.badge-preview').forEach(span => span.classList.remove('selected'));
		radio.nextElementSibling.classList.add('selected');
	}

	window.onload = function() {
		const selectedBadge = "${level.badgeImage}";
		document.querySelectorAll('input[name="badgeImage"]').forEach(radio => {
			if (radio.value === selectedBadge) {
				radio.checked = true;
				selectBadge(radio);
			}
		});
	};
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
