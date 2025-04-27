package unip.universityInParty.domain.pmList.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unip.universityInParty.domain.member.entity.Member;
import unip.universityInParty.domain.member.repository.MemberRepository;
import unip.universityInParty.domain.member.service.MemberService;
import unip.universityInParty.domain.party.entity.Party;
import unip.universityInParty.domain.party.repository.PartyRepository;
import unip.universityInParty.domain.party.service.PartyService;
import unip.universityInParty.domain.pmList.entity.Enum.PartyRole;
import unip.universityInParty.domain.pmList.entity.PMList;
import unip.universityInParty.domain.pmList.repository.PMListRepository;
import unip.universityInParty.global.exception.custom.CustomException;
import unip.universityInParty.domain.member.exception.MemberErrorCode;
import unip.universityInParty.domain.party.exception.PartyErrorCode;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PMListService {
    private final PMListRepository pmListRepository;
    private final MemberService memberService;
    private final PartyService partyService;
    // 주어진 파티 ID에 속한 멤버 ID 리스트를 조회합니다.
    public List<Long> getMemberIdsByPartyId(Long partyId) {
        if (!partyService.existsById(partyId)) {
            throw new CustomException(PartyErrorCode.PARTY_NOT_FOUND);
        }

        // 파티가 존재할 경우 멤버 ID 리스트 조회
        return pmListRepository.findMemberIdsByPartyId(partyId);
    }

    // 파티에 멤버를 추가하고, 멤버의 역할을 설정합니다.
    @Transactional
    public void createJoinParty(PartyRole partyRole, Long memberId, Long partyId) {
        Party party = partyService.findByIdPessimisticLock(partyId);
        Member member = memberService.findById(memberId);

        if (pmListRepository.existsByPartyAndMember(party, member)) {
            throw new CustomException(PartyErrorCode.ALREADY_JOINED);
        }

        party.joinParty(); // 파티 인원 수 증가
        PMList pmList = PMList.builder()
            .party(party)
            .member(member)
            .role(partyRole)
            .build();
        pmListRepository.save(pmList); // 파티 멤버 리스트에 저장
    }

    // 특정 멤버를 파티에서 삭제합니다.
    @Transactional
    public void deletePartyMember(Long memberId, Long partyId) {
        Party party = partyService.findByIdPessimisticLock(partyId);
        Member member = memberService.findById(memberId);


        if (!pmListRepository.existsByPartyAndMember(party, member)) {
            throw new CustomException(PartyErrorCode.MEMBER_NOT_IN_PARTY);
        }

        party.leaveParty(); // 파티 인원 수 감소
        pmListRepository.deleteByPartyAndMember(party, member); // 멤버 삭제
    }

}