package unip.universityInParty.domain.refresh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unip.universityInParty.domain.refresh.entity.Refresh;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    Boolean existsByRefresh(String refresh);
    Boolean existsByUsername(String username);
    void deleteByRefresh(String refresh);
    void deleteByUsername(String username);
}