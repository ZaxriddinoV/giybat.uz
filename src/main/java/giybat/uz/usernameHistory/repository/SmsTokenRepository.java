package giybat.uz.usernameHistory.repository;

import giybat.uz.usernameHistory.entiy.SmsTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsTokenRepository extends JpaRepository<SmsTokenEntity,Integer> {
}
