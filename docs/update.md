# Update Log

프로젝트 오류 수정 및 개선 기록

# 26/03/13 Ver1.0.1
### 1) 회원가입/상세조회용 공개 REST 경로 인터셉터 예외 추가
- 이유: 회원가입 중복검사, 이메일 인증, 게시글 좋아요 조회, 댓글 목록 조회가 로그인 인터셉터에 막히던 문제를 완화합니다.
- 파일: `src/main/java/com/spring/semi/aop/InterceptorConfiguration.java`
```java
"/rest/member/checkId", "/rest/member/checkNickname",
"/rest/member/checkDuplication", "/rest/member/certSend",
"/rest/member/certCheck", "/rest/member/findSend",
"/rest/board/check", "/rest/reply/list"
```
### 2) 인증번호 만료 시간 계산 수정
- 이유: Duration 계산 순서가 반대로 되어 있어 만료가 사실상 동작하지 않던 부분을 고쳤습니다.
- 파일: `src/main/java/com/spring/semi/restcontroller/MemberRestController.java`
```java
Duration duration = Duration.between(sent, current);
```
### 3) 회원 탈퇴 오류 수정
- 이유 : jsp 의 파라미터 값이 안맞아서 수정하였습니다. 그리고 비밀번호를 틀려도 세션을 지우기때문에 수정하였습니다.
src/main/java/com/spring/semi/controller/MemberController.java
```
boolean deleted = memberService.deleteMember(loginId, memberPw);
if (!deleted) {
return "redirect:drop?error";
}
```
### 4) 닉네임검사 중복로직 변경
- 이유: nickname이 아닌 memberid를 체크하고있습니다 
(1)데이터베이스 닉네임 찾기
(2) 없으면 (중복아님)  false를 반환합니다
(3) 중복이면 true를 반환합니다
src/main/java/com/spring/semi/restcontroller/MemberRestController.java
```
@PostMapping("/checkDuplication")
public boolean checkDuplication(
        @RequestParam String memberId,
        @RequestParam String memberNickname
        ) {
    MemberDto nicknameOwner = memberDao.selectForNickname(memberNickname);
    if (nicknameOwner == null) return false;
    return !nicknameOwner.getMemberId().equals(memberId);
}
``` 
# 26/03/21 Ver 1.0.5
### 5) 분양 완료 로직변경
- 이유 : 동물 소유주 변경이나 동물 상태 변경시 false만 반환하고 끝날수있어서 보충했습니다.
핵심 데이터는 안전하게 롤백되게 하고, 알림은 별도로 처리해서 완료 로직의 안정성을 높였습니다.
src/main/java/com/spring/semi/service/AdoptionProcessService.java
```
    @Transactional
    public boolean complete(int boardNo, String ownerId) {
        // (1) 로그인 여부 확인
        if (ownerId == null) return false;

        // (2) 게시글 정보 조회
        AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(boardNo);
        if (detail == null) return false;

        // (3) 작성자 본인인지 확인
        if (!ownerId.equals(detail.getBoardWriter())) return false;

        // (4) 이미 완료된 글인지 확인
        if ("f".equals(detail.getAnimalPermission())) return false;

        // (5) 승인된 신청 건 조회
        AdoptionApplyVO approved = adoptionApplyDao.selectApprovedByBoardNo(boardNo);
        if (approved == null) return false;

        // (6) 신청 상태를 완료로 변경
        boolean completed = adoptionApplyDao.completeApproved(boardNo);
        if (!completed) return false;

        // (7) 동물 소유자 변경
        // 핵심 데이터라 실패하면 롤백되도록 예외 처리
        boolean masterUpdated = animalDao.updateMaster(
            detail.getAnimalNo(),
            approved.getApplicantId()
        );
        if (!masterUpdated) {
            throw new IllegalStateException("동물 소유자 변경 실패");
        }

        // (8) 게시글 상태를 완료로 변경
        int updated = adoptionBoardDao.updatePermissionToF(boardNo);
        if (updated <= 0) {
            throw new IllegalStateException("분양 완료 상태 반영 실패");
        }

        // (9) 완료 알림 발송
        // 알림은 부가 기능이라 실패해도 본 처리는 유지
        try {
            String url = "/board/adoption/detail?boardNo=" + boardNo;
            notificationService.notify(
                approved.getApplicantId(),
                "ADOPTION_COMPLETE",
                "분양이 완료 처리되었습니다. 후기 작성도 가능해요.",
                url
            );
        }
        catch (Exception e) {
        	log.error("분양 완료 알림 실패 - 게시글 번호: {}", boardNo, e);
        }

        // (10) 최종 성공 반환
        return true;
    }
```