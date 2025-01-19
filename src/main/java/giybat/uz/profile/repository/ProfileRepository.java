package giybat.uz.profile.repository;


import giybat.uz.profile.entity.ProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer>, PagingAndSortingRepository<ProfileEntity, Integer> {



    @Query("FROM ProfileEntity p WHERE p.visible = true ")
    Page<ProfileEntity> findAllPage(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity p SET p.visible = false WHERE p.id=?1 ")
    int deleted(Integer id);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM ProfileEntity a WHERE a.username = ?1 AND a.status = 'IN_REGISTRATION'")
    Boolean existsByUsername(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer id);

    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);



    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity s SET s.status = 'ACTIVE' WHERE s.username=?1 ")
    Integer updateStatus(String username);

}
