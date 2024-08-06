package takehomeassignments.aspire.mockaspireloanapplication.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import takehomeassignments.aspire.mockaspireloanapplication.enums.PrivilegeEnum;

import java.util.HashMap;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "admin_users")
public class AdminUser {

    @Id
    private String id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Column
    private Float approvalLimit;

    @Transient
    HashMap<PrivilegeEnum, Boolean> adminUserPrivileges = new HashMap<>();

    @JsonIgnore
    public boolean hasPrivilege(PrivilegeEnum privilege) {
        //This is a function stub.
        //To implement complex privilege checks, this function is the easiest and most maintainable place to do so.
        //Here the privileges are assumed to true, but in a real system, this would be more complex and fetched from a database or a cache
        return adminUserPrivileges.getOrDefault(privilege , true);
    }

}
