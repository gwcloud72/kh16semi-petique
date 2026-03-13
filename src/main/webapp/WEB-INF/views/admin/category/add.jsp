<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [관리자] 카테고리 관리 등록 --%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-600 mt-50 mb-50 admin-page">
  <h2 class="center mb-20">새로운 게시판 등록</h2>

  <form action="add" method="post">
    <div class="cell">
      <label for="categoryName">카테고리 이름</label>
      <input type="text" id="categoryName" name="categoryName" class="field w-100p" required autocomplete="off" placeholder="카테고리 이름을 입력하세요" />
    </div>

    <div class="cell center mt-20">
      <button type="submit" class="btn btn-positive me-10">등록</button>
      <a href="list" class="btn btn-neutral">목록으로 돌아가기</a>
    </div>
  </form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
