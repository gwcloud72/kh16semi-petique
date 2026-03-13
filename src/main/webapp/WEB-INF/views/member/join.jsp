<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- [회원] 회원가입 화면 --%>

<c:set var="pageTitle" value="회원가입" scope="request"/>
<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css,/css/member-auth.css" scope="request"/>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>
<script src="${cp}/js/member-check.js"></script>

<div class="auth-page container w-750">
	<div class="auth-card">
		<div class="auth-head">
			<h1 class="auth-title">회원가입</h1>
			<div class="auth-subtitle">기본 정보를 입력하고 PETIQUE를 시작하세요.</div>
		</div>

		<form class="auth-form check-form" action="join" method="post" enctype="multipart/form-data" autocomplete="off">
			<div class="join-grid">
				<div>
					<div class="auth-section">
						<div class="auth-section__title">계정 정보</div>

						<div class="cell">
							<label>
								<span>아이디</span>
								<i class="fa-solid fa-asterisk orange"></i>
							</label>
							<input class="field w-100p" type="text" name="memberId" required>
							<div class="fail-feedback">* 아이디: 영문자로 시작해야하며, 2~20자의 영문 대/소문자, 숫자만 사용 가능합니다.</div>
							<div class="fail2-feedback">* 아이디: 사용할 수 없는 아이디입니다. 다른 아이디를 입력해 주세요.</div>
						</div>

						<div class="cell">
							<label>
								<span>비밀번호</span>
								<i class="fa-solid fa-asterisk orange"></i>
								<i class="fa-solid fa-eye-slash"></i>
								<i class="fa-solid fa-eye d-none"></i>
							</label>
							<input class="field w-100p" type="password" name="memberPw" required>
							<div class="fail-feedback">* 비밀번호: 8~20자의 영문 대/소문자, 숫자, 특수문자(!@#$)를 사용해 주세요.</div>
						</div>

						<div class="cell">
							<label>
								<span>닉네임</span>
								<i class="fa-solid fa-asterisk orange"></i>
							</label>
							<input class="field w-100p" type="text" name="memberNickname" required>
							<div class="fail-feedback">* 닉네임: 3~16자의 영문 대/소문자, 숫자, 한글을 사용해 주세요.</div>
							<div class="fail-feedback">&emsp;&emsp;&emsp;&ensp; 닉네임의 맨앞/뒤에는 띄어쓰기가 불가능합니다.</div>
							<div class="fail2-feedback">* 닉네임: 이미 사용중인 닉네임입니다.</div>
						</div>
					</div>

					<div class="auth-section">
						<div class="auth-section__title">이메일 (선택)</div>
						<div class="cell">
							<label>
								<span>이메일</span>
							</label>
							<div class="auth-inline">
								<input class="field flex-fill w-100p" type="text" inputmode="email" name="memberEmail" placeholder="example@petique.com">
								<button type="button" class="btn-cert-send btn btn-positive">
									<i class="fa-solid fa-paper-plane"></i>
									<span>인증메일 전송</span>
								</button>
							</div>
							<div class="success-feedback w-100p"></div>
							<div class="fail-feedback w-100p">올바른 이메일 형식이 아닙니다</div>
							<div class="fail2-feedback w-100p">이미 등록된 이메일입니다</div>
							<input type="hidden" name="memberAuth" value="f">
							<div class="auth-hint">이메일을 등록하면 아이디/비밀번호 찾기에 사용할 수 있습니다.</div>
						</div>

						<div class="cell cert-input-area d-none">
							<label>
								<span>인증번호</span>
							</label>
							<div class="auth-inline">
								<input type="text" inputmode="numeric" class="field cert-input flex-fill w-100p" placeholder="인증번호 5자리 입력">
								<button type="button" class="btn-cert-check btn btn-positive">
									<i class="fa-solid fa-envelope"></i>
									<span>확인</span>
								</button>
								<button class="auth-btn btn bggreen d-none" type="button">
									<i class="fa-solid fa-check"></i>
								</button>
							</div>
							<div class="fail-feedback w-100p">인증번호가 올바르지 않거나 유효시간이 초과되었습니다</div>
						</div>
					</div>
				</div>

				<div>
					<div class="auth-section">
						<div class="auth-section__title">프로필</div>

						<div class="cell">
							<label>
								<span>프로필 이미지 (선택)</span>
							</label>
							<div class="join-avatar">
								<img class="img-preview" src="${cp}/image/error/no-image.png" alt="프로필 미리보기">
								<div class="join-avatar__actions">
									<input type="file" name="media" class="field w-100p" accept="image/*">
									<div class="auth-hint">미등록 시 기본 이미지가 사용됩니다.</div>
								</div>
							</div>
						</div>

						<div class="cell">
							<label>
								<span>소개글 (선택)</span>
							</label>
							<textarea class="text-summernote-editor w-100p" name="memberDescription"></textarea>
							<div class="auth-hint">프로필 소개는 마이페이지에서 언제든 수정할 수 있습니다.</div>
						</div>
					</div>
				</div>
			</div>

			<div class="auth-actions">
				<button type="submit" class="btn btn-positive w-100p">가입하기</button>
			</div>

			<div class="auth-help">
				<a href="${cp}/member/login">이미 계정이 있나요? 로그인</a>
			</div>
		</form>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
