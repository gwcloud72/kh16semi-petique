<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [동물위키] 작성 화면 --%>
	<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css,/css/board_edit.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>

<form autocomplete="off" action="write" method="post" enctype="multipart/form-data">
  <div class="container w-800">
      <div class="cell center">
          <h1>동물위키 작성</h1>
      </div>
      <div class="cell center">
          이 글은 동물위키 게시판에 등록됩니다.<br>
          <em>다른 사람에게 도움이 되는 유익한 글을 작성해주세요!</em>
      </div>
        <div class="cell flex-box">
	      <div class="flex-box flex-vertical w-25p">
	      <label>동물</label>
			  <select name="boardAnimalHeader" class="field w-100p mt-2">
			  	         <c:forEach var="animalHeader" items="${animalList}">
			  	             <option value="${animalHeader.headerNo}">${animalHeader.headerName}</option>
			  	         </c:forEach>
			  </select>
	      </div>
	      <div class="flex-box flex-vertical w-25p ms-10">
		  <label>type</label>
			  <select name="boardTypeHeader" class="field w-100p mt-2">
			  	         <c:forEach var="typeHeader" items="${typeList}">
			  	             <c:if test="${(typeHeader.headerName ne '공지' or sessionScope.loginLevel eq 0) and typeHeader.headerName ne '분양'}">
    <option value="${typeHeader.headerNo}">${typeHeader.headerName}</option>
</c:if>
</c:forEach>
			  </select>
	      </div>
      </div>
<jsp:include page="/WEB-INF/views/board/fragment/notice-pin.jsp"></jsp:include>
      <div class="cell mt-20">
          <input type="text" name="boardTitle" class="field w-100p" placeholder="제목을 입력하세요.">
      </div>

      <div class="cell mt-20">
          <textarea name="boardContent" class="summernote-editor"></textarea>
      </div>

      <div class = "cell">
      		<label>썸네일</label>
            <input type = "file"
            name = "media" accept = ".png,.jpg" class = "field w-100p" required>
       </div>

      <div class="cell right mt-20">
          <button class="btn btn-positive">등록하기</button>
      </div>
  </div>
</form>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
