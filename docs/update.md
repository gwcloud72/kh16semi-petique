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