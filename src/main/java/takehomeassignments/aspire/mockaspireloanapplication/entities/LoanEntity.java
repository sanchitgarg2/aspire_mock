package takehomeassignments.aspire.mockaspireloanapplication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import takehomeassignments.aspire.mockaspireloanapplication.enums.InstallmentFrequency;
import takehomeassignments.aspire.mockaspireloanapplication.enums.LoanStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "loans")
public class LoanEntity {

    public static final List<LoanStatus> PENDING_STATUSES = List.of(LoanStatus.DEFAULTED, LoanStatus.PENDING, LoanStatus.APPROVED);
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    private AdminUser reviewedBy;

    @Column
    private ZonedDateTime reviewedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime startDate;

    //This interest rate is stored as the mathematical interest rate per period eg : 0.01 for 12% p.a interest rate and monthly installments
    @Column(nullable = false)
    private Float interestRate;

    @Column(nullable = false)
    private Float amount;

    @Column(nullable = false)
    private Integer numberOfInstallments;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InstallmentFrequency repaymentFrequency;

}
