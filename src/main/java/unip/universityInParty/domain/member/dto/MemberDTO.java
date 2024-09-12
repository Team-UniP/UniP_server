package unip.universityInParty.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {
    private String role;
    private String username;
    private String name;
    private String profile_image;
    private boolean status;
}