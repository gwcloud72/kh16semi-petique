<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- [관리자] 카테고리 관리 목록 --%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-800 admin-page">
  <h1>게시판 목록</h1>

  <div class="cell right mb-20">
    <a class="btn btn-neutral" href="${cp}/admin/home">목록으로</a>
  </div>

  <c:choose>
    <c:when test="${empty categoryList}">
      <div class="no-posts">등록된 카테고리가 없습니다.</div>
    </c:when>
    <c:otherwise>
      <table class="table table-border table-striped table-hover w-100p">
        <thead>
          <tr>
            <th>번호</th>
            <th>이름</th>
            <th>가능한 작업</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="categoryDto" items="${categoryList}">
            <tr>
              <td>${categoryDto.categoryNo}</td>
              <td><a class="link" href="./stats?categoryName=${categoryDto.categoryName}">${categoryDto.categoryName}</a></td>
              <td>
                <a class="btn btn-positive" href="${pageContext.request.contextPath}/board/${categoryDto.categoryName}/list">이동</a>
                <a class="btn btn-neutral" href="edit?categoryNo=${categoryDto.categoryNo}">수정</a>
                <form method="post" action="delete" class="d-inline" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                  <input type="hidden" name="categoryNo" value="${categoryDto.categoryNo}">
                  <button type="submit" class="btn btn-delete">삭제</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>

  <div class="center mt-20">
    <a href="add" class="btn btn-positive">새로운 카테고리 등록</a>
  </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
