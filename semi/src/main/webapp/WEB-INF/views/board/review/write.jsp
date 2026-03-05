<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [후기] 작성 화면 --%>
	<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css,/css/board_edit.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>
<script src="https://cdn.jsdelivr.net/gh/hiphop5782/score@latest/score.js"></script>
<script type = "text/javascript">
    $(function(){
        $(".star-editor").score({
            starColor:"#f1c40f",

            editable:true,
            integerOnly:true,
            zeroAvailable:false,
            send:{
                sendable:true,
                name:"reviewScore",
            },
        });
    });
</script>

<form autocomplete="off" action="write" method="post" enctype="multipart/form-data">
	<c:if test="${not empty adoptionBoardNo}">
		<input type="hidden" name="adoptionBoardNo" value="${adoptionBoardNo}">
	</c:if>
	<div class="container w-800">
		<div class="cell center">
			<c:choose>
				<c:when test="${not empty adoptionBoardNo}"><h1>분양 후기 작성</h1></c:when>
				<c:otherwise><h1>사용후기 작성</h1></c:otherwise>
			</c:choose>
		</div>
		<div class="cell center">
			<c:choose>
				<c:when test="${not empty adoptionBoardNo}">
					분양이 완료된 뒤 작성하는 후기입니다.<br>
					<em>실제 진행 과정에서 도움이 되었던 내용을 남겨주세요.</em>
				</c:when>
				<c:otherwise>
					이 글은 사용후기 게시판에 등록됩니다.<br>
					<em>다른 사람에게 도움이 되는 유익한 글을 작성해주세요!</em>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="cell flex-box">
	      <div class="flex-box flex-vertical w-25p">
	      <label>동물</label>
		  <select name="boardAnimalHeader" class="field w-100p mt-2">
			         <c:forEach var="animalHeader" items="${animalList}">
			             <option value="${animalHeader.headerNo}" ${prefillAnimalHeader == animalHeader.headerNo ? 'selected' : ''}>${animalHeader.headerName}</option>
			         </c:forEach>
		  </select>
	      </div>
	      <div class="flex-box flex-vertical w-25p ms-10">
		  <label>type</label>
		  <select name="boardTypeHeader" class="field w-100p mt-2">
			         <c:forEach var="typeHeader" items="${typeList}">
			             <c:if test="${(typeHeader.headerName ne '공지' or sessionScope.loginLevel eq 0) and typeHeader.headerName ne '분양'}">
						<option value="${typeHeader.headerNo}" ${prefillTypeHeader == typeHeader.headerNo ? 'selected' : ''}>${typeHeader.headerName}</option>
					</c:if>
				</c:forEach>
		  </select>
	      </div>
      </div>
<jsp:include page="/WEB-INF/views/board/fragment/notice-pin.jsp"></jsp:include>

		<div class = "cell">
            <div name="boardScore" class="star-editor" data-max="5" data-rate="1"></div>
        </div>

      <div class="cell mt-20">
          <input type="text" name="boardTitle" class="field w-100p" placeholder="제목을 입력하세요." value="${prefillTitle}">
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
