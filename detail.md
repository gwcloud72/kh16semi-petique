# detail.md — 분양 프로세스(Adoption Workflow) 구현 기록

이 문서는 PETIQUE 프로젝트의 **분양 게시판 기능**을 기준으로,  
“왜 이렇게 설계했는지(의도/문제)”부터 “어떤 코드/쿼리로 구현했는지(구현)”까지 정리한 기술 문서입니다.

포트폴리오에서 말하고 싶은 핵심은 하나입니다.

> **분양 게시판을 ‘게시글’이 아니라, 상태가 변하는 ‘프로세스’로 만들었다.**

처음에는 분양 메뉴를 일반 게시판처럼 만들었다가, 테스트를 하면서 “서비스 느낌이 안 난다”는 지점이 명확하게 보였습니다.  
그때부터 분양을 **콘텐츠(글)** 가 아니라 **진행되는 일(워크플로우)** 로 재정의했고, 그 기준으로 DB/권한/UI를 다시 맞췄습니다.

---

## ERD & Architecture

> 이미지 파일은 레포 기준 경로로 링크합니다.

![Architecture](docs/diagrams/architecture.png)

![ERD](docs/diagrams/erd.png)

---

## 1) 문제 정의: 분양이 ‘글’로 끝나면 서비스가 멈춘다

초기에는 분양 메뉴를 “평범한 게시판”으로 구성했습니다.

- 작성자가 글을 올린다
- 다른 사용자는 글을 본다
- 댓글 정도만 남긴다

이 구조를 그대로 분양에 가져오면 화면은 그럴듯합니다.  
그런데 실제로 사용 흐름을 따라가 보면 **기능이 멈춘다**는 문제가 생깁니다.

### 테스트 중 발견한 문제

- **누가 신청했는지가 남지 않는다**  
  댓글은 “대화”에 가깝고, 신청서처럼 구조화된 기록이 아니어서 관리가 어렵습니다.
- **작성자가 선택(승인)할 수 없다**  
  “이 사람에게 분양합니다”라는 결정을 시스템이 저장하지 못합니다.
- **분양 완료 상태가 없다**  
  분양이 끝났는지 사용자 입장에서 구분이 안 됩니다.
- 결국 모든 글이 영원히 **‘모집중’처럼 보이는 문제**가 생깁니다.

여기서 결론은 단순했습니다.

> 분양은 글이 아니라 **진행되는 일**이다.  
> 진행되는 일이라면 “기록”과 “상태”가 있어야 한다.

그래서 분양을 게시판으로 두지 않고, **상태를 가진 프로세스**로 다시 설계했습니다.

---

## 2) 목표: 상태가 DB에 저장되고, UI/권한이 그 상태를 따라가게

PETIQUE의 분양을 아래처럼 정의했습니다.

- 신청자가 남긴 신청서(메시지)는 **기록으로 남는다**
- 작성자는 신청자 중 **1명만 승인할 수 있다** (중요: 승인 유일성)
- 승인 이후 완료를 처리하면
  - 신청 상태가 완료로 바뀌고
  - 동물의 소유자가 바뀌고
  - 분양이 종료된다
- 누구에게 어떤 버튼을 보여줄지는 **“상태 + 권한”** 으로 결정한다

여기서 중요한 포인트는 두 가지였습니다.

1) UI만으로 막으면 운영에서 깨질 수 있음 (중복 클릭, 동시 요청, 권한 우회 등)  
2) 그래서 서버에서 상태 전이를 검증하고, DB에서도 유일성을 보조해야 함

즉, **프론트(버튼 숨김)** 는 “사용성”, **서버 검증/DB 제약**은 “무결성”을 담당하도록 역할을 나눴습니다.

---

## 3) 데이터 모델(핵심 테이블)

### 3-1. adoption_apply (상태를 저장하는 테이블)


분양 프로세스의 중심 테이블입니다.  
“신청이 있었다/없었다”를 넘어서 **신청 상태가 어떻게 변했는지**를 저장합니다.

#### 핵심 컬럼

- `board_no` : 어떤 분양글에 대한 신청인지
- `animal_no` : 어떤 동물인지(글과의 연결을 고정)
- `applicant_id` : 신청자
- `apply_status` : 상태(APPLIED/APPROVED/REJECTED/CANCELLED/COMPLETED)
- `apply_wtime / apply_etime` : 신청/처리 시각

#### 왜 `animal_no`를 신청 테이블에 중복 저장했는가?

분양글은 `board_animal`로 동물과 연결되지만, 신청은 “그 시점의 분양 대상”을 확정해야 합니다.

- 운영 중에 글/연결이 수정되거나(혹은 개발 중 실수로 수정되거나)
- 데이터가 꼬였을 때

신청 데이터가 “어느 동물을 대상으로 한 신청인지”를 확실하게 들고 있어야 안정적입니다.

그래서 `adoption_apply`에도 `animal_no`를 넣어서 **신청의 대상이 흔들리지 않게** 했습니다.  
이 테이블이 생기면서 분양은 “진짜로 움직이는 기능”이 되었습니다.

---

### 3-2. board_animal (글 ↔ 동물 연결)


분양글과 동물은 **1:1 연결**로 단순하게 가져갔습니다.

- 한 분양글은 한 동물만 다룬다 (UI/서비스 흐름에 자연스럽다)
- 복수 동물을 한 글에서 다루면 신청/승인/완료 처리 기준이 어려워진다

#### 운영 규칙 반영

- 글이 삭제되면 연결도 삭제 (`ON DELETE CASCADE`)
- 완료가 되면 동물 `permission`을 `f`로 전환 (더 이상 분양 대상으로 노출되지 않게 종료)

> 여기서 `permission='f'`는 “완료됨”을 UI/리스트에서 판단할 수 있는 안정적인 기준으로도 사용했습니다.  
> 신청 테이블이 아니라 **동물 자체가 분양 대상에서 내려간 상태**를 의미하기 때문입니다.

---

## 4) 상태 설계(Workflow)

### 4-1. 상태 정의

- `APPLIED` : 신청 접수(대기)
- `APPROVED` : 승인(진행중)
- `REJECTED` : 거절
- `CANCELLED` : 신청 취소
- `COMPLETED` : 분양 완료(종료)

### 4-2. Stage(화면 표시용)와 Status(DB 저장)의 분리

DB에는 `apply_status`가 있지만, 화면에서는 “이 분양이 지금 어느 단계인지”를 더 직관적으로 보여주고 싶었습니다.

그래서 화면/리스트에서 사용하는 단계 값을 아래처럼 단순화했습니다.

- `OPEN` : 승인된 신청이 없음, 분양 진행 가능
- `APPROVED` : 승인된 신청이 존재, 작성자 승인 완료(진행중)
- `COMPLETED` : 동물 permission이 `f`, 분양 종료

> 즉, **Status(신청 단위)** 와 **Stage(게시글/프로세스 단위)** 를 분리해서 UX를 단순화했습니다.

### 4-3. 핵심 규칙(운영 사고 방지)

- 한 분양글에서 **승인은 1명만**
- 완료는 승인 이후에만 가능
- 승인/거절/완료는 작성자만 수행
- 신청 취소는 **대기(APPLIED)** 상태일 때만 가능
- 거절된 사용자는 다시 신청 가능(서비스 정책)

이 규칙은 UI로만 막지 않고, **서버에서 상태 전이를 검증**합니다.

(예: 승인된 상태에서 다시 승인 요청이 들어오면 서버에서 false 처리)

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

POST 요청은 모두 처리 후 상세로 redirect 하는 형태로 구성했습니다(일반적인 PRG 패턴).  
새로고침 시 중복 요청이 되지 않게 만들기 위한 선택입니다.

---

## 6) 핵심 코드(Controller → Service → DAO)

### 6-1. 상세 화면에서 UI가 바뀌는 “기준값” 만들기

상세 페이지는 버튼이 많습니다.

- 신청 / 취소
- 작성자라면 승인 / 거절 / 완료
- 완료 상태라면 후기 작성/보기 등

처음에는 JSP에서 조건 분기를 많이 했는데, 그렇게 하면

- 조건이 화면에 흩어지고
- 같은 조건을 리스트/상세에서 반복하고
- 작은 규칙 변경에도 JSP 여러 군데가 터지는 문제

가 생겼습니다.

그래서 상세에서는 아래 값을 **컨트롤러에서 먼저 확정**한 뒤, JSP는 그 값을 그대로 사용하도록 정리했습니다.

- `adoptionStage` : OPEN / APPROVED / COMPLETED
- `isOwner` : 작성자 여부
- `myApply` : 내 신청(없을 수 있음)
- `canApply`, `canCancel` : 버튼 노출 여부
- (추가로 실제 구현에서는 `canApprove`, `canReject`, `canComplete` 같은 값도 같은 방식으로 확장 가능)

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

#### 이렇게 정리했을 때 장점

- JSP는 **보여주기만** 하면 되고(조건 최소화)
- 상태/권한 규칙 변경이 생겨도 컨트롤러 한 군데에서 정리 가능
- “왜 이 버튼이 보이냐”를 디버깅할 때, 컨트롤러에서 기준값만 보면 됨

---

### 6-2. 상태 전이는 Service에서 트랜잭션으로 묶기

파일: `semi/src/main/java/com/spring/semi/service/AdoptionProcessService.java`

승인/완료는 “한 테이블 update”로 끝나지 않습니다.

- 승인: 한 명 승인 + 나머지 자동 거절
- 완료: 신청 상태 완료 + 동물 소유자 이전 + 분양 종료(permission 변경)

이 중 하나라도 실패하면 데이터가 어긋나기 때문에, 서비스에서 `@Transactional`로 묶어 **원자성**을 확보했습니다.

#### 승인(approve): “1명 승인 + 나머지 자동 거절”

```java
@Transactional
public boolean approve(int applyNo, String ownerId) {
    AdoptionApplyDto dto = adoptionApplyDao.selectOne(applyNo);
    AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(dto.getBoardNo());

    // 1) 권한 검증: 작성자만 승인 가능
    if (!ownerId.equals(detail.getBoardWriter())) return false;

    // 2) 운영 규칙: 한 게시글에서 최종(APPROVED/COMPLETED) 상태는 1명만
    if (adoptionApplyDao.existsApprovedOrCompleted(dto.getBoardNo())) return false;

    // 3) 상태 조건 update: APPLIED일 때만 APPROVED로 바뀜
    boolean ok = adoptionApplyDao.approve(applyNo);
    if (!ok) return false;

    // 4) 승인 성공 시 나머지 대기(APPLIED) 자동 거절로 정리
    adoptionApplyDao.rejectOthersApplied(dto.getBoardNo(), applyNo);
    return true;
}
```

여기서 핵심은 “검증 → 조건 update → 후처리” 순서를 고정했다는 점입니다.  
그리고 동시 요청(중복 클릭)이 들어와도 `approve()`의 update가 `apply_status='APPLIED'` 조건 때문에 **두 번째 요청은 자연스럽게 실패**합니다.

#### 완료(complete): “신청 상태 완료 + 동물 소유자 이전 + 분양 종료”

```java
@Transactional
public boolean complete(int boardNo, String ownerId) {
    AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(boardNo);

    // 1) 권한 검증
    if (!ownerId.equals(detail.getBoardWriter())) return false;

    // 2) 승인된 신청이 있어야 완료 가능
    AdoptionApplyVO approved = adoptionApplyDao.selectApprovedByBoardNo(boardNo);
    if (approved == null) return false;

    // 3) 승인 상태를 완료로 변경
    boolean completed = adoptionApplyDao.completeApproved(boardNo);
    if (!completed) return false;

    // 4) 동물 소유자 변경(분양받은 사람으로)
    boolean masterUpdated = animalDao.updateMaster(detail.getAnimalNo(), approved.getApplicantId());
    if (!masterUpdated) return false;

    // 5) 분양 종료 처리(동물 permission='f')
    int updated = adoptionBoardDao.updatePermissionToF(boardNo);
    return updated > 0;
}
```

완료 처리 순서는 의도적으로 고정했습니다.

- (완료 가능한지 검증) → (신청 완료 처리) → (소유자 변경) → (분양 종료 플래그)

이 순서를 유지하면 운영에서 디버깅할 때도 “어디까지 됐는지” 추적이 쉽고, 트랜잭션 실패 시 롤백으로 정리됩니다.

---

### 6-3. 핵심 쿼리(DAO)

#### 6-3-1. 승인/거절/완료 업데이트 쿼리

파일: `semi/src/main/java/com/spring/semi/dao/AdoptionApplyDao.java`

상태 전이는 SQL에서 `apply_status='APPLIED'` 같은 조건을 꼭 걸었습니다.  
UI에서 버튼을 막아도, 네트워크 지연/중복 클릭/동시 요청에서 상태가 튀는 경우가 실제로 생깁니다.

**승인(대기 → 승인)**

```sql
update adoption_apply
set apply_status = 'APPROVED', apply_etime = systimestamp
where apply_no = ? and apply_status = 'APPLIED';
```

**나머지 자동 거절(대기 → 거절)**

```sql
update adoption_apply
set apply_status = 'REJECTED', apply_etime = systimestamp
where board_no = ? and apply_status = 'APPLIED' and apply_no <> ?;
```

**완료(승인 → 완료)**

```sql
update adoption_apply
set apply_status = 'COMPLETED', apply_etime = systimestamp
where board_no = ? and apply_status = 'APPROVED';
```

> 조건 update를 넣으면 “이미 처리된 건 다시 처리되지 않는다”가 DB 레벨에서 보장됩니다.

---

#### 6-3-2. 리스트에서 상태를 함께 계산(OPEN/APPROVED/COMPLETED)

파일: `semi/src/main/java/com/spring/semi/dao/AdoptionBoardDao.java`

리스트에서 상태 뱃지를 표시하려고 글마다 상태를 다시 조회하면 N+1에 가까운 구조가 됩니다.  
그래서 리스트 조회 쿼리에서 **stage를 함께 계산**하도록 했습니다.

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

이 방식의 장점은

- 리스트 상태 뱃지 표시
- 상태 탭 필터
- 정렬 기준

을 **같은 기준**으로 처리할 수 있다는 점입니다.

그리고 상세에서도 같은 판단 로직을 쓰기 때문에, “리스트는 OPEN인데 상세는 APPROVED” 같은 불일치를 줄일 수 있습니다.

---

## 7) 페이지네이션(12개 고정) 설계

UI 기준으로 분양 리스트는 카드형으로 구성되어서 한 화면에 12개가 가장 깔끔했습니다.

- 분양 리스트: 12개 (카드 UI 기준 한 화면 밀도)
- 신청자 목록/내역: 10개 (테이블 UI 기준 안정적)

begin/end 계산은 단순하게 처리했습니다.

```java
int begin = (page - 1) * pageSize + 1;
int end = page * pageSize;
```

> (Oracle 기준이라면 begin/end를 ROWNUM으로 감싸는 방식으로 구현했고, MySQL이라면 LIMIT/OFFSET로 동일 개념 적용 가능합니다.)

---

## 7-1) 알림(Notifications)과 분양 후기 연결

분양 프로세스는 상태 전이만으로도 기능적으로 동작합니다.  
하지만 실제 서비스를 사용할 때는 **사용자가 다음에 무엇을 해야 하는지 자연스럽게 이어지는 흐름**이 중요합니다.

예를 들어,

- 신청자는 승인 / 거절 / 완료 결과를 바로 알기 어렵고
- 작성자는 새 신청이 들어왔는지 확인하기 위해 직접 페이지를 다시 확인해야 하며
- 분양이 완료된 이후에도 흐름이 끊기면 기능이 끝난 느낌이 납니다.

그래서 분양 기능에 다음 두 가지를 추가했습니다.

- **알림 저장 기능(Notification)**
- **분양글 ↔ 후기글 연결 기능**

웹소켓 기반의 실시간 알림 대신  
**DB에 알림을 저장하는 방식**을 사용했습니다.

이 방식은 구현이 단순하고, 사용자가 접속하지 않았더라도  
나중에 알림을 확인할 수 있다는 장점이 있습니다.

---

### (1) 알림 테이블

알림 기능을 위해 별도의 알림 테이블을 추가했습니다.

주요 컬럼은 다음과 같습니다.

- `noti_no` : 알림 번호
- `member_id` : 알림을 받을 사용자
- `noti_type` : 알림 종류
- `noti_message` : 알림 내용
- `noti_url` : 클릭 시 이동할 페이지
- `noti_read` : 읽음 여부
- `noti_wtime` : 생성 시간

알림은 다음 상황에서 생성됩니다.

- 분양 신청이 들어왔을 때 → **작성자에게 알림**
- 작성자가 신청을 승인했을 때 → **신청자에게 알림**
- 작성자가 신청을 거절했을 때 → **신청자에게 알림**
- 작성자가 분양 완료 처리했을 때 → **신청자에게 알림**

읽음 여부는 `noti_read` 값을 통해 구분하고  
사용자는 다음 위치에서 알림을 확인할 수 있도록 구현했습니다.

- 헤더 알림 메뉴
- 마이페이지 알림 목록

---

### (2) 분양글 ↔ 후기글 연결

분양이 완료된 이후에도 서비스 흐름이 이어지도록  
**분양글과 후기글을 연결하는 구조**를 추가했습니다.

분양글과 후기글을 연결하는 별도의 테이블을 만들고  
한 분양글에는 하나의 후기만 연결되도록 설계했습니다.

이렇게 하면 분양 상세 페이지에서 다음과 같은 흐름이 가능합니다.

- 후기가 없는 경우 → **후기 작성 버튼 표시**
- 후기가 있는 경우 → **후기 보기 버튼 표시**

이 기능을 통해 분양 완료 이후에도  
사용자가 자연스럽게 후기를 남기고 다른 사용자와 경험을 공유할 수 있도록 했습니다.

---


### (4) 화면에서의 변화

- 분양 신청/승인/거절/완료가 발생하면 알림이 저장되고, 헤더/마이페이지에서 확인됩니다
- 완료된 분양 상세에서는 후기 작성/보기 버튼이 열려, 완료 이후 동선이 자연스럽게 이어집니다

---

## 8) 트러블슈팅(분양 프로세스 중심)

분양 기능을 구현하면서 단순한 UI 문제보다는  
**상태 관리와 데이터 무결성**을 어떻게 유지할 것인지가 중요한 이슈였습니다.

특히 다음과 같은 문제를 중심으로 구조를 정리했습니다.

---

### 8-1. 승인자가 여러 명이 될 수 있는 문제

**상황**

작성자가 승인 버튼을 여러 번 누르거나  
거의 동시에 승인 요청이 들어오는 경우  
한 분양글에서 승인자가 여러 명이 될 가능성이 있습니다.

분양 기능에서는 승인자가 **1명만 존재해야 하기 때문에**  
이 문제를 반드시 막아야 했습니다.

**해결**

다음과 같은 방식으로 중복 승인을 방지했습니다.

- 승인 처리 전에 **이미 승인된 신청이 있는지 검사**
- 승인 update는 **APPLIED 상태일 때만 변경되도록 조건 설정**
- 승인 성공 시 나머지 신청(APPLIED)은 **자동으로 REJECTED 처리**

이렇게 하면 중복 요청이 들어오더라도  
상태 전이가 한 번만 발생하도록 제어할 수 있습니다.

---

### 8-2. 분양 완료 처리 중 데이터 불일치 문제

**상황**

분양 완료 처리는 다음 작업이 함께 실행됩니다.

- 신청 상태를 COMPLETED로 변경
- 동물 소유자를 신청자로 변경
- 분양 종료 상태로 변경

이 작업이 각각 따로 실행되면  
중간 실패 시 데이터가 서로 맞지 않는 문제가 발생할 수 있습니다.

예를 들어

- 신청 상태는 완료인데
- 동물 소유자는 변경되지 않는 상황

같은 문제가 발생할 수 있습니다.

**해결**

완료 처리 로직을 서비스 계층에 모으고  
하나의 트랜잭션으로 묶어 처리하도록 구성했습니다.

```java
@Transactional

### 8-3. 분양 상태 판단 기준 통일

**상황**

분양 상태는 다음 두 가지 기준으로 판단됩니다.

- 승인된 신청이 존재하는지
- 동물의 분양 가능 상태(permission)

초기에는 리스트 화면과 상세 화면에서
각각 다른 기준으로 상태를 판단하는 코드가 존재했습니다.

이 경우 사용자 입장에서는 다음과 같은 문제가 발생할 수 있습니다.

- 리스트에서는 모집중
- 상세에서는 승인 상태

같은 글이 서로 다른 상태로 보일 수 있었습니다.

**해결**

분양 상태 판단 기준을 다음과 같이 통일했습니다.

- 승인된 신청이 있으면 → **APPROVED**
- 동물 permission이 `f`이면 → **COMPLETED**
- 그 외에는 → **OPEN**

그리고 이 기준을

- 리스트 조회
- 상세 화면

모두 동일하게 사용하도록 정리했습니다.

## 9) 스크린샷 위치

- 저장 위치: `docs/screenshots/`  
  (README에서 바로 불러올 수 있게 레포 내부 경로로 통일했습니다)

---

## 10) 회고

분양 기능을 만들면서 느낀 건, “기능이 많다”보다  
**한 기능이 실제 서비스처럼 흐르게 만드는 것**이 훨씬 중요하다는 점이었습니다.

처음에는 게시판을 하나 더 만드는 느낌이었는데,  
상태/권한/데이터 무결성을 끝까지 맞추고 나니 분양은 더 이상 단순 게시판이 아니라

- 누가 신청했고
- 누가 승인됐고
- 언제 완료됐고
- 완료 후에는 후기로 이어지는

“진짜 서비스 기능”처럼 보이기 시작했습니다.

다음 개선으로는

- 알림을 더 자연스럽게(읽음 처리/필터)
- 완료 후 후기 작성 UX 개선


같은 부분을 생각하고 있습니다.
