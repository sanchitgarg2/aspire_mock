package takehomeassignments.aspire.mockaspireloanapplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import takehomeassignments.aspire.mockaspireloanapplication.entities.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String>{
    Optional<UserEntity> findOneById(String id);

    @Query("SELECT email FROM UserEntity WHERE id = ?1")
    String getEmailByUserId(String userId);

    @Query("SELECT phoneNumber FROM UserEntity WHERE id = ?1")
    String getPhoneNumberByUserId(String userId);

}
