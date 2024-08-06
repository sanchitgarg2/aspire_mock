package takehomeassignments.aspire.mockaspireloanapplication.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import takehomeassignments.aspire.mockaspireloanapplication.entities.AdminUser;
import takehomeassignments.aspire.mockaspireloanapplication.entities.InstallmentEntity;
import takehomeassignments.aspire.mockaspireloanapplication.entities.LoanEntity;
import takehomeassignments.aspire.mockaspireloanapplication.entities.UserEntity;
import takehomeassignments.aspire.mockaspireloanapplication.enums.InstallmentStatus;
import takehomeassignments.aspire.mockaspireloanapplication.enums.LoanStatus;
import takehomeassignments.aspire.mockaspireloanapplication.enums.NotificationChannel;
import takehomeassignments.aspire.mockaspireloanapplication.enums.PrivilegeEnum;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.AdminUserRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.InstallmentRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.LoanRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.UserRepository;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {

    LoanRepository loanRepository;

    UserRepository userRepository;
    NotificationsService notificationsService;
    private InstallmentRepository installmentRepo;

    private final AdminUserRepository adminUserRepository;

    public Page<LoanEntity> getPendingLoans(Pageable pageable, String adminUserId){
        float amountThreshold = adminUserRepository.findByUserId(adminUserId).orElseThrow().getApprovalLimit();
        return loanRepository.findAllByStatusAndBelowAmount(pageable, LoanStatus.PENDING_APPROVAL, amountThreshold);
    }

    @Transactional
    public boolean approveLoan(String loanId, String adminUserId) {
        //Approve the loan

        //The timestamp to use across the method for consistency
        ZonedDateTime now = ZonedDateTime.now();

        LoanEntity loan = loanRepository.findById(loanId).orElseThrow();
        AdminUser adminUser = adminUserRepository.findByUserId(adminUserId).orElseThrow();
        if(!adminUser.hasPrivilege(PrivilegeEnum.CAN_APPROVE_LOANS)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have the privilege to approve loans");
        }
        if(isLoanAmountAboveThreshold(loan, adminUser)) {
            //Notify the admin that the loan amount is above their approval limit
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan amount is above your approval limit");
        }
        loan.setStatus(LoanStatus.APPROVED);
        loan.setReviewedBy(adminUser);

        loan.setReviewedAt(now);

        //The loan is disbursed only when it is approved. The installments are only created when the loan is disbursed
        if(now.isAfter(loan.getStartDate())) {
            //log warning if the loan is approved after the start date
            log.warn("Loan approved after the start date for loan with id "+loanId);

            //The startDate Of the Installments is adjusted to the current date
            List<InstallmentEntity> installments = installmentRepo.findByLoanId(loanId);
            for (InstallmentEntity installment : installments) {
                int i = installment.getInstallmentNumber();
                installment.setDueAt(
                                switch (loan.getRepaymentFrequency()){
                                    case MONTHLY -> now.plusMonths(i);
                                    case WEEKLY -> now.plusWeeks(i);
                                    case DAILY -> now.plusDays(i);
                                    case QUARTERLY -> now.plusMonths(i* 3L);
                                    case YEARLY -> now.plusYears(i);
                                }
                );
                installment.setStatus(InstallmentStatus.PENDING);
                installmentRepo.save(installment);
            }
            loan.setStartDate(now);
        }
        else{
            //Mark all the installments as pending
            installmentRepo.markInstallmentsAsStatus(InstallmentStatus.PENDING, loanId);
        }
        loanRepository.save(loan);

        UserEntity user = userRepository.findOneById(loan.getUser().getId()).orElseThrow();
        //Notify the user that the loan has been approved
        notificationsService.sendNotification(String.format("Your loan with id %s, of amount %s has been approved" , loan.getId(), loan.getAmount()), user.getId(), NotificationChannel.TEXT_MESSAGE);
        return true;
    }

    @Transactional
    public boolean rejectLoan(String loanId, String adminUserId) {
        AdminUser adminUser = adminUserRepository.findByUserId(adminUserId).orElseThrow();
        LoanEntity loan = loanRepository.findById(loanId).orElseThrow();
        if(isLoanAmountAboveThreshold(loan, adminUser)) {
            //Notify the admin that the loan amount is above their approval limit
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan amount is above your approval limit");
        }
        //Reject the loan
        loan.setStatus(LoanStatus.REJECTED);
        loan.setReviewedBy(adminUser);
        loan.setReviewedAt(ZonedDateTime.now());
        loanRepository.save(loan);

        installmentRepo.markInstallmentsAsStatus(InstallmentStatus.REJECTED, loanId);

        UserEntity user = userRepository.findOneById(loan.getUser().getId()).orElseThrow();
        //Notify the user that the loan has been rejected
        notificationsService.sendNotification(String.format("Your loan with id %s, of amount %s has been rejected" , loan.getId(), loan.getAmount()), user.getId(), NotificationChannel.TEXT_MESSAGE);
        return true;
    }

    private boolean isLoanAmountAboveThreshold(LoanEntity loan, AdminUser adminUser) {
        return loan.getAmount() > adminUser.getApprovalLimit();
    }
}
