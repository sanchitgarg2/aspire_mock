package takehomeassignments.aspire.mockaspireloanapplication.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import takehomeassignments.aspire.mockaspireloanapplication.api.requests.LoanApplicationRequest;
import takehomeassignments.aspire.mockaspireloanapplication.api.requests.PayNextInstallmentRequest;
import takehomeassignments.aspire.mockaspireloanapplication.entities.InstallmentEntity;
import takehomeassignments.aspire.mockaspireloanapplication.entities.LoanEntity;
import takehomeassignments.aspire.mockaspireloanapplication.entities.UserEntity;
import takehomeassignments.aspire.mockaspireloanapplication.enums.InstallmentStatus;
import takehomeassignments.aspire.mockaspireloanapplication.enums.LoanStatus;
import takehomeassignments.aspire.mockaspireloanapplication.exceptions.InsufficientBalanceException;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.InstallmentRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.LoanRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.UserRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepo;
    private LoanRepository loanRepo;
    private InstallmentRepository installmentRepo;

    public String getUserEmail(String userId) {
        return userRepo.getEmailByUserId(userId);
    }

    public String getUserPhoneNumber(String userId) {
        return userRepo.getPhoneNumberByUserId(userId);
    }

    public String createUser(UserEntity user) {
        user.setApprovedLimit(UserEntity.DEFAULT_APPROVED_LIMIT);
        user.setCurrentlyDefaulted(false);
        user.setDefaultedBefore(false);
        user.setId(UUID.randomUUID().toString().substring(0, 8));
        return userRepo.save(user).getId();
    }


    public String applyForLoan(LoanApplicationRequest loanRequest, String token) throws InsufficientBalanceException {
        if (userRepo.findByIdAndToken(loanRequest.getUserId(), token).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized Access")).isBefore(ZonedDateTime.now())) {
            throw new NotAcceptableStatusException("Token is expired please get a new token");
        }
        UserEntity user = userRepo.findById(loanRequest.getUserId()).orElseThrow();
        if (loanRequest.getTotalAmount() <= 0 || loanRequest.getInterestRate() < 0) {
            log.error("Invalid loan request");
            throw new NotAcceptableStatusException("Invalid loan request");
        }

        Float userBalance = loanRepo.getTotalLoansDisbursedToUser(user.getId(), LoanEntity.PENDING_STATUSES);
        userBalance = userBalance == null ? 0 : userBalance;
        if (loanRequest.getTotalAmount() + userBalance > user.getApprovedLimit()) {
            log.error("UserID " + user.getId() + " Requesting loan for more than he is eligible for");
            throw new InsufficientBalanceException("User is requesting loan for more than he is eligible for");
        }
        LoanEntity loan = LoanEntity.builder()
                .user(user)
                .amount(loanRequest.getTotalAmount())
                .status(LoanStatus.PENDING_APPROVAL)
                .interestRate(getMathematicalInterestRate(loanRequest))
                .startDate(loanRequest.getStartDate())
                .createdAt(ZonedDateTime.now())
                .repaymentFrequency(loanRequest.getFrequency())
                .numberOfInstallments(loanRequest.getNumberOfInstallments())
                .id(UUID.randomUUID().toString().substring(0, 8))
                .build();
        loan = loanRepo.save(loan);

        for (int i = 1; i < loanRequest.getNumberOfInstallments() + 1; i++) {
            InstallmentEntity installment = InstallmentEntity.builder()
                    .loan(loan)
                    .dueAmount(getEmiAmount(loanRequest))
                    .status(InstallmentStatus.PENDING_APPROVAL)
                    .dueAt(
                            switch (loanRequest.getFrequency()) {
                                case MONTHLY -> loanRequest.getStartDate().plusMonths(i);
                                case WEEKLY -> loanRequest.getStartDate().plusWeeks(i);
                                case DAILY -> loanRequest.getStartDate().plusDays(i);
                                case QUARTERLY -> loanRequest.getStartDate().plusMonths(i * 3L);
                                case YEARLY -> loanRequest.getStartDate().plusYears(i);
                            }
                    )
                    .installmentNumber(i)
                    .id(UUID.randomUUID().toString().substring(0, 8))
                    .build();
            installmentRepo.save(installment);
        }
        return loan.getId();
    }

    private Float getMathematicalInterestRate(LoanApplicationRequest loanRequest) {
        //Calculate interest rate based on the frequency
        return switch (loanRequest.getFrequency()) {
            case MONTHLY -> loanRequest.getInterestRate() / (12 * 100);
            case WEEKLY -> loanRequest.getInterestRate() / (52 * 100);
            case DAILY -> loanRequest.getInterestRate() / (365 * 100);
            case QUARTERLY -> loanRequest.getInterestRate() / (4 * 100);
            case YEARLY -> loanRequest.getInterestRate() / 100;
        };
    }

    private Float getEmiAmount(LoanApplicationRequest loanRequest) {
        //Formula to calculate EMIs
        //emi = [P x R x (1+R)^N]/[(1+R)^N-1]
        float interestRate = getMathematicalInterestRate(loanRequest);
        return (float) (loanRequest.getTotalAmount() * interestRate * Math.pow(1 + interestRate, loanRequest.getNumberOfInstallments())) / (float) (Math.pow(1 + interestRate, loanRequest.getNumberOfInstallments()) - 1);
//        return loanRequest.getTotalAmount() + (loanRequest.getTotalAmount() * loanRequest.getInterestRate());
    }

    @Transactional
    public void payNextInstallment(PayNextInstallmentRequest paymentRequest, String token) {
        if (userRepo.findByIdAndToken(paymentRequest.getUserId(), token).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized Access")).isBefore(ZonedDateTime.now())) {
            throw new NotAcceptableStatusException("Token is expired please get a new token");
        }
        UserEntity user = userRepo.findById(paymentRequest.getUserId()).orElseThrow();
        LoanEntity loan = loanRepo.findById(paymentRequest.getLoanId()).orElseThrow();
        if (!loan.getUser().getId().equals(user.getId())) {
            log.error("User " + user.getId() + " trying to add repayment details for loan " + paymentRequest.getLoanId());
            throw new NotAcceptableStatusException("User is not authorized to add repayment details for this loan");
        }
        if (loan.getStatus() != LoanStatus.APPROVED) {
            log.error("User " + user.getId() + " trying to add repayment details for unapproved loan " + paymentRequest.getLoanId());
            throw new NotAcceptableStatusException("User is not authorized to add repayment details for this loan");
        }
        int installmentNumber = installmentRepo.findNextInstallmentByLoanId(loan.getId());
        InstallmentEntity installment = installmentRepo.findByLoanIdAndInstallmentNumber(loan.getId(), installmentNumber);

        //This is a check to see if the installment is already paid - will be relevant at scale when user agents might replay requests
        if (installment.getStatus() == InstallmentStatus.PAID) {
            log.error("User " + user.getId() + " trying to pay for an already paid installment " + installment.getId());
            throw new NotAcceptableStatusException("User is trying to pay for an already paid installment");
        }

        if (
                Objects.equals(installment.getInstallmentNumber(), loan.getNumberOfInstallments())
                        && !Objects.equals(installment.getDueAmount(), paymentRequest.getAmount())
        ) {
            log.error("User " + user.getId() + " trying to pay amount different than the due amount for last installment " + installment.getId());
            throw new NotAcceptableStatusException("User is trying to pay different amount than the due amount for last installment");
        }
        installment.setStatus(InstallmentStatus.PAID);
        installment.setPaidAt(ZonedDateTime.now());
        installment.setPaidAmount(paymentRequest.getAmount());
        installmentRepo.save(installment);
        if (Objects.equals(installment.getInstallmentNumber(), loan.getNumberOfInstallments())
                && Objects.equals(installment.getDueAmount(), paymentRequest.getAmount())) {
            //Check if is last installment then amount should match exactly
            //Close loan
            loan.setStatus(LoanStatus.PAID);
            loanRepo.save(loan);
            return;
        }
        if (!Objects.equals(installment.getDueAmount(), paymentRequest.getAmount())) {
            float excessPayment = paymentRequest.getAmount() - installment.getDueAmount();
            this.adjustEmiAmount(excessPayment, installment, loan);
        }
        loanRepo.save(loan);
    }

    public List<LoanEntity> getLoans(String userId, String token) {
        if (userRepo.findByIdAndToken(userId, token).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized Access")).isBefore(ZonedDateTime.now())) {
            throw new NotAcceptableStatusException("Token is expired please get a new token");
        }
        return loanRepo.findAllByUserId(userId);
    }

    public String login(String userId) {
        //Assume that this method is called post Authentication and Authorization, so the token is always overridden
        UserEntity user = userRepo.findById(userId).orElseThrow();
        user.setToken(UUID.randomUUID().toString());
        user.setTokenExpiry(ZonedDateTime.now().plusHours(1));
        userRepo.save(user);
        return user.getToken();
    }

    //Using the term EMI here since it is colloquially the accepted one. Technically it should be called Installment Amount
    public void adjustEmiAmount(float excessPayment, InstallmentEntity installment, LoanEntity loan) {

        //Adjust the EMI amount -
        //Calculate the emi that it would take to borrow this extra amount today for the remaining period of the loan
        //Reduce all future EMIs by that amount
        LoanApplicationRequest loanRequest = LoanApplicationRequest.builder()
                .totalAmount(Math.abs(excessPayment))
                .interestRate(loan.getInterestRate())
                .startDate(null)
                .frequency(loan.getRepaymentFrequency())
                .numberOfInstallments(loan.getNumberOfInstallments() - installment.getInstallmentNumber())
                .build();
        float InstallmentAmountReducedBy = getEmiAmount(loanRequest) * (excessPayment < 0 ? -1 : 1);

        float regularInstallmentAmount = installmentRepo.getDueAmountForInstallment(loan.getId(), installment.getInstallmentNumber() + 1);
        if (InstallmentAmountReducedBy > 0) {
            //If paying too much, we check if it is more than the current value of the entire loan or if it is a full repayment
            if (InstallmentAmountReducedBy > regularInstallmentAmount) {
                throw new NotAcceptableStatusException("Too much Repayment cannot be accepted for installment " + installment.getId());
            } else if (InstallmentAmountReducedBy == regularInstallmentAmount) {
                //Loan is repaid
                installmentRepo.markInstallmentsAsStatus(InstallmentStatus.PAID, loan.getId());
                loan.setStatus(LoanStatus.PAID);
                return;
            }
        }
        installmentRepo.markPendingInstallmentsWithDueAmount(loan.getId(), regularInstallmentAmount - InstallmentAmountReducedBy);
    }
}
