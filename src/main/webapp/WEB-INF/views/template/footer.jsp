<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>


<%-- [템플릿] 공통 푸터 --%>

			</main>
			<jsp:include page="/WEB-INF/views/template/right-sidebar.jsp"></jsp:include>
		</div>
	</div>

	<footer class="kh-footer">
		<div class="kh-footer__inner">
			<div>
				<strong>KH PETIQUE</strong>
				<span class="ms-10">3D Semi Project</span>
			</div>
			<div class="kh-footer__links">
				<a href="${cp}/">HOME</a>
				<a href="${cp}/board/community/list">COMMUNITY</a>
				<a href="${cp}/board/info/list">INFO</a>
				<a href="${cp}/board/adoption/list">SERVICE</a>
			</div>
		</div>
	</footer>
</body>
</html>
