package takehomeassignments.aspire.mockaspireloanapplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import takehomeassignments.aspire.mockaspireloanapplication.entities.InstallmentEntity;
import takehomeassignments.aspire.mockaspireloanapplication.enums.InstallmentStatus;

import java.util.List;

public interface InstallmentRepository extends JpaRepository<InstallmentEntity, String> {

    List<InstallmentEntity> findByLoanId(String loanId);


    @Modifying
    @Query("update InstallmentEntity i set i.status = ?1 where i.loan.id = ?2")
    int markInstallmentsAsStatus( InstallmentStatus status, String loanId);

    @Query("select i from InstallmentEntity i where i.loan.id = ?1 and i.installmentNumber = ?2")
    InstallmentEntity findByLoanIdAndInstallmentNumber(String id, int i);

    @Query("SELECT MIN(i.installmentNumber) from InstallmentEntity i where i.loan.id = ?1 and i.status = 'PENDING'")
    int findNextInstallmentByLoanId(String id);

    @Query("SELECT i.dueAmount from InstallmentEntity i where i.loan.id = ?1 and i.installmentNumber = ?2")
    float getDueAmountForInstallment(String loanId, int installmentNumber);

    @Modifying
    @Query("update InstallmentEntity i set i.dueAmount = ?2 where i.loan.id = ?1 and i.status = 'PENDING'")
    void markPendingInstallmentsWithDueAmount(String loanId, float dueAmount);
}
