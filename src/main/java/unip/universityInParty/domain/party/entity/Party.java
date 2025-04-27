package unip.universityInParty.domain.party.entity;

import jakarta.persistence.*;
import lombok.*;
import unip.universityInParty.domain.member.entity.Member;
import unip.universityInParty.domain.party.dto.request.PartyDto;
import unip.universityInParty.domain.party.entity.type.PartyType;
import unip.universityInParty.global.exception.custom.CustomException;


import java.time.LocalDateTime;

import unip.universityInParty.domain.party.exception.PartyErrorCode;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    indexes = {
        @Index(name = "idx_party_cursor", columnList = " isClosed, partyType, id")
    }
)
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String content;

    private int partyLimit;

    private int peopleCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private PartyType partyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean isClosed;

    public boolean isPartyFull() {
        return peopleCount >= partyLimit;
    }

    public void joinParty() {
        if (isPartyFull()) {
            throw new CustomException(PartyErrorCode.PARTY_FULL);
        }
        peopleCount++;
    }

    public void leaveParty() {
        if (peopleCount <= 0) {
            throw new CustomException(PartyErrorCode.NO_MEMBER_TO_LEAVE);
        }
        peopleCount--;
    }

    public static Party createParty(PartyDto partyDto, Member member) {
        return Party.builder()
            .title(partyDto.title())
            .content(partyDto.content())
            .partyType(partyDto.partyType())
            .partyLimit(partyDto.limit())
            .peopleCount(0)
            .startTime(partyDto.startTime())
            .endTime(partyDto.endTime())
            .member(member)
            .isClosed(false)
            .build();
    }

    // 엔티티 업데이트 메서드
    public void updateParty(PartyDto partyDto) {
        this.title = partyDto.title();
        this.content = partyDto.content();
        this.partyLimit = partyDto.limit();
        this.startTime = partyDto.startTime();
        this.partyType = partyDto.partyType();
        this.endTime = partyDto.endTime();
    }
}
