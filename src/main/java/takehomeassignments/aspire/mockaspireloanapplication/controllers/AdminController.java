package takehomeassignments.aspire.mockaspireloanapplication.controllers;

import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import takehomeassignments.aspire.mockaspireloanapplication.entities.LoanEntity;
import takehomeassignments.aspire.mockaspireloanapplication.services.AdminService;


@Controller
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    AdminService adminService;

    //    Can Approve loans
    @GetMapping("/getPendingLoans")
    public Page<LoanEntity> getPendingLoans(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size , String adminUserId) {
        //Get all pending loans for the admin User to approve
        Pageable pageable = PageRequest.of(page, size);
        return adminService.getPendingLoans(pageable, adminUserId);
    }


    //performLoanAction
    //    modifyLoanDetails - defer/delay installment


    //    Approve Loan

    @PutMapping("/approveLoan")
    @ResponseBody
    public boolean approveLoan(@RequestParam String loanId,  @RequestParam String adminUserId) throws BadRequestException {
        //Get all pending loans for the admin User to approve
        adminService.approveLoan(loanId, adminUserId);
        return true;
    }

    //performLoanAction
    //    modifyLoanDetails - defer/delay installment
    @PutMapping("/rejectLoan")
    @ResponseBody
    public boolean rejectLoan(@RequestParam String loanId,  @RequestParam String adminUserId) {
        //Get all pending loans for the admin User to approve
        adminService.rejectLoan(loanId, adminUserId);
        return true;
    }

//    Approval limit
//            (optional - privilege allocation expiry time)
//
//    Block/Disable AdminUser - until X date
//
//
}
