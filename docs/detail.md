# PETIQUE | 분양 게시판 상세 정리

이 문서는 PETIQUE 전체를 설명하기 위한 것이 아니라, 제가 담당했던 분양 게시판 파트를 코드 중심으로 다시 정리한 기록입니다. 겉으로 보기에는 단순히 게시글이 하나 더 추가된 기능처럼 느껴질 수 있지만, 실제로는 신청자가 생기고, 그 중 한 명만 승인될 수 있으며, 분양이 완료되면 소유자와 상태도 함께 변경되어야 하기에 일반 게시판과는 다르게 접근해야 했던 부분입니다.

이러한 이유로 분양 기능을 처음부터 단순한 '글'보다는, 일련의 상태가 자연스럽게 이어지는 하나의 흐름으로 바라보고 작업했습니다. 리스트부터 상세 보기, 신청자 관리, 분양 완료, 후기 작성, 알림 기능까지 모두 같은 원칙 아래 유기적으로 연결되도록 설계하는 것이 이번 작업의 핵심 목표였습니다.

---

## 담당 역할

- 분양 게시판 목록 / 상세 화면 구현
- 분양 신청 / 취소 / 승인 / 거절 / 완료 프로세스 설계 및 구현
- 상태 기반 버튼 노출과 권한 처리
- `ADOPTION_APPLY`, `BOARD_ANIMAL`, `ANIMAL` 연동 로직 구성
- 마이페이지 신청 내역, 알림, 후기 작성 동선 연동

---

## 먼저 정리한 문제

- 리스트와 상세 화면에서 동일한 글을 서로 다르게 보여줄 수 있는 경우 발생  
- 승인자가 두 명 이상 생기거나, 완료 처리 후에 일부 데이터가 제대로 반영되지 않을 수 있음  
- 후기 버튼 노출만으로는 권한 제어가 충분하지 않은 문제

분양 기능은 겉보기에는 일반 게시판과 비슷하지만, 실제로는 글의 상태 변화와 권한 관리가 밀접하게 연결되어 있습니다. 그래서 UI 구성에 앞서 먼저 상태 기준과 완료 처리 순서를 어느 계층에서 책임질지부터 명확히 정의하는 작업을 우선으로 했습니다.

---

## 리스트 상태 계산

<table>
<tr>
<td width="48%"><img src="screenshots/01_adoption_list.png" width="100%" alt="분양 리스트 화면"></td>
<td valign="top">
분양 리스트는 글 목록을 나열한 것처럼 보이지만, 실제로는 각각의 카드에서 현재 상태가 가장 먼저 드러납니다.<br><br>
예를 들어,<br>
- 현재 모집중인지<br>
- 이미 승인자가 존재하는지<br>
- 완료된 글인지<br><br>
이처럼 상태 정보가 중요하기 때문에, 만약 상세 화면에서만 상태를 따로 계산한다면 리스트와 불일치하는 상황이 쉽게 발생합니다. 이런 문제를 막기 위해, 글 리스트를 조회하는 쿼리 단계에서부터 각 글의 상태를 함께 계산하도록 구조를 설계했습니다.
</td>
</tr>
</table>

연결 기준
- 컨트롤러에서는 필터, 검색, 정렬, 페이지네이션 등 기본적인 목록 처리만 담당했습니다.
- 실제 상태 계산은 리스트 조회 쿼리에서 함께 처리하도록 해, 처음부터 정확한 정보를 제공합니다.
- 리스트와 상세 화면 모두 동일한 상태 기준을 적용해 일관성을 유지하도록 했습니다.

### Controller

```java
@GetMapping("/list")
public String list(@ModelAttribute PageFilterVO pageFilterVO, Model model) {
    final int boardType = 4;
    final int pageSize = 12;

    int page = (pageFilterVO.getPage() > 0) ? pageFilterVO.getPage() : 1;
    pageFilterVO.setSize(pageSize);
    pageFilterVO.setBegin((page - 1) * pageSize + 1);
    pageFilterVO.setEnd(page * pageSize);

    List<AdoptDetailVO> boardList = adoptionBoardDao.selectFilterListWithPaging(pageFilterVO, boardType);
    int totalCount = adoptionBoardDao.countFilter(pageFilterVO, boardType);

    model.addAttribute("boardList", boardList);
    pageFilterVO.setDataCount(totalCount);
    model.addAttribute("pageVO", pageFilterVO);
    return "/WEB-INF/views/board/adoption/list.jsp";
}
```

### Query

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

이 기준을 쿼리에서 먼저 계산해두니 리스트에서 모집중으로 보이는데 상세에 들어가면 이미 끝난 글처럼 보이는 어색함을 줄일 수 있었습니다.

---

## 상세 버튼 조건

<table>
<tr>
<td width="48%"><img src="screenshots/02_adoption_detail_open.png" width="100%" alt="분양 상세 화면"></td>
<td width="48%"><img src="screenshots/09_adoption_detail_review.png" width="100%" alt="후기 버튼 화면"></td>
</tr>
</table>

분양 상세에서는 신청 / 취소 / 승인 / 거절 / 완료 / 후기 버튼까지 조건이 많았습니다. 이걸 JSP 안에서 전부 계산하게 두면 화면이 금방 복잡해질 것 같아서, 컨트롤러에서 먼저 상태와 권한을 정리해 모델에 담는 방식으로 맞췄습니다.

연결 기준
- `AdoptionBoardController.detail()`에서 상태와 권한 계산
- JSP는 계산보다 표시 쪽에 집중
- 후기 작성 가능 여부도 같은 곳에서 함께 정리

### Controller

```java
AdoptionApplyVO approvedApply = adoptionApplyDao.selectApprovedByBoardNo(boardNo);
AdoptionApplyVO completedApply = adoptionApplyDao.selectCompletedByBoardNo(boardNo);

String adoptionStage = "OPEN";
if ("f".equals(adoptDetailVO.getAnimalPermission())) adoptionStage = "COMPLETED";
else if (approvedApply != null) adoptionStage = "APPROVED";

boolean isOwner = loginId != null && loginId.equals(adoptDetailVO.getBoardWriter());
AdoptionApplyVO myApply = loginId != null
        ? adoptionApplyDao.selectLatestByBoardAndApplicant(boardNo, loginId)
        : null;

boolean canApply = loginId != null
        && !isOwner
        && !"COMPLETED".equals(adoptionStage)
        && approvedApply == null
        && (myApply == null
            || "REJECTED".equals(myApply.getApplyStatus())
            || "CANCELLED".equals(myApply.getApplyStatus()));
```

```java
boolean canWriteReview = loginId != null
        && "COMPLETED".equals(adoptionStage)
        && completedApply != null
        && loginId.equals(completedApply.getApplicantId())
        && reviewBoardNo == null;
```

상세 화면에서는 버튼을 숨기는 것보다, 왜 이 버튼이 보여야 하는지를 한 군데에 모아두는 일이 더 중요했습니다. 같은 글이라도 보는 사람이 작성자인지, 신청자인지, 완료된 글인지에 따라 화면이 달라졌기 때문입니다.

---

## 신청 승인과 중복 방지

<table>
<tr>
<td width="100%"><img src="screenshots/03_owner_manage_applicants.png" width="100%" alt="신청자 관리 화면"></td>
</tr>
</table>

작성자는 상세 화면에서 신청자 목록을 보고 승인 / 거절을 처리합니다. 이 구간은 버튼을 하나만 누르게 하는 것보다, 서버에서 한 번 더 막는 쪽이 더 중요했습니다. 분양은 결국 한 명만 승인돼야 했기 때문입니다.

연결 기준
- 신청 시 자기 글 신청, 완료 글 신청, 중복 신청을 먼저 차단
- 승인 시 작성자 권한과 현재 상태를 다시 확인
- 승인 성공 뒤 나머지 대기 신청은 한 번에 정리

### Service - apply

```java
@Transactional
public boolean apply(int boardNo, String applicantId, String applyContent) {
    AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(boardNo);
    if (detail == null) return false;
    if (applicantId == null) return false;
    if (applicantId.equals(detail.getBoardWriter())) return false;
    if ("f".equals(detail.getAnimalPermission())) return false;
    if (adoptionApplyDao.existsApprovedOrCompleted(boardNo)) return false;
    if (adoptionApplyDao.existsActiveByBoardAndApplicant(boardNo, applicantId)) return false;

    AdoptionApplyDto dto = AdoptionApplyDto.builder()
            .applyNo(adoptionApplyDao.sequence())
            .boardNo(boardNo)
            .animalNo(detail.getAnimalNo())
            .applicantId(applicantId)
            .applyContent(applyContent == null || applyContent.isBlank() ? "(신청 내용 없음)" : applyContent.trim())
            .build();
    adoptionApplyDao.insert(dto);
    return true;
}
```

### Service - approve

```java
@Transactional
public boolean approve(int applyNo, String ownerId) {
    AdoptionApplyDto dto = adoptionApplyDao.selectOne(applyNo);
    AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(dto.getBoardNo());
    if (!ownerId.equals(detail.getBoardWriter())) return false;
    if ("f".equals(detail.getAnimalPermission())) return false;
    if (adoptionApplyDao.existsApprovedOrCompleted(dto.getBoardNo())) return false;

    boolean ok = adoptionApplyDao.approve(applyNo);
    if (!ok) return false;

    adoptionApplyDao.rejectOthersApplied(dto.getBoardNo(), applyNo);
    return true;
}
```

### Query

```sql
update adoption_apply
set apply_status = 'APPROVED', apply_etime = systimestamp
where apply_no = ? and apply_status = 'APPLIED'
```

```sql
update adoption_apply
set apply_status = 'REJECTED', apply_etime = systimestamp
where board_no = ? and apply_status = 'APPLIED' and apply_no <> ?
```

분양 기능에서 제가 중요하게 본 건 승인 버튼이 있는가가 아니라, 결국 승인자는 한 명만 남는가였습니다. 이 기준이 흔들리면 뒤쪽 완료 처리와 후기 연결까지 전부 같이 흔들릴 수 있었습니다.

---

## 완료 처리와 신청 내역

<table>
<tr>
<td width="48%"><img src="screenshots/04_adoption_completed.png" width="100%" alt="분양 완료 화면"></td>
<td width="48%"><img src="screenshots/05_mypage_applies.png" width="100%" alt="마이페이지 신청 내역"></td>
</tr>
</table>

완료 처리 뒤에는 화면 상태만 끝난 것으로 보이면 안 됐습니다.

- 신청 상태가 COMPLETED로 바뀌고
- 실제 동물 소유자가 바뀌고
- 분양 종료 permission도 같이 바뀌어야 했습니다.

### Controller

```java
@PostMapping("/completeAdoption")
public String completeAdoption(@RequestParam int boardNo, HttpSession session) {
    String loginId = (String) session.getAttribute("loginId");
    if (loginId == null) return "redirect:/member/login";

    boolean ok = adoptionProcessService.complete(boardNo, loginId);
    return "redirect:detail?boardNo=" + boardNo + (ok ? "&complete=ok" : "&complete=fail");
}
```

### Service

```java
@Transactional
public boolean complete(int boardNo, String ownerId) {
    AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(boardNo);
    if (detail == null) return false;
    if (!ownerId.equals(detail.getBoardWriter())) return false;
    if ("f".equals(detail.getAnimalPermission())) return false;

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

### DAO / Query

```java
public boolean updateMaster(int animalNo, String animalMaster) {
    String sql = "update animal set animal_master = ? where animal_no = ?";
    return jdbcTemplate.update(sql, animalMaster, animalNo) > 0;
}
```

```java
public int updatePermissionToF(int boardNo) {
    String sql =
            "update animal a set a.animal_permission = 'f' " +
            "where a.animal_no = (select ba.animal_no from board_animal ba where ba.board_no = ?)";
    return jdbcTemplate.update(sql, boardNo);
}
```

저는 완료를 상태 문자열 하나 바꾸는 버튼으로 두고 싶지 않았습니다. 실제 소유자 변경까지 이어져야 분양이 정말 끝났다고 볼 수 있다고 생각했습니다.

---

## 후기와 알림 연결

<table>
<tr>
<td width="48%"><img src="screenshots/10_review_write.png" width="100%" alt="후기 작성 화면"></td>
<td width="48%"><img src="screenshots/11_mypage_notifications.png" width="100%" alt="알림 화면"></td>
</tr>
</table>

분양이 끝난 뒤에는 후기 작성과 알림까지 이어져야 사용자가 다음 행동을 찾기 쉬웠습니다.

- 완료된 신청자만 후기 버튼 노출
- 이미 연결된 후기면 중복 작성 방지
- 신청 / 승인 / 거절 / 완료 시 알림 발송

### Controller - review

```java
@GetMapping("/write")
public String writeForm(Model model, HttpSession session,
        @RequestParam(required = false) Integer adoptionBoardNo) {
    String loginId = (String) session.getAttribute("loginId");

    Integer linkedReviewNo = adoptionReviewLinkDao.findReviewBoardNo(adoptionBoardNo);
    if (linkedReviewNo != null) {
        return "redirect:detail?boardNo=" + linkedReviewNo;
    }

    AdoptionApplyVO completedApply = adoptionApplyDao.selectCompletedByBoardNo(adoptionBoardNo);
    if (completedApply == null || !loginId.equals(completedApply.getApplicantId())) {
        throw new NeedPermissionException();
    }
}
```

### DAO

```java
public Integer findReviewBoardNo(int adoptionBoardNo) {
    String sql = "select review_board_no from adoption_review_link where adoption_board_no = ?";
    List<Integer> list = jdbcTemplate.query(sql, (rs, rn) -> rs.getInt("review_board_no"), adoptionBoardNo);
    return list.isEmpty() ? null : list.get(0);
}
```

### Service

```java
public void notify(String memberId, String type, String message, String url) {
    if (memberId == null || memberId.isBlank()) return;
    if (message == null || message.isBlank()) return;

    NotificationDto dto = NotificationDto.builder()
            .notiNo(notificationDao.sequence())
            .memberId(memberId)
            .notiType(type == null ? "INFO" : type)
            .notiMessage(message.trim())
            .notiUrl(url != null && url.trim().startsWith("/") ? url.trim() : null)
            .build();

    notificationDao.insert(dto);
}
```

후기와 알림 기능은 얼핏 보면 분양이 끝난 뒤에 추가된 부가적인 요소처럼 느껴질 수 있습니다. 하지만 제가 바라본 관점에서는, 분양이 마무리된 이후 사용자가 마지막으로 어디로 이어지는지 보여주는 중요한 연결 고리라고 생각했습니다.

---

## 트러블슈팅

### 리스트와 상세 화면의 상태 불일치

- 문제: 리스트에서는 '모집중'으로 보이는데, 상세 화면에서는 이미 '진행중'이나 '완료'로 표시되는 경우가 있었습니다.
- 원인: 상태 계산을 한 쪽 화면에서만 처리하다 보니 기준이 서로 달라질 수밖에 없었습니다.
- 해결: 리스트 조회 쿼리 단계에서도 `adoption_stage` 값을 함께 계산하도록 했고, 상세 화면 역시 동일한 기준으로 상태를 나누어 일관성을 맞췄습니다.

### 승인자가 여러 명이 생길 위험

- 문제: 화면상으로는 승인 버튼이 하나만 보이지만, 서버 측에서 별도의 검증 없이 처리할 경우 예외 상황이 발생할 수 있었습니다.
- 원인: UI에만 의존하다 보면 동시에 여러 요청이 들어왔을 때 승인자가 둘 이상 될 가능성이 있었습니다.
- 해결: `approve()` 함수에서 `existsApprovedOrCompleted()`를 먼저 확인하도록 하고, 실제 업데이트 시에도 `apply_status = 'APPLIED'` 조건을 추가하여 한 번 상태가 바뀐 지원자는 다시 승인되지 않도록 했습니다.

### 완료 처리 후에도 동물 소유자 정보가 그대로 남는 문제

- 문제: 신청 상태만 변경되고, 실제 동물 소유자 정보가 바뀌지 않으면서 화면에 보이는 내용과 데이터베이스 정보가 어긋나는 현상이 나타날 수 있었습니다.
- 원인: 완료 처리를 상태값 변경만으로 끝냈기 때문입니다.
- 해결: `completeApproved()` 이후에 동물의 소유자를 갱신하는 `animalDao.updateMaster()`와 권한 값을 조정하는 `updatePermissionToF()`까지 같은 트랜잭션에서 처리하도록 수정했습니다.

### 후기 버튼 숨김만으로는 권한 제어가 충분하지 않음

- 문제: UI에서 버튼을 숨긴다고 해도, 직접 URL로 접근하면 후기를 작성할 수 있는 상황이 생길 수 있습니다.
- 원인: 화면 노출과 서버 권한 검증을 동일시한 점이 원인이었습니다.
- 해결: 상세 화면에는 `canWriteReview` 값으로 버튼 노출을 제어하고, 실제 후기를 작성하는 `ReviewController.writeForm()`과 `write()` 단계에서는 신청자 상태와 기존 후기 여부를 재확인하여 권한을 두 번 검증하도록 했습니다.

---

## 확인한 시나리오

- 리스트와 상세 화면 모두 일관된 상태 정보를 보여주는지
- 글 작성자가 직접 자기 글에 신청할 수 없는지
- 한 게시글에서 승인자가 동시에 둘 이상 발생하지 않는지
- 신청 취소는 `APPLIED` 상태에서만 정상적으로 되는지
- 완료 이후에는 동물 소유자 정보와 permission 값이 함께 변경되는지
- 완료된 신청자만 후기를 쓸 수 있도록 버튼이 노출되는지
- 신청, 승인, 거절, 완료 단계별로 알림이 정확한 대상에게 전달되는지

---

## 정리하며

분양 기능은 처음에는 단순히 게시판의 기능을 조금 확장하는 작업처럼 여겨졌습니다. 하지만 실제로 구현해보니, 각 단계의 상태와 권한을 처음부터 끝까지 일관되게 관리하는 일이 무엇보다 중요하다는 점을 깨달았습니다. 이번 파트에서는 화면을 무턱대고 늘리는 대신, 신청부터 승인, 완료, 그리고 후기 작성까지 모든 플로우를 같은 기준 아래 촘촘하게 이어가는 데 집중했습니다. 그 과정에서 사용자의 경험은 물론, 데이터 신뢰성까지 함께 지킬 수 있었습니다.