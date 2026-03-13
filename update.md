# Update Log

프로젝트 오류 수정 및 개선 기록

---Ver1.0.1
### 1) 회원가입/상세조회용 공개 REST 경로 인터셉터 예외 추가
- 이유: 회원가입 중복검사, 이메일 인증, 게시글 좋아요 조회, 댓글 목록 조회가 로그인 인터셉터에 막히던 문제를 완화합니다.
- 파일: `src/main/java/com/spring/semi/aop/InterceptorConfiguration.java`
```java
"/rest/member/checkId", "/rest/member/checkNickname",
"/rest/member/checkDuplication", "/rest/member/certSend",
"/rest/member/certCheck", "/rest/member/findSend",
"/rest/board/check", "/rest/reply/list"
```