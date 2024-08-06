package takehomeassignments.aspire.mockaspireloanapplication.api.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import takehomeassignments.aspire.mockaspireloanapplication.enums.InstallmentFrequency;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationRequest {
    private String userId;
    private Float totalAmount;
    private Float interestRate;
    private InstallmentFrequency frequency;
    private Integer numberOfInstallments;
    private ZonedDateTime startDate;
}
