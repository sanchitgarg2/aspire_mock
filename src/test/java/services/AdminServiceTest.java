package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import takehomeassignments.aspire.mockaspireloanapplication.entities.AdminUser;
import takehomeassignments.aspire.mockaspireloanapplication.entities.LoanEntity;
import takehomeassignments.aspire.mockaspireloanapplication.enums.PrivilegeEnum;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.AdminUserRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.InstallmentRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.LoanRepository;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.UserRepository;
import takehomeassignments.aspire.mockaspireloanapplication.services.AdminService;
import takehomeassignments.aspire.mockaspireloanapplication.services.NotificationsService;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationsService notificationsService;

    @Mock
    private InstallmentRepository installmentRepository;

    @Mock
    private AdminUserRepository adminUserRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testApproveLoanSuccess() {
        String loanId = "loanId";
        String adminUserId = "adminUserId";
        AdminUser adminUser = new AdminUser();
        adminUser.setPrivileges(Map.of(PrivilegeEnum.CAN_APPROVE_LOANS, true));
        LoanEntity loan = new LoanEntity();
        loan.setAmount(5000f);
        adminUser.setApprovalLimit(10000f);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(adminUserRepository.findByUserId(adminUserId)).thenReturn(Optional.of(adminUser));

        assertTrue(adminService.approveLoan(loanId, adminUserId));

        verify(loanRepository, times(1)).save(loan);
        verify(notificationsService, times(1)).sendNotification(anyString(), anyString(), any());
    }

    @Test
    public void testApproveLoanFailDueToPrivileges() {
        String loanId = "loanId";
        String adminUserId = "adminUserId";
        AdminUser adminUser = new AdminUser();
        adminUser.setPrivileges(Map.of(PrivilegeEnum.CAN_APPROVE_LOANS, false));
        LoanEntity loan = new LoanEntity();
        loan.setAmount(5000f);
        adminUser.setApprovalLimit(10000f);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(adminUserRepository.findByUserId(adminUserId)).thenReturn(Optional.of(adminUser));

        assertThrows(ResponseStatusException.class, () -> adminService.approveLoan(loanId, adminUserId));
    }

    @Test
    public void testApproveLoanFailDueToAmountAboveLimit() {
        String loanId = "loanId";
        String adminUserId = "adminUserId";
        AdminUser adminUser = new AdminUser();
        adminUser.setPrivileges(Map.of(PrivilegeEnum.CAN_APPROVE_LOANS, true));
        LoanEntity loan = new LoanEntity();
        loan.setAmount(15000f);
        adminUser.setApprovalLimit(10000f);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(adminUserRepository.findByUserId(adminUserId)).thenReturn(Optional.of(adminUser));

        assertThrows(ResponseStatusException.class, () -> adminService.approveLoan(loanId, adminUserId));
    }

    @Test
    public void testRejectLoanSuccess() {
        String loanId = "loanId";
        String adminUserId = "adminUserId";
        AdminUser adminUser = new AdminUser();
        adminUser.setPrivileges(Map.of(PrivilegeEnum.CAN_APPROVE_LOANS, true));
        LoanEntity loan = new LoanEntity();
        loan.setAmount(5000f);
        adminUser.setApprovalLimit(10000f);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(adminUserRepository.findByUserId(adminUserId)).thenReturn(Optional.of(adminUser));

        assertTrue(adminService.rejectLoan(loanId, adminUserId));

        verify(loanRepository, times(1)).save(loan);
        verify(notificationsService, times(1)).sendNotification(anyString(), anyString(), any());
    }

    @Test
    public void testRejectLoanFailDueToAmountAboveLimit() {
        String loanId = "loanId";
        String adminUserId = "adminUserId";
        AdminUser adminUser = new AdminUser();
        adminUser.setPrivileges(Map.of(PrivilegeEnum.CAN_APPROVE_LOANS, true));
        LoanEntity loan = new LoanEntity();
        loan.setAmount(15000f);
        adminUser.setApprovalLimit(10000f);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(adminUserRepository.findByUserId(adminUserId)).thenReturn(Optional.of(adminUser));

        assertThrows(ResponseStatusException.class, () -> adminService.rejectLoan(loanId, adminUserId));
    }
}
