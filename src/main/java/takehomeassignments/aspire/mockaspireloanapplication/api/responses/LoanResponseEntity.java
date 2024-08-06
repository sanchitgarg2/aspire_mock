package takehomeassignments.aspire.mockaspireloanapplication.api.responses;

import lombok.Builder;
import lombok.Data;
import takehomeassignments.aspire.mockaspireloanapplication.entities.LoanEntity;
import takehomeassignments.aspire.mockaspireloanapplication.enums.InstallmentFrequency;
import takehomeassignments.aspire.mockaspireloanapplication.enums.LoanStatus;

import java.time.ZonedDateTime;

@Data
@Builder
public class LoanResponseEntity {
    private String id;
    private String userId;
    private String reviewedBy;
    private ZonedDateTime reviewedAt;
    private LoanStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime startDate;
    private Float interestRate;
    private Float amount;
    private Integer numberOfInstallments;
    private InstallmentFrequency repaymentFrequency;

    public static LoanResponseEntity mapEntityToResponse(LoanEntity entity){
        return LoanResponseEntity.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .reviewedBy(entity.getReviewedBy() != null? entity.getReviewedBy().getId():null)
                .reviewedAt(entity.getReviewedAt())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .startDate(entity.getStartDate())
                .interestRate(entity.getInterestRate())
                .amount(entity.getAmount())
                .numberOfInstallments(entity.getNumberOfInstallments())
                .repaymentFrequency(entity.getRepaymentFrequency())
                .build();
    }
}
