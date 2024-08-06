package takehomeassignments.aspire.mockaspireloanapplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import takehomeassignments.aspire.mockaspireloanapplication.entities.AdminUser;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, String> {
    @Query("SELECT a FROM AdminUser a WHERE a.id = :adminUserId")
    Optional<AdminUser> findByUserId(String adminUserId);
}
