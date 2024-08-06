package takehomeassignments.aspire.mockaspireloanapplication.enums;

public enum PrivilegeEnum {

    //Self-explanatory
    CAN_APPROVE_LOANS,

    //typically for users who want to increase the terms of their loans
    MODIFY_LOAN_DETAILS,


    //For the purposes of this assignement I will only use the above two privileges, but in a real system, there would be many more
    //Here are the possible privileges that could be added to the system - applying and using them is beyond the scope of this assignment

    //For CUSTOMER_SERVICE_REPRESENTATIVES
    BLOCK_USER,
    UNBLOCK_USER,

    //Power Users from the approval team
    //Highly privileged users who can mark a loan as NPA and accept a loss for the company
    WRITE_OFF_LOAN,
    MARK_DEFAULTER,
    UNMARK_DEFAULTER,

    //For the superUser
    //Can do anything
    SUPER_USER
}
