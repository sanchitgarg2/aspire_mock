package takehomeassignments.aspire.mockaspireloanapplication.api.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayNextInstallmentRequest {
    private String userId;
    private Float amount;
    private String loanId;
}
