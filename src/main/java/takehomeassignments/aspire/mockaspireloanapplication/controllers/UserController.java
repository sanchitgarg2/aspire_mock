package takehomeassignments.aspire.mockaspireloanapplication.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import takehomeassignments.aspire.mockaspireloanapplication.api.requests.ApplyLoanRequest;
import takehomeassignments.aspire.mockaspireloanapplication.api.requests.PayNextInstallmentRequest;
import takehomeassignments.aspire.mockaspireloanapplication.api.responses.LoanResponseEntity;
import takehomeassignments.aspire.mockaspireloanapplication.entities.UserEntity;
import takehomeassignments.aspire.mockaspireloanapplication.services.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    //Create User
    @PostMapping
    @ResponseBody
    public String createUser(@RequestBody UserEntity user) {
        try {
            return userService.createUser(user);
        } catch (NotAcceptableStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not created", e);
        }
    }


    //Apply for Loan

    @PostMapping("/loan")
    public String applyForLoan(@RequestBody ApplyLoanRequest loanRequest) {
        try {
            return userService.applyForLoan(loanRequest);
        } catch (NotAcceptableStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan not applied", e);
        }
    }

    @PostMapping("/payNextInstallment")
    public boolean payNextInstallment(@RequestBody PayNextInstallmentRequest paymentRequest) {
        try {
            userService.payNextInstallment(paymentRequest);
            return true;
        } catch (NotAcceptableStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in paying installment ", e);
        }
    }

    @GetMapping("/getLoans/{userId}")
    public List<LoanResponseEntity> getLoans(@PathVariable String userId) {
        try {
            return userService.getLoans(userId).stream().map(LoanResponseEntity::mapEntityToResponse).toList();
        } catch (NotAcceptableStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in getting loans ", e);
        }
    }

    //View Installments
    //View User Details

}
