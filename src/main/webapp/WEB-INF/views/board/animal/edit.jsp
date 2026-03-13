<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [동물위키] 수정 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageCss" value="/css/board_edit.css,https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>

<form action="edit" method="post" enctype="multipart/form-data">
  <input type="hidden" name="boardNo" value="${boardDto.boardNo}">

  <div class="container w-800">
    <div class="cell">
      <h1>[${boardDto.boardNo}번] 게시글 수정</h1>
      <input type="hidden" name="boardNo" value="${boardDto.boardNo}">
    </div>
	  <select name="boardAnimalHeader" required>
	          <option value="">-- 선택 --</option>
	          <c:forEach var="animalHeader" items="${animalList}">
	              <option value="${animalHeader.headerNo}" ${animalHeader.headerNo == boardDto.boardAnimalHeader ? 'selected' : ''}>
	                  ${animalHeader.headerName}
	              </option>
	          </c:forEach>
	  </select>
	  <select name="boardTypeHeader" required>
	          <option value="">-- 선택 --</option>
	          <c:forEach var="typeHeader" items="${typeList}">
	              <c:if test="${(typeHeader.headerName ne '공지' or sessionScope.loginLevel eq 0) and typeHeader.headerName ne '분양'}">
	                  <option value="${typeHeader.headerNo}" ${typeHeader.headerNo == boardDto.boardTypeHeader ? 'selected' : ''}>
	                      ${typeHeader.headerName}
	                  </option>
	              </c:if>
	          </c:forEach>
	  </select>
<jsp:include page="/WEB-INF/views/board/fragment/notice-pin.jsp"></jsp:include>

    <div class="cell">
      <label for="title">제목 <i class="fa-solid fa-asterisk red"></i></label>
      <input type="text" name="boardTitle" id="title" value="${boardDto.boardTitle}" class="field w-100">
    </div>

    <div class="cell">
      <label for="content">내용 <i class="fa-solid fa-asterisk red"></i></label>
      <textarea name="boardContent" id="content" class="summernote-editor">${boardDto.boardContent}</textarea>
    </div>

    <div class = "cell">
   		<label>썸네일</label>
        <input type = "file"
        name = "media" accept = ".png,.jpg" class = "field w-100" required>
    </div>

    <div class="cell mt-40">
      <button type="submit" class="btn btn-positive w-100">
        <i class="fa-solid fa-edit"></i>
        <span>수정하기</span>
      </button>
    </div>
  </div>
</form>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
