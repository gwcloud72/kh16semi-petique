package com.spring.semi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.semi.dao.AdoptionApplyDao;
import com.spring.semi.dao.AdoptionBoardDao;
import com.spring.semi.dao.AnimalDao;
import com.spring.semi.dto.AdoptionApplyDto;
import com.spring.semi.vo.AdoptionApplyVO;
import com.spring.semi.vo.AdoptDetailVO;


/**
 * AdoptionProcessService - 비즈니스 로직을 담당하는 서비스.
 */
@Service
public class AdoptionProcessService {

    @Autowired
    private AdoptionApplyDao adoptionApplyDao;

    @Autowired
    private AdoptionBoardDao adoptionBoardDao;

	@Autowired
	private AnimalDao animalDao;

	@Autowired
	private NotificationService notificationService;

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

		String url = "/board/adoption/detail?boardNo=" + boardNo;
		notificationService.notify(detail.getBoardWriter(), "ADOPTION_APPLY",
				"'" + detail.getAnimalName() + "' 분양글에 새로운 신청이 도착했어요.", url);
        return true;
    }

    @Transactional
    public boolean cancel(int applyNo, String applicantId) {
        if (applicantId == null) return false;
		AdoptionApplyDto dto = adoptionApplyDao.selectOne(applyNo);
		boolean ok = adoptionApplyDao.cancel(applyNo, applicantId);
		if (ok && dto != null) {
			AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(dto.getBoardNo());
			if (detail != null) {
				String url = "/board/adoption/detail?boardNo=" + dto.getBoardNo();
				notificationService.notify(detail.getBoardWriter(), "ADOPTION_CANCEL",
						"'" + detail.getAnimalName() + "' 분양글의 신청이 취소되었습니다.", url);
			}
		}
		return ok;
    }

    @Transactional
    public boolean reject(int applyNo, String ownerId) {
        if (ownerId == null) return false;

        AdoptionApplyDto dto = adoptionApplyDao.selectOne(applyNo);
        if (dto == null) return false;

        AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(dto.getBoardNo());
        if (detail == null) return false;
        if (!ownerId.equals(detail.getBoardWriter())) return false;
        if ("f".equals(detail.getAnimalPermission())) return false;
		boolean ok = adoptionApplyDao.reject(applyNo);
		if (ok) {
			String url = "/board/adoption/detail?boardNo=" + dto.getBoardNo();
			notificationService.notify(dto.getApplicantId(), "ADOPTION_REJECT",
					"분양 신청이 거절되었습니다.", url);
		}
		return ok;
    }

    @Transactional
    public boolean approve(int applyNo, String ownerId) {
        if (ownerId == null) return false;

        AdoptionApplyDto dto = adoptionApplyDao.selectOne(applyNo);
        if (dto == null) return false;

        AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(dto.getBoardNo());
        if (detail == null) return false;
        if (!ownerId.equals(detail.getBoardWriter())) return false;
        if ("f".equals(detail.getAnimalPermission())) return false;
        if (adoptionApplyDao.existsApprovedOrCompleted(dto.getBoardNo())) return false;

        boolean ok = adoptionApplyDao.approve(applyNo);
        if (!ok) return false;

        adoptionApplyDao.rejectOthersApplied(dto.getBoardNo(), applyNo);

		String url = "/board/adoption/detail?boardNo=" + dto.getBoardNo();
		notificationService.notify(dto.getApplicantId(), "ADOPTION_APPROVE",
				"분양 신청이 승인되었습니다. 작성자가 완료 처리하면 분양이 종료됩니다.", url);
        return true;
    }

    @Transactional
    public boolean complete(int boardNo, String ownerId) {
        if (ownerId == null) return false;

        AdoptDetailVO detail = adoptionBoardDao.selectAdoptDetail(boardNo);
        if (detail == null) return false;
        if (!ownerId.equals(detail.getBoardWriter())) return false;
        if ("f".equals(detail.getAnimalPermission())) return false;

        AdoptionApplyVO approved = adoptionApplyDao.selectApprovedByBoardNo(boardNo);
        if (approved == null) return false;

        if (!adoptionApplyDao.completeApproved(boardNo)) {
            return false;
        }

        if (!animalDao.updateMaster(detail.getAnimalNo(), approved.getApplicantId())) {
            throw new IllegalStateException("동물 소유자 변경 실패");
        }

        int updated = adoptionBoardDao.updatePermissionToF(boardNo);
        if (updated <= 0) {
            throw new IllegalStateException("분양 완료 상태 반영 실패");
        }

        String url = "/board/adoption/detail?boardNo=" + boardNo;
        notificationService.notify(approved.getApplicantId(), "ADOPTION_COMPLETE",
                "분양이 완료 처리되었습니다. 후기 작성도 가능해요.", url);

        return true;
    }
}
