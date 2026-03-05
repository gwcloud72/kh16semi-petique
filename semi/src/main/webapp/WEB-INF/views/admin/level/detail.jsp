<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- [관리자] 레벨/뱃지 관리 상세 --%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-600 mt-50 mb-50 admin-page">
    <h2 class="center mb-20">회원 등급 상세 정보</h2>

    <div class="cell">
        <label>등급 번호</label>
        <div class="field">${level.levelNo}</div>
    </div>

    <div class="cell">
        <label>등급 이름</label>
        <div class="field">${level.levelName}</div>
    </div>

    <div class="cell">
        <label>포인트 범위</label>
        <div class="flex-box gap-10">
            <div class="field w-50p">최소: ${level.minPoint}</div>
            <div class="field w-50p">최대: ${level.maxPoint}</div>
        </div>
    </div>

    <div class="cell">
        <label>설명</label>
        <div class="field">${level.description}</div>
    </div>

    <div class="cell">
        <label>뱃지</label>
        <div class="field">${level.badgeImage}</div>
    </div>
     <div class="cell">
        <label>회원 수</label>
        <div class="field">${level.memberCount}</div>
    </div>


    <div class="cell center mt-20">
        <a href="${pageContext.request.contextPath}/admin/level/edit?levelNo=${level.levelNo}" class="btn btn-positive me-10">수정</a>

        <form action="${pageContext.request.contextPath}/admin/level/delete" method="post" class="d-inline">
            <input type="hidden" name="levelNo" value="${level.levelNo}">
            <button type="submit" onclick="return confirm('정말 삭제하시겠습니까?');" class="btn btn-delete">삭제</button>
        </form>

        <a href="${pageContext.request.contextPath}/admin/level/list" class="btn btn-neutral ms-10">목록으로</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
