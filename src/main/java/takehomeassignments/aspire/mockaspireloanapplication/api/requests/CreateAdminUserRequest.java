package takehomeassignments.aspire.mockaspireloanapplication.api.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAdminUserRequest {
    private String email;
    private String phoneNumber;
    private Float approvalLimit;
}