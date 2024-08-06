package takehomeassignments.aspire.mockaspireloanapplication.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import takehomeassignments.aspire.mockaspireloanapplication.entities.LoanEntity;
import takehomeassignments.aspire.mockaspireloanapplication.enums.LoanStatus;

import java.util.List;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    @Query("SELECT l FROM LoanEntity l WHERE l.status = ?1 AND l.amount < ?2")
    Page<LoanEntity> findAllByStatusAndBelowAmount(Pageable pageable, LoanStatus status, float amountThreshold);



    //This is technically not the right sum to be looking at, we should deduct any payments already made by the user
    //Since we allow for higher than due payment of loans, we would have to sum all the installments paid by the user
    //For now, doing a simple implementation
    @Query("SELECT SUM(amount) FROM LoanEntity WHERE user.id = ?1 and status IN ?2")
    Float getTotalLoansDisbursedToUser(String userId, List<LoanStatus> statuses);

    @Query("SELECT l FROM LoanEntity l WHERE l.user.id = ?1")
    List<LoanEntity> findAllByUserId(String userId);
}
