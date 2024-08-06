package takehomeassignments.aspire.mockaspireloanapplication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users")
public class UserEntity {

    public static final Float DEFAULT_APPROVED_LIMIT = 10000f;
    @Id
    private String id;

    @Column(unique = true)
    private String email;

    @Column
    private String name;

    @Column(unique = true)
    private String phoneNumber;

    @Column
    private Float approvedLimit;

    @Column
    private Boolean defaultedBefore;

    @Column
    private Boolean currentlyDefaulted;

    @Column
    private String token;

    @Column
    private ZonedDateTime tokenExpiry;

}
