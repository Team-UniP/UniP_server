package unip.universityInParty.domain.oauth.refresh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unip.universityInParty.domain.oauth.refresh.entity.Refresh;
import unip.universityInParty.domain.oauth.refresh.repository.RefreshRepository;
import unip.universityInParty.domain.oauth.utils.ResponseUtil;
import unip.universityInParty.domain.oauth.utils.jwt.JwtTokenProvider;
import unip.universityInParty.domain.member.entity.Member;
import unip.universityInParty.global.exception.custom.CustomException;
import unip.universityInParty.global.exception.errorCode.OAuthErrorCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RefreshService {
    private final RefreshRepository refreshRepository; // RedisRepository 사용
    private final JwtTokenProvider jwtTokenProvider;

    // Refresh Token 신규 발급
    public Map<String, String> refreshAccessToken(String refreshToken) {

        // 토큰 유효성 검사
        jwtTokenProvider.validateToken(refreshToken);

        // Redis에 저장된 리프레시 토큰과 비교
        Member member = jwtTokenProvider.getMember(refreshToken);
        Refresh refresh = refreshRepository.findById(member.getId())
            .orElseThrow(() -> new CustomException(OAuthErrorCode.INVALID_REFRESH_TOKEN));

        if (!refresh.getToken().equals(refreshToken)) {
            throw new CustomException(OAuthErrorCode.INVALID_REFRESH_TOKEN);
        }


        // 새 토큰 생성 및 갱신
        String newAccess = jwtTokenProvider.createAccessToken(member.getId(), member.getRoles());
        String newRefresh = jwtTokenProvider.createRefreshToken(member.getId(), member.getRoles());

        addRefresh(member.getId(), newRefresh);

        return ResponseUtil.createTokenMap(newAccess, newRefresh, member.isAuth());
    }
    // Refresh 객체를 Redis에 추가
    @Transactional
    public void addRefresh(Long id, String token) {
        deleteById(id);
        Refresh refresh = Refresh.builder()
            .id(id)
            .token(token)
            .build();
        refreshRepository.save(refresh);
    }
    // 모든 Refresh 객체 조회
    public List<Refresh> get() {
        List<Refresh> refreshList = new ArrayList<>();
        refreshRepository.findAll().forEach(refreshList::add); // 모든 Refresh 객체 추가
        return refreshList;
    }

    @Transactional
    public void deleteById(Long memberId) {
        refreshRepository.deleteById(memberId);
    }
}