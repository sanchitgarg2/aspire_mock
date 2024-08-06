package takehomeassignments.aspire.mockaspireloanapplication.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import takehomeassignments.aspire.mockaspireloanapplication.api.requests.LoanApplicationRequest;
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
    public String applyForLoan(@RequestBody LoanApplicationRequest loanRequest, @RequestParam(required = true) String token) {
        try {
            return userService.applyForLoan(loanRequest , token);
        } catch (NotAcceptableStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan not applied", e);
        }
    }

    @PostMapping("/payNextInstallment")
    public boolean payNextInstallment(@RequestBody PayNextInstallmentRequest paymentRequest, @RequestParam(required = true) String token) {
        try {
            userService.payNextInstallment(paymentRequest, token);
            return true;
        } catch (NotAcceptableStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in paying installment ", e);
        }
    }

    @GetMapping("/getLoans/{userId}")
    public List<LoanResponseEntity> getLoans(@PathVariable String userId , @RequestParam(required = true) String token){
        try {
            return userService.getLoans(userId, token).stream().map(LoanResponseEntity::mapEntityToResponse).toList();
        } catch (NotAcceptableStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in getting loans ", e);
        }
    }

    @GetMapping("/login/{userId}")
    public String login(@PathVariable String userId) {
        try {
            return userService.login(userId);
        } catch (NotAcceptableStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in login ", e);
        }
    }


        //View Installments
    //View User Details

}
