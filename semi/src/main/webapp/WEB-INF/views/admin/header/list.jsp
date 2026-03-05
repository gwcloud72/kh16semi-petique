<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- [관리자] 헤더 관리 목록 --%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-800 admin-page">
   <h1>헤더 목록</h1>
   <c:choose>
       <c:when test="${empty headerList}">
           <div class="no-posts">등록된 헤더가 없습니다.</div>
       </c:when>
       <c:otherwise>
           <table class="table table-border table-striped table-hover w-100p">
               <thead>
                   <tr>
                       <th>번호</th>
                       <th>헤더 이름</th>
                       <th>관리</th>
                   </tr>
               </thead>
               <tbody>
                   <c:forEach var="header1" items="${headerList}">
                       <tr>
                           <td>${header1.headerNo}</td>
                           <td>${header1.headerName}</td>
                           <td>
                               <a class="btn btn-positive"
                                  href="edit?headerNo=${header1.headerNo}">
                                   수정
                               </a>
                               <form action="delete" method="post" class="d-inline">
                                   <input type="hidden" name="headerNo" value="${header1.headerNo}">
                                   <button type="submit" class="btn btn-negative">삭제</button>
                               </form>
                           </td>
                       </tr>
                   </c:forEach>
               </tbody>
           </table>
       </c:otherwise>
   </c:choose>
   <div class="cell right">
       <a href="add" class="btn btn-positive">＋ 새 헤더 추가</a>
   </div>
</div>
<jsp:include page="/WEB-INF/views/template/footer.jsp" />
