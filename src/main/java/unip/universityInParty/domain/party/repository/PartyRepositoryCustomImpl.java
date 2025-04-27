package unip.universityInParty.domain.party.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import unip.universityInParty.domain.course.dto.CourseDto;
import unip.universityInParty.domain.party.dto.response.PartyDetailDto;
import unip.universityInParty.domain.party.dto.response.PartyDetailsResponseDto;
import unip.universityInParty.domain.party.dto.response.PartyMyDto;
import unip.universityInParty.domain.party.dto.response.PartyResponseDto;
import unip.universityInParty.domain.party.entity.type.PartyType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static unip.universityInParty.domain.course.entity.QCourse.course;
import static unip.universityInParty.domain.member.entity.QMember.member;
import static unip.universityInParty.domain.party.entity.QParty.party;

@Repository
@RequiredArgsConstructor
public class PartyRepositoryCustomImpl implements PartyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /*
    @Override
    public List<PartyResponseDto> getMainPartyPage(PartyType partyType) {
        BooleanBuilder conditions = createMainPartyConditions(partyType);

        return queryFactory
            .select(Projections.constructor(PartyResponseDto.class,
                party.id,
                member.name,
                member.profileImage,
                party.title,
                party.partyType,
                party.partyLimit,
                party.peopleCount,
                party.startTime,
                party.endTime
            ))
            .from(party)
            .join(party.member, member)
            .where(conditions)
            .fetch();
    }
     */

    @Override
    public PartyDetailsResponseDto findPartyDetailById(Long id) {
        PartyDetailDto partyDetailDto = queryFactory
            .select(Projections.constructor(PartyDetailDto.class,
                party.id,
                party.title,
                party.content,
                party.partyLimit,
                party.peopleCount,
                party.startTime,
                party.endTime
            ))
            .from(party)
            .where(party.id.eq(id))
            .fetchOne();

        List<CourseDto> courses = queryFactory
            .select(Projections.constructor(CourseDto.class,
                course.address,
                course.name,
                course.content
            ))
            .from(course)
            .where(course.party.id.eq(id))
            .fetch();

        return PartyDetailsResponseDto.createPartyDto(Objects.requireNonNull(partyDetailDto), courses);
    }


    @Override
    public List<PartyMyDto> getMyParty(Long id) {
        return queryFactory
            .select(Projections.constructor(PartyMyDto.class,
                party.id,
                party.title,
                party.partyLimit,
                party.peopleCount
            ))
            .from(party)
            .where(party.member.id.eq(id))
            .fetch();
    }


    @Override
    public List<PartyResponseDto> getPartyPage(PartyType partyType, Long lastId, int size) {
        BooleanBuilder conditions = createMainPartyConditions(partyType, lastId);
        return queryFactory
            .select(Projections.constructor(PartyResponseDto.class,
                party.id,
                member.name,
                member.profileImage,
                party.title,
                party.partyType,
                party.partyLimit,
                party.peopleCount,
                party.startTime,
                party.endTime
            ))
            .from(party)
            .join(party.member, member)
            .where(conditions)
            .orderBy(party.id.desc()) //커서 페이징은 안정적이고 예측가능한 페이징을 위해 정렬을 추가
            .limit(size)
            .fetch();
    }

    private BooleanBuilder createMainPartyConditions(PartyType partyType,  Long lastId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(party.isClosed.isFalse()); // 종료되지 않은 파티만 조회

        if (partyType != null) {
            builder.and(party.partyType.eq(partyType)); // 파티 타입 조건 추가
        }

        builder.and(lastId != null ? party.id.gt(lastId) : party.id.gt(0)); // lastId 조건 추가

        return builder;
    }
}
