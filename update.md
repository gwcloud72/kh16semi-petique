# Update Log

프로젝트 오류 수정 및 개선 기록

---Ver1.0.1
### 1) 회원가입/상세조회용 공개 REST 경로 인터셉터 예외 추가
- 파일: `src/main/java/com/spring/semi/aop/InterceptorConfiguration.java`
```java
"/rest/member/checkId", "/rest/member/checkNickname",
"/rest/member/checkDuplication", "/rest/member/certSend",
"/rest/member/certCheck", "/rest/member/findSend",
"/rest/board/check", "/rest/reply/list"
```