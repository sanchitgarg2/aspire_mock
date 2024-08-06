package takehomeassignments.aspire.mockaspireloanapplication.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import takehomeassignments.aspire.mockaspireloanapplication.entities.AdminUser;
import takehomeassignments.aspire.mockaspireloanapplication.services.AdminUserService;

@Controller
@AllArgsConstructor
@RequestMapping("/adminUser")
public class AdminUserController {

    AdminUserService adminUserService;


    //Perform Caller Auth here
//    Create Admin role
    @PostMapping
    @ResponseBody
    public String createAdminUser(@RequestBody AdminUser request) {
        //Create AdminUser
        return adminUserService.createAdminUser(request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAdminUser(@PathVariable String id) {
        adminUserService.deleteAdminUser(id);
    }

    //Rest of the APIs are probably out of scope for this assignment
    //This controller would also drive the following APIs
    //    SetAdminUserPrivileges
    //    ModifyApprovalLimit
    //    BlockAdminUser

}
