<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- [분양] 상세 화면 --%>

<c:set var="pageCss" value="/css/board_detail.css,/css/reply.css,/css/adoption.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-800 adoption-detail board-detail" data-board-no="${adoptDetailVO.boardNo}" data-reply-category-no="${adoptDetailVO.boardCategoryNo}" data-login-id="${sessionScope.loginId}">
<div class="detail-wrapper">
<c:if test="${not empty adoptDetailVO.typeHeaderName}">
   <div class="board-title">[${adoptDetailVO.typeHeaderName}] ${adoptDetailVO.boardTitle}</div>
</c:if>

<div class="animal-profile-image-wrapper">
   <img class="profile-img"
        src="${cp}/animal/profile?animalNo=${adoptDetailVO.animalNo}"
        alt="${adoptDetailVO.animalName}의 프로필 사진"
        title="${adoptDetailVO.animalName}의 프로필 사진"
        onerror="this.onerror=null; this.src='${cp}/image/error/no-image.png'">
</div>

	<c:url var="writerProfileUrl" value="/member/detail">
		<c:param name="memberNickname" value="${adoptDetailVO.memberNickname}"/>
	</c:url>

<div class="board-meta">
   <table>
     <tr>
			<th>[작성자]</th>
					<td><a class="profile-link" href="${writerProfileUrl}"><c:out value="${adoptDetailVO.memberNickname}"/></a><c:if
							test="${not empty adoptDetailVO.badgeImage}">${adoptDetailVO.badgeImage}</c:if>
						<c:if test="${not empty adoptDetailVO.levelName}">
							<span class="level-badge">${adoptDetailVO.levelName}</span>
						</c:if>
					</td>
		  <th>작성일</th>
		  <td><fmt:formatDate value="${adoptDetailVO.boardWtime}" pattern="yyyy-MM-dd HH:mm" /></td>
		</tr>
		<tr>
		  <th>수정일</th>
		  <td><fmt:formatDate value="${adoptDetailVO.boardEtime}" pattern="yyyy-MM-dd HH:mm" /></td>
		  <th>동물 이름</th>
		  <td>${adoptDetailVO.animalName}</td>
		</tr>
		<tr>
		  <th>동물분류</th>
		  <td>${adoptDetailVO.animalHeaderName}</td>
		  <th>분양상태</th>
		  <td>
		    <c:choose>
		      <c:when test="${adoptionStage eq 'OPEN'}">
		        <span class="adopt-status open">분양가능</span>
		      </c:when>
		      <c:when test="${adoptionStage eq 'APPROVED'}">
		        <span class="adopt-status approved">🟡 분양 진행중</span>
		      </c:when>
		      <c:when test="${adoptionStage eq 'COMPLETED'}">
		        <span class="adopt-status completed">분양완료</span>
		      </c:when>
		      <c:otherwise>-</c:otherwise>
		    </c:choose>
		  </td>
		</tr>
		<tr>
		  <th>승인대상</th>
		  <td colspan="3">
		    <c:choose>
		      <c:when test="${adoptionStage eq 'APPROVED'}">
		        <c:choose>
		          <c:when test="${isOwner or (myApply != null and myApply.applyStatus eq 'APPROVED')}">
		            <c:url var="approvedProfileUrl" value="/member/detail">
		            	<c:param name="memberId" value="${approvedApply.applicantId}"/>
		            </c:url>
		            <a class="profile-link" href="${approvedProfileUrl}"><c:out value="${approvedApply.applicantNickname}"/></a> (${approvedApply.applicantId})
		          </c:when>
		          <c:otherwise>승인된 신청자가 있습니다</c:otherwise>
		        </c:choose>
		      </c:when>
		      <c:when test="${adoptionStage eq 'COMPLETED'}">
		        <c:choose>
		          <c:when test="${isOwner or (myApply != null and myApply.applyStatus eq 'COMPLETED')}">
		            <c:url var="completedProfileUrl" value="/member/detail">
		            	<c:param name="memberId" value="${completedApply.applicantId}"/>
		            </c:url>
		            <a class="profile-link" href="${completedProfileUrl}"><c:out value="${completedApply.applicantNickname}"/></a> (${completedApply.applicantId})
		          </c:when>
		          <c:otherwise>분양이 완료되었습니다</c:otherwise>
		        </c:choose>
		      </c:when>
		      <c:otherwise>-</c:otherwise>
		    </c:choose>
		  </td>
		</tr>
   </table>
 </div>
 <div class="animal-summary-box">
     <strong>🐾 동물 간단 소개 (핵심 정보)</strong>
     ${adoptDetailVO.animalContent}
 </div>
  <div class="board-content">
   ${adoptDetailVO.boardContent}
 </div>

 <c:if test="${not isOwner}">
   <div class="apply-box">
     <div class="apply-title">📩 분양 신청</div>
     <c:if test="${empty sessionScope.loginId}">
       <div class="apply-message">신청하려면 로그인이 필요합니다. <a href="${cp}/member/login">로그인</a></div>
     </c:if>
     <c:if test="${not empty sessionScope.loginId}">
       <c:if test="${param.apply eq 'ok'}"><div class="apply-message ok">신청이 완료되었습니다.</div></c:if>
       <c:if test="${param.apply eq 'fail'}"><div class="apply-message fail">신청할 수 없는 상태입니다.</div></c:if>
       <c:if test="${param.cancel eq 'ok'}"><div class="apply-message ok">신청이 취소되었습니다.</div></c:if>
       <c:if test="${param.cancel eq 'fail'}"><div class="apply-message fail">신청 취소에 실패했습니다.</div></c:if>
       <c:choose>
         <c:when test="${adoptionStage eq 'COMPLETED'}">
           <div class="apply-message">이미 분양이 완료된 동물입니다.</div>
         </c:when>
         <c:when test="${adoptionStage eq 'APPROVED'}">
           <c:choose>
             <c:when test="${myApply != null and myApply.applyStatus eq 'APPROVED'}">
               <div class="apply-message ok">신청이 승인되었습니다. 작성자가 완료 처리하면 분양이 종료됩니다.</div>
             </c:when>
             <c:otherwise>
               <div class="apply-message">현재 분양이 진행중입니다.</div>
             </c:otherwise>
           </c:choose>
         </c:when>
         <c:otherwise>
           <c:if test="${myApply != null}">
             <div class="apply-message">
               내 신청 상태:
               <c:choose>
                 <c:when test="${myApply.applyStatus eq 'APPLIED'}">접수</c:when>
                 <c:when test="${myApply.applyStatus eq 'APPROVED'}">승인</c:when>
                 <c:when test="${myApply.applyStatus eq 'REJECTED'}">거절</c:when>
                 <c:when test="${myApply.applyStatus eq 'CANCELLED'}">취소</c:when>
                 <c:when test="${myApply.applyStatus eq 'COMPLETED'}">완료</c:when>
                 <c:otherwise>${myApply.applyStatus}</c:otherwise>
               </c:choose>
             </div>
           </c:if>
           <c:if test="${canCancel}">
             <form action="cancel" method="post" onsubmit="return confirm('신청을 취소하시겠습니까?');">
               <input type="hidden" name="applyNo" value="${myApply.applyNo}">
               <input type="hidden" name="boardNo" value="${adoptDetailVO.boardNo}">
               <button type="submit" class="btn btn-neutral">신청 취소</button>
             </form>
           </c:if>
           <c:if test="${canApply}">
             <form action="apply" method="post" class="apply-form">
               <input type="hidden" name="boardNo" value="${adoptDetailVO.boardNo}">
               <textarea name="applyContent" class="apply-textarea" rows="4" placeholder="신청 메시지를 작성해주세요"></textarea>
               <button type="submit" class="btn btn-positive">신청하기</button>
             </form>
           </c:if>
           <c:if test="${not canApply and myApply != null and myApply.applyStatus eq 'REJECTED'}">
             <div class="apply-message fail">신청이 거절되었습니다.</div>
           </c:if>
           <c:if test="${not canApply and myApply != null and myApply.applyStatus eq 'CANCELLED'}">
             <div class="apply-message">신청을 취소했습니다.</div>
           </c:if>
         </c:otherwise>
       </c:choose>
     </c:if>
   </div>
 </c:if>

 <c:if test="${isOwner}">
   <div class="apply-box">
     <div class="apply-title">📋 신청자 관리</div>
     <c:if test="${param.approve eq 'ok'}"><div class="apply-message ok">승인되었습니다.</div></c:if>
     <c:if test="${param.approve eq 'fail'}"><div class="apply-message fail">승인에 실패했습니다.</div></c:if>
     <c:if test="${param.reject eq 'ok'}"><div class="apply-message ok">거절 처리되었습니다.</div></c:if>
     <c:if test="${param.reject eq 'fail'}"><div class="apply-message fail">거절 처리에 실패했습니다.</div></c:if>
     <c:if test="${param.complete eq 'ok'}"><div class="apply-message ok">분양 완료 처리되었습니다.</div></c:if>
     <c:if test="${param.complete eq 'fail'}"><div class="apply-message fail">완료 처리에 실패했습니다.</div></c:if>

     <c:choose>
       <c:when test="${empty applyList}">
         <div class="apply-message">아직 신청자가 없습니다.</div>
       </c:when>
       <c:otherwise>
         <div class="apply-table-wrapper">
           <table class="apply-table">
             <thead>
               <tr>
                 <th>신청자</th>
                 <th>내용</th>
                 <th class="col-status">상태</th>
                 <th class="col-date">신청일</th>
                 <th class="col-action">처리</th>
               </tr>
             </thead>
             <tbody>
               <c:forEach var="a" items="${applyList}">
                 <tr>
				   <td>
				   	<c:url var="applicantProfileUrl" value="/member/detail">
				   		<c:param name="memberId" value="${a.applicantId}"/>
				   	</c:url>
				   	<a class="profile-link" href="${applicantProfileUrl}"><c:out value="${a.applicantNickname}"/></a> (${a.applicantId})
				   </td>
                   <td><c:out value="${a.applyContent}"/></td>
<td class="col-status">
  <c:choose>
    <c:when test="${a.applyStatus eq 'APPLIED'}"><span class="apply-status-badge applied">접수</span></c:when>
    <c:when test="${a.applyStatus eq 'APPROVED'}"><span class="apply-status-badge approved">승인</span></c:when>
    <c:when test="${a.applyStatus eq 'REJECTED'}"><span class="apply-status-badge rejected">거절</span></c:when>
    <c:when test="${a.applyStatus eq 'CANCELLED'}"><span class="apply-status-badge cancelled">취소</span></c:when>
    <c:when test="${a.applyStatus eq 'COMPLETED'}"><span class="apply-status-badge completed">완료</span></c:when>
    <c:otherwise><span class="apply-status-badge etc"><c:out value="${a.applyStatus}"/></span></c:otherwise>
  </c:choose>
</td>
<td class="col-date"><fmt:formatDate value="${a.applyWtime}" pattern="yy.MM.dd HH:mm"/></td>
<td class="col-action">
  <div class="apply-action-cell">

                       <c:if test="${a.applyStatus eq 'APPLIED' and adoptionStage eq 'OPEN'}">
                         <form action="approve" method="post" class="apply-action-form" onsubmit="return confirm('이 신청을 승인하시겠습니까?');">
                           <input type="hidden" name="applyNo" value="${a.applyNo}">
                           <input type="hidden" name="boardNo" value="${adoptDetailVO.boardNo}">
                           <button type="submit" class="btn btn-positive">승인</button>
                         </form>
                         <form action="reject" method="post" class="apply-action-form" onsubmit="return confirm('이 신청을 거절하시겠습니까?');">
                           <input type="hidden" name="applyNo" value="${a.applyNo}">
                           <input type="hidden" name="boardNo" value="${adoptDetailVO.boardNo}">
                           <button type="submit" class="btn btn-negative">거절</button>
                         </form>
                       </c:if>
                       <c:if test="${not (a.applyStatus eq 'APPLIED' and adoptionStage eq 'OPEN')}">-</c:if>
                     </div>
                   </td>
                 </tr>
               </c:forEach>
             </tbody>
           </table>
         </div>
       </c:otherwise>
     </c:choose>

     <c:if test="${adoptionStage eq 'APPROVED'}">
       <form action="completeAdoption" method="post" onsubmit="return confirm('정말로 분양을 완료 처리하시겠습니까?');">
         <input type="hidden" name="boardNo" value="${adoptDetailVO.boardNo}">
         <button type="submit" class="btn btn-positive">분양 완료 처리</button>
       </form>
     </c:if>

     <c:if test="${adoptionStage eq 'COMPLETED'}">
       <div class="apply-message ok">분양 완료 처리된 게시글입니다.</div>
     </c:if>
   </div>
 </c:if>

	<c:if test="${adoptionStage eq 'COMPLETED'}">
		<div class="apply-box review-box">
			<div class="apply-title">📝 분양 후기</div>
			<c:choose>
				<c:when test="${reviewBoardNo != null}">
					<c:url var="reviewDetailUrl" value="${cp}/board/review/detail">
						<c:param name="boardNo" value="${reviewBoardNo}"/>
					</c:url>
					<div class="apply-message ok">작성된 후기가 있습니다.</div>
					<a class="btn btn-menu" href="${reviewDetailUrl}">후기 보기</a>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${canWriteReview}">
							<c:url var="reviewWriteUrl" value="${cp}/board/review/write">
								<c:param name="adoptionBoardNo" value="${adoptDetailVO.boardNo}"/>
							</c:url>
							<div class="apply-message">분양이 완료되었습니다. 후기를 남겨주세요.</div>
							<a class="btn btn-positive" href="${reviewWriteUrl}">후기 작성</a>
						</c:when>
						<c:otherwise>
							<div class="apply-message">후기 작성은 승인된 신청자만 가능합니다.</div>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>

 <jsp:include page="/WEB-INF/views/board/fragment/adoption-actions.jsp" />
</div>
<jsp:include page="/WEB-INF/views/board/fragment/reply-section.jsp" />

<script src="${cp}/js/board-detail.js"></script>
</div>
<jsp:include page="/WEB-INF/views/template/footer.jsp" />
