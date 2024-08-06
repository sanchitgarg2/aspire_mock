package services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.NotAcceptableStatusException;
import takehomeassignments.aspire.mockaspireloanapplication.api.requests.LoanApplicationRequest;
import takehomeassignments.aspire.mockaspireloanapplication.entities.LoanEntity;
import takehomeassignments.aspire.mockaspireloanapplication.entities.UserEntity;
import takehomeassignments.aspire.mockaspireloanapplication.enums.InstallmentFrequency;
import takehomeassignments.aspire.mockaspireloanapplication.exceptions.InsufficientBalanceException;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.InstallmentRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.LoanRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.UserRepository;
import takehomeassignments.aspire.mockaspireloanapplication.services.UserService;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private InstallmentRepository installmentRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testApplyForLoan() {

        String userId = "testUserId";
        String token = "testToken";
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setApprovedLimit(1000f);
        user.setTokenExpiry(ZonedDateTime.now().plusHours(1));
        LoanApplicationRequest loanRequest = new LoanApplicationRequest();
        loanRequest.setUserId(userId);
        loanRequest.setTotalAmount(500f);
        loanRequest.setInterestRate(5f);
        loanRequest.setStartDate(ZonedDateTime.now());
        loanRequest.setFrequency(InstallmentFrequency.WEEKLY);
        loanRequest.setNumberOfInstallments(10);

        LoanEntity loanEntity = mock(LoanEntity.class);
        when(loanEntity.getId()).thenReturn("testLoanId");

        when(userRepository.findByIdAndToken(userId, token)).thenReturn(Optional.of(user.getTokenExpiry()));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(loanRepository.getTotalLoansDisbursedToUser(userId, LoanEntity.PENDING_STATUSES)).thenReturn(0f);
        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loanEntity);

        String loanId = userService.applyForLoan(loanRequest, token);

        verify(userRepository, times(1)).findByIdAndToken(userId, token);
        verify(userRepository, times(1)).findById(userId);
        verify(loanRepository, times(1)).getTotalLoansDisbursedToUser(userId, LoanEntity.PENDING_STATUSES);
        verify(loanRepository, times(1)).save(any(LoanEntity.class));
        verify(installmentRepository, times(loanRequest.getNumberOfInstallments())).save(any());
    }

    @Test
    public void testApplyForLoanTotalAmountZero() {

        String userId = "testUserId";
        String token = "testToken";
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setApprovedLimit(1000f);
        user.setTokenExpiry(ZonedDateTime.now().plusHours(1));
        LoanApplicationRequest loanRequest = new LoanApplicationRequest();
        loanRequest.setUserId(userId);
        loanRequest.setTotalAmount(0f);
        loanRequest.setInterestRate(5f);
        loanRequest.setStartDate(ZonedDateTime.now());
        loanRequest.setFrequency(InstallmentFrequency.WEEKLY);
        loanRequest.setNumberOfInstallments(10);

        when(userRepository.findByIdAndToken(userId, token)).thenReturn(Optional.of(user.getTokenExpiry()));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(loanRepository.getTotalLoansDisbursedToUser(userId, LoanEntity.PENDING_STATUSES)).thenReturn(0f);

        assertThrows(NotAcceptableStatusException.class, () -> userService.applyForLoan(loanRequest, token));
    }

    @Test
    public void testApplyForLoanInterestRateNegative() {

        String userId = "testUserId";
        String token = "testToken";
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setApprovedLimit(1000f);
        user.setTokenExpiry(ZonedDateTime.now().plusHours(1));
        LoanApplicationRequest loanRequest = new LoanApplicationRequest();
        loanRequest.setUserId(userId);
        loanRequest.setTotalAmount(500f);
        loanRequest.setInterestRate(-5f);
        loanRequest.setStartDate(ZonedDateTime.now());
        loanRequest.setFrequency(InstallmentFrequency.WEEKLY);
        loanRequest.setNumberOfInstallments(10);

        when(userRepository.findByIdAndToken(userId, token)).thenReturn(Optional.of(user.getTokenExpiry()));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(loanRepository.getTotalLoansDisbursedToUser(userId, LoanEntity.PENDING_STATUSES)).thenReturn(0f);

        assertThrows(NotAcceptableStatusException.class, () -> userService.applyForLoan(loanRequest, token));
    }

    @Test
    public void testApplyForLoanExceedsApprovedLimit() {

        String userId = "testUserId";
        String token = "testToken";
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setApprovedLimit(1000f);
        user.setTokenExpiry(ZonedDateTime.now().plusHours(1));
        LoanApplicationRequest loanRequest = new LoanApplicationRequest();
        loanRequest.setUserId(userId);
        loanRequest.setTotalAmount(1500f);
        loanRequest.setInterestRate(5f);
        loanRequest.setStartDate(ZonedDateTime.now());
        loanRequest.setFrequency(InstallmentFrequency.WEEKLY);
        loanRequest.setNumberOfInstallments(10);

        when(userRepository.findByIdAndToken(userId, token)).thenReturn(Optional.of(user.getTokenExpiry()));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(loanRepository.getTotalLoansDisbursedToUser(userId, LoanEntity.PENDING_STATUSES)).thenReturn(0f);

        assertThrows(InsufficientBalanceException.class, () -> userService.applyForLoan(loanRequest, token));
    }

    @Test
    public void testApplyForLoanTokenExpired() {

        String userId = "testUserId";
        String token = "testToken";
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setApprovedLimit(1000f);
        user.setTokenExpiry(ZonedDateTime.now().minusHours(1));
        LoanApplicationRequest loanRequest = new LoanApplicationRequest();
        loanRequest.setUserId(userId);
        loanRequest.setTotalAmount(500f);
        loanRequest.setInterestRate(5f);
        loanRequest.setStartDate(ZonedDateTime.now());
        loanRequest.setFrequency(InstallmentFrequency.WEEKLY);
        loanRequest.setNumberOfInstallments(10);

        when(userRepository.findByIdAndToken(userId, token)).thenReturn(Optional.of(user.getTokenExpiry()));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(loanRepository.getTotalLoansDisbursedToUser(userId, LoanEntity.PENDING_STATUSES)).thenReturn(0f);

        assertThrows(NotAcceptableStatusException.class, () -> userService.applyForLoan(loanRequest, token));
    }
}