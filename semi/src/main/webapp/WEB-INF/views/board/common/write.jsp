<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [게시판 공통] 작성 화면 --%>
	<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css,/css/board_edit.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script
	src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>

<form autocomplete="off" action="write" method="post">
	<div class="container w-800">
		<div class="cell center">
			<h1>글 작성</h1>
		</div>
		<div class="cell center">
			<em>작은 정보라도 공유하면 큰 가치를 만듭니다.</em>
		</div>
		<div class="cell">

			<div class="cell mt-20">
				<input type="text" name="boardTitle" class="field w-100p"
					placeholder="제목을 입력하세요.">
			</div>
			<div class="cell flex-box">
		      <div class="flex-box flex-vertical w-25p ms-10">
				  <label>type</label>
					  <select name="boardTypeHeader" class="field w-100p mt-2">
					  	         <c:forEach var="headerDto" items="${headerList}">
					  	             <c:if test="${(headerDto.headerName ne '공지' or sessionScope.loginLevel eq 0) and headerDto.headerName ne '분양'}">
    <option value="${headerDto.headerNo}">${headerDto.headerName}</option>
</c:if>
</c:forEach>
					  </select>
			      </div>
		      </div>
<jsp:include page="/WEB-INF/views/board/fragment/notice-pin.jsp"></jsp:include>
			<div class="cell mt-20">
				<textarea name="boardContent" id="content" class="summernote-editor"></textarea>
			</div>
			<div class="cell right mt-20">
				<button class="btn btn-positive">등록하기</button>
			</div>
		</div>
		</div>

</form>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
