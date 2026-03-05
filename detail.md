# detail.md — 분양 프로세스(Adoption Workflow) 구현 기록

이 문서는 분양 게시판을 중심으로, “왜 이렇게 설계했는지”부터 “어떤 코드/쿼리로 구현했는지”까지 정리한 기술 문서입니다.  
포트폴리오에서 말하고 싶은 주제는 명확합니다.

> **분양 게시판을 게시글이 아니라, 상태가 변하는 프로세스로 만들었다.**

## ERD & Architecture

![Architecture](docs/diagrams/architecture.png)

![ERD](docs/diagrams/erd.png)


---

## 1) 문제 정의: 분양이 ‘글’로 끝나면 서비스가 멈춘다

처음에는 평범한 게시판으로 분양 메뉴를 구성했습니다.  
그런데 테스트를 하다 보니, 화면은 그럴듯해도 “서비스” 느낌이 안 났습니다.

- 누가 신청했는지 남지 않고
- 작성자가 선택(승인)할 수도 없고
- 분양 완료 상태도 없고
- 결국 모든 글이 영원히 ‘모집중’처럼 보였습니다

분양은 콘텐츠가 아니라 **진행되는 일**이기 때문에, 기록과 상태가 필요했습니다.

---

## 2) 목표: 상태가 DB에 저장되고, UI/권한이 그 상태를 따라가게

PETIQUE의 분양은 아래처럼 정의했습니다.

- 신청자가 남긴 신청서(메시지)는 기록으로 남는다.
- 작성자는 신청자 중 1명을 승인할 수 있다(승인은 1명만).
- 승인 이후 완료를 처리하면, 동물의 소유자가 바뀌고 분양이 종료된다.
- 누구에게 어떤 버튼을 보여줄지는 “상태 + 권한”으로 결정한다.

---

## 3) 데이터 모델(핵심 테이블)

### 3-1. adoption_apply (상태를 저장하는 테이블)
파일: `semi/src/main/resources/sql/adoption_apply.sql`

핵심 컬럼:
- `board_no`: 어떤 분양글에 대한 신청인지
- `animal_no`: 어떤 동물인지(글과의 연결을 고정)
- `applicant_id`: 신청자
- `apply_status`: 상태(APPLIED/APPROVED/REJECTED/CANCELLED/COMPLETED)
- `apply_wtime / apply_etime`: 신청/처리 시각

이 테이블이 생기면서, 분양은 “진짜로 움직이는 기능”이 되었습니다.

### 3-2. board_animal (글 ↔ 동물 연결)
파일: `semi/src/main/resources/sql/board_animal.sql`

분양글과 동물은 1:1 연결로 단순하게 가져갔습니다.
- 글이 삭제되면 연결도 삭제(ON DELETE CASCADE)
- 완료가 되면 동물 permission을 `f`로 전환(종료)

---

## 4) 상태 설계(Workflow)

### 4-1. 상태 정의
- `APPLIED` : 신청 접수(대기)
- `APPROVED`: 승인(진행중)
- `REJECTED`: 거절
- `CANCELLED`: 신청 취소
- `COMPLETED`: 분양 완료(종료)

### 4-2. 핵심 규칙(운영 사고 방지)
- 한 분양글에서 **승인은 1명만**
- 완료는 승인 이후에만 가능
- 승인/거절/완료는 작성자만 수행

이 규칙은 UI로만 막지 않고, **서버에서 상태 전이를 검증**합니다.

---

## 5) URL/매핑(Controller)

### 5-1. 분양 게시판: AdoptionBoardController
파일: `semi/src/main/java/com/spring/semi/controller/AdoptionBoardController.java`

| Method | URL | 설명 |
|---|---|---|
| GET | `/board/adoption/list` | 상태 탭/필터/검색/정렬 + 12개 페이지네이션 |
| GET | `/board/adoption/detail?boardNo=` | 상세 + 프로세스 UI + 신청/관리 |
| POST | `/board/adoption/apply` | 신청(APPLIED) |
| POST | `/board/adoption/cancel` | 신청 취소(CANCELLED) |
| POST | `/board/adoption/approve` | 승인(APPROVED) |
| POST | `/board/adoption/reject` | 거절(REJECTED) |
| POST | `/board/adoption/completeAdoption` | 완료(COMPLETED) |

---

## 6) 핵심 코드(Controller → Service → DAO)

### 6-1. 상세 화면에서 UI가 바뀌는 기준값 만들기
상세에서는 아래 값을 먼저 확정합니다.

- `adoptionStage` : OPEN / APPROVED / COMPLETED
- `isOwner` : 작성자 여부
- `myApply` : 내 신청(없을 수 있음)
- `canApply` / `canCancel` : 버튼 노출 여부

발췌(AdoptionBoardController.detail):

```java
AdoptionApplyVO approvedApply = adoptionApplyDao.selectApprovedByBoardNo(boardNo);

String adoptionStage = "OPEN";
if ("f".equals(adoptDetailVO.getAnimalPermission())) adoptionStage = "COMPLETED";
else if (approvedApply != null) adoptionStage = "APPROVED";

boolean isOwner = loginId != null && loginId.equals(adoptDetailVO.getBoardWriter());

AdoptionApplyVO myApply = null;
if (loginId != null) {
    myApply = adoptionApplyDao.selectLatestByBoardAndApplicant(boardNo, loginId);
}

boolean canApply = loginId != null
        && !isOwner
        && !"COMPLETED".equals(adoptionStage)
        && approvedApply == null
        && (myApply == null
            || "REJECTED".equals(myApply.getApplyStatus())
            || "CANCELLED".equals(myApply.getApplyStatus()));

boolean canCancel = loginId != null
        && myApply != null
        && "APPLIED".equals(myApply.getApplyStatus());
```

이렇게 만들어 둔 값들 덕분에 JSP는 단순해집니다.  
“상태와 권한에 따라 무엇을 보여줄지”가 뷰 로직이 아니라 컨트롤러에서 결정됩니다.

---

### 6-2. 상태 전이는 Service에서 트랜잭션으로 묶기
파일: `semi/src/main/java/com/spring/semi/service/AdoptionProcessService.java`

승인/완료는 여러 테이블을 동시에 변경하기 때문에, 도중에 실패하면 데이터가 어긋납니다.  
그래서 `@Transactional`로 묶어 **원자성**을 확보했습니다.

#### 승인(approve): “1명 승인 + 나머지 자동 거절”
```java
@Transactional
public boolean approve(int applyNo, String ownerId) {
    AdoptionApplyDto dto = adoptionApplyDao.selectOne(applyNo);
    AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(dto.getBoardNo());

    if (!ownerId.equals(detail.getBoardWriter())) return false;
    if (adoptionApplyDao.existsApprovedOrCompleted(dto.getBoardNo())) return false;

    boolean ok = adoptionApplyDao.approve(applyNo);
    if (!ok) return false;

    adoptionApplyDao.rejectOthersApplied(dto.getBoardNo(), applyNo);
    return true;
}
```

#### 완료(complete): “신청 상태 완료 + 동물 소유자 이전 + 분양 종료”
```java
@Transactional
public boolean complete(int boardNo, String ownerId) {
    AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(boardNo);
    if (!ownerId.equals(detail.getBoardWriter())) return false;

    AdoptionApplyVO approved = adoptionApplyDao.selectApprovedByBoardNo(boardNo);
    if (approved == null) return false;

    boolean completed = adoptionApplyDao.completeApproved(boardNo);
    if (!completed) return false;

    boolean masterUpdated = animalDao.updateMaster(detail.getAnimalNo(), approved.getApplicantId());
    if (!masterUpdated) return false;

    int updated = adoptionBoardDao.updatePermissionToF(boardNo);
    return updated > 0;
}
```

---

### 6-3. 핵심 쿼리(DAO)

#### 6-3-1. 승인/거절/완료 업데이트 쿼리
파일: `semi/src/main/java/com/spring/semi/dao/AdoptionApplyDao.java`

상태 전이는 SQL에서 `apply_status='APPLIED'` 같은 조건을 꼭 걸어야 안전합니다.  
(중복 클릭/동시 요청에서 의도치 않게 상태가 튀는 것을 막습니다)

```sql
update adoption_apply
set apply_status = 'APPROVED', apply_etime = systimestamp
where apply_no = ? and apply_status = 'APPLIED';
```

```sql
update adoption_apply
set apply_status = 'REJECTED', apply_etime = systimestamp
where board_no = ? and apply_status = 'APPLIED' and apply_no <> ?;
```

```sql
update adoption_apply
set apply_status = 'COMPLETED', apply_etime = systimestamp
where board_no = ? and apply_status = 'APPROVED';
```

#### 6-3-2. 리스트에서 상태를 함께 계산(OPEN/APPROVED/COMPLETED)
파일: `semi/src/main/java/com/spring/semi/dao/AdoptionBoardDao.java`

```sql
case
  when a.animal_permission = 'f' then 'COMPLETED'
  when exists (
      select 1 from adoption_apply aa
      where aa.board_no = b.board_no
        and aa.apply_status in ('APPROVED','COMPLETED')
  ) then 'APPROVED'
  else 'OPEN'
end as adoption_stage
```

리스트에서 상태 뱃지를 표시하기 위해 “글마다 상태를 다시 조회”할 필요가 없고,  
탭 필터도 같은 기준으로 적용할 수 있습니다.

---

## 7) 페이지네이션(12개 고정) 설계

- 분양 리스트: 12개(카드형 UI 기준으로 한 화면에 맞음)
- 신청자 목록/내역: 10개(표 UI에 안정적)

리스트의 begin/end 계산은 단순하게 처리했습니다.

```java
int begin = (page - 1) * pageSize + 1;
int end = page * pageSize;
```

---


## 7-1) 알림(Notifications)과 분양 후기 연결

분양 프로세스는 상태 전이만으로도 동작하지만, 사용자가 체감하는 완성도는 “다음 행동이 자연스럽게 이어지느냐”에서 갈립니다.

- 신청자는 승인/거절/완료 결과를 놓치기 쉽고
- 작성자는 새 신청이 들어왔는지 수시로 확인해야 합니다
- 완료 후에는 분양이 끝이 아니라, 후기(콘텐츠)로 이어지면 서비스처럼 보입니다

그래서 웹소켓 채팅 없이도 운영 가능한 방식으로 **알림 저장형(Notification)** 과 **분양글 ↔ 후기글 1:1 연결**을 추가했습니다.

### (1) 알림 테이블
- `NOTIFICATION(noti_no, member_id, noti_type, noti_message, noti_url, noti_read, noti_wtime)`
- 헤더 배지용으로 `member_id + noti_read` 인덱스, 목록용으로 `member_id + noti_wtime` 인덱스를 둡니다

### (2) 분양글 ↔ 후기글 연결
- `ADOPTION_REVIEW_LINK(adoption_board_no PK, review_board_no, link_wtime)`
- 분양글 기준으로 후기가 1개만 연결되도록 `adoption_board_no`를 PK로 구성했습니다

### (3) 핵심 SQL(패치)
패치 파일: `semi/src/main/resources/sql/notification_review_patch.sql`

- **승인/완료 유일성 보장(함수 기반 유니크 인덱스)**
```sql
create unique index ux_adoption_apply_one_final
on adoption_apply (case when apply_status in ('APPROVED','COMPLETED') then board_no end);
```
- **알림 테이블/시퀀스 생성**
```sql
create table notification(...);
create sequence notification_seq;
```
- **분양 후기 링크 테이블 생성**
```sql
create table adoption_review_link(...);
```

### (4) 화면에서의 변화
- 분양 신청/승인/거절/완료가 발생하면 알림이 저장되고, 헤더/마이페이지에서 확인됩니다
- 완료된 분양 상세에서는 후기 작성/보기 버튼이 열려, 완료 이후 동선이 자연스럽게 이어집니다

## 8) 트러블슈팅(분양 프로세스 중심)

### 8-1. “승인을 두 번 누르면 어떻게 되지?” (중복 승인 방지)
**상황**  
작성자가 승인 버튼을 여러 번 누르거나, 거의 동시에 승인 요청이 들어오면  
승인자가 두 명이 될 수 있습니다.

**해결**
- 승인 전에 `existsApprovedOrCompleted(boardNo)` 검사
- update도 `where apply_status='APPLIED'`일 때만 성공
- 승인 성공 후 남은 대기(APPLIED)는 자동 거절로 정리

---

### 8-2. “완료 처리에서 데이터가 분리된다” (트랜잭션)
**상황**  
완료 처리에서 `신청 상태`, `동물 소유자`, `분양 종료(permission)`이 따로 움직이면  
중간 실패 시 데이터가 찢어질 수 있습니다.

**해결**
- 완료 처리 로직을 서비스에 모으고 `@Transactional`로 묶음
- 승인된 신청이 없으면 완료 불가(사전 검증)
- 완료의 순서를 고정(신청 완료 → 소유자 변경 → 종료 처리)

---

### 8-3. “상태가 한 글자씩 세로로 보인다” (상태 UI 깨짐)
**상황**  
신청자 관리 테이블에서 `상태`, `접수`, `완료` 같은 글자가 `상\n태`처럼 보였습니다.

**원인**  
테이블 폭이 줄어들 때 한국어가 글자 단위로 줄바꿈되면서 발생.

**해결**
- 상태/신청일/처리 컬럼에 `white-space: nowrap`
- 버튼 영역은 `flex-wrap: nowrap`, 최소 폭 지정

---

### 8-4. “리스트는 OPEN인데 상세는 APPROVED” (상태 기준 불일치)
**상황**  
리스트와 상세가 서로 다른 기준으로 상태를 판단하면 사용자에게 혼란을 줍니다.

**해결**
- 리스트의 상태 계산(approval exists + permission)을 상세와 동일 기준으로 통일
- `adoptionStage`는 한 군데에서 결정되도록 정리

---

## 9) 스크린샷 위치
- 저장 위치: `docs/screenshots/` (README에서 바로 불러옵니다)

---

## 10) 회고
분양 기능을 만들면서 느낀 건, “기능이 많다”보다  
**한 기능이 실제 서비스처럼 흐르게 만드는 것**이 훨씬 중요하다는 점이었습니다.

상태/권한/데이터 무결성을 끝까지 맞추고 나니,  
분양 게시판은 단순 게시판이 아니라 “진짜 서비스 기능”처럼 보이기 시작했습니다.
