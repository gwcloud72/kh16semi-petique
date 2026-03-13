<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [분양] 수정 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageCss" value="/css/board_edit.css,/css/adoption.css,/summernote/custom-summernote.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<c:if test="${sessionScope.loginId == adoptDetailVO.boardWriter
              and adoptionStage eq 'APPROVED'}">
  <form id="completeForm" action="completeAdoption" method="post"
        onsubmit="return confirm('정말로 이 동물의 분양을 완료 처리하시겠습니까?')"
        class="d-none">
    <input type="hidden" name="boardNo" value="${adoptDetailVO.boardNo}">
  </form>
</c:if>

<form id="editForm" action="edit" method="post">
 <input type="hidden" name="boardNo" value="${adoptDetailVO.boardNo}">
 <div class="container w-800">
   <h1>[${adoptDetailVO.boardNo}번] 게시글 수정</h1>

   <label for="animalHeader">동물 종류</label>
   <select class="field w-100p" name="boardAnimalHeader" id="animalHeader" required>
       <option value="">-- 동물 선택 --</option>
       <c:forEach var="animal" items="${animalList}">
           <option value="${animal.headerNo}" ${animal.headerNo != adoptDetailVO.boardAnimalHeader? '':'selected'}>
               ${animal.headerName}
           </option>
       </c:forEach>
   </select>

   <label for="typeHeader">게시글 분류</label>
       <select class="field w-100p" name="boardTypeHeader" id="typeHeader" required>
           <option value="">-- 분류 선택 --</option>

           <c:forEach var="type" items="${typeList}">
               <option value="${type.headerNo}" ${type.headerNo == adoptDetailVO.boardTypeHeader ? 'selected' : ''}>
                   ${type.headerName}
               </option>
           </c:forEach>
       </select>
<jsp:include page="/WEB-INF/views/board/fragment/notice-pin.jsp"></jsp:include>

   <label for="animalNo">분양할 동물 선택</label>
   <select class="field w-100p" name="animalNo" id="animalNo" required>
       <option value="">-- 분양할 동물 선택 (재선택 가능) --</option>
       <c:forEach var="animal" items="${adoptableAnimalList}">
           <option value="${animal.animalNo}" ${animal.animalNo == currentAnimalNo ? 'selected' : ''}>
               ${animal.animalName} (No.${animal.animalNo})
           </option>
       </c:forEach>
   </select>

   <div class="cell right mt-2" >
       <a href="${cp}/animal/list" class="btn btn-neutral">
           <i class="fa-solid fa-paw"></i>
           <span>동물 등록하러가기</span>
       </a>
   </div>

   <label for="title">제목 <i class="fa-solid fa-asterisk red"></i></label>
   <input type="text" name="boardTitle" id="title"
          value="${adoptDetailVO.boardTitle}" class="field w-100p">

   <label for="content">내용 <i class="fa-solid fa-asterisk red"></i></label>
   <textarea class="field w-100p" name="boardContent" id="content" rows="15">${adoptDetailVO.boardContent}</textarea>

   <div class="cell right mt-30">

     <c:if test="${sessionScope.loginId == adoptDetailVO.boardWriter
                   and adoptionStage eq 'APPROVED'}">
       <button type="submit" form="completeForm" class="btn btn-negative">
         <i class="fa-solid fa-check"></i> 분양 완료 처리
       </button>
     </c:if>

     <button type="submit" form="editForm" class="btn btn-positive">
       <i class="fa-solid fa-edit"></i> 수정하기
     </button>
   </div>
 </div>
</form>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
