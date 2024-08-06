package takehomeassignments.aspire.mockaspireloanapplication.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import takehomeassignments.aspire.mockaspireloanapplication.enums.InstallmentStatus;

import java.time.ZonedDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "installments")
public class InstallmentEntity {

    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    LoanEntity loan;

    @Column(nullable = false)
    Float dueAmount;

    @Column
    Float paidAmount;

    @Column
    ZonedDateTime paidAt;

    @Column
    ZonedDateTime dueAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    InstallmentStatus status;

    @Column(nullable = false)
    Integer installmentNumber;
}
