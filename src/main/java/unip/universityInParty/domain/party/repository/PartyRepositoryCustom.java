package unip.universityInParty.domain.party.repository;

import unip.universityInParty.domain.party.dto.response.PartyDetailDto;
import unip.universityInParty.domain.party.dto.response.PartyDetailsResponseDto;
import unip.universityInParty.domain.party.dto.response.PartyMyDto;
import unip.universityInParty.domain.party.dto.response.PartyResponseDto;
import unip.universityInParty.domain.party.entity.type.PartyType;

import java.util.List;
import java.util.Optional;

public interface PartyRepositoryCustom {
    /*
    List<PartyResponseDto> getMainPartyPage(PartyType partyType);
    */
    PartyDetailsResponseDto findPartyDetailById(Long id);

    List<PartyMyDto> getMyParty(Long id);

    List<PartyResponseDto> getPartyPage(PartyType partyType, Long lastId, int size);

}
