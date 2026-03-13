<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [분양] 작성 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="pageCss" value="/css/board_edit.css,/css/adoption.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<form autocomplete="off" action="write" method="post" enctype="multipart/form-data">
 <div class="container w-800">
     <div class="cell center">
         <h1>분양 정보 공유하기</h1>
     </div>
     <div class="cell center mb-30">
         이 글은 분양게시판에 등록됩니다.<br>
         <em>다른 사람에게 도움이 되는 유익한 글을 작성해주세요!</em>
     </div>

     <div class="cell">
         <input type="text" name="boardTitle" class="field w-100p" placeholder="제목을 입력하세요." required>

		  <select name="boardAnimalHeader" class="field w-100p mt-2">
		  	         <option value="">-- 동물 분류 선택 --</option>
		  	         <c:forEach var="animalHeader" items="${animalList}">
		  	             <option value="${animalHeader.headerNo}">${animalHeader.headerName}</option>
		  	         </c:forEach>
		  </select>
		  <select name="boardTypeHeader" class="field w-100p mt-2">
		  	         <option value="">-- 게시글 타입 선택 --</option>
		  	         <c:forEach var="typeHeader" items="${typeList}">
		  	             <c:if test="${typeHeader.headerName ne '공지' or sessionScope.loginLevel eq 0}">
    <option value="${typeHeader.headerNo}">${typeHeader.headerName}</option>
</c:if>
</c:forEach>
		  </select>
<jsp:include page="/WEB-INF/views/board/fragment/notice-pin.jsp"></jsp:include>
	  </div>

     <div class="cell mt-4">
         <select name="animalNo" class="field w-100p" required>
             <option value="">-- 분양할 동물 선택 (분양 가능만 표시) --</option>
             <c:forEach var="animal" items="${adoptableAnimalList}">
                 <option value="${animal.animalNo}">${animal.animalName} (No.${animal.animalNo})</option>
             </c:forEach>
         </select>

         <div class="cell right mt-2">
           <a href="${cp}/animal/list" class="btn btn-neutral">
               <i class="fa-solid fa-paw"></i> 동물 등록하기
           </a>
         </div>
     </div>

     <div class="cell mt-4">
         <label for="board_content" class="form-label">게시글 본문 (분양 조건 및 자세한 소개)</label>
         <textarea id="board_content" name="boardContent" class="field w-100p" rows="10"
                   placeholder="분양 조건 및 분양을 원하는 분들에게 전달할 내용을 상세하게 작성해주세요. (필수)"
                   required></textarea>
     </div>

     <div class="cell right mt-4">
         <button class="btn btn-positive">등록</button>
     </div>

 </div>
</form>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
