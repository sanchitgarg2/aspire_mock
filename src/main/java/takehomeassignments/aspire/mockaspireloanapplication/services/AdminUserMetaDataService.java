package takehomeassignments.aspire.mockaspireloanapplication.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import takehomeassignments.aspire.mockaspireloanapplication.entities.AdminUser;
import takehomeassignments.aspire.mockaspireloanapplication.repositories.AdminUserRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminUserMetaDataService {
    AdminUserRepository adminUserRepository;
    public String createAdminUser(AdminUser request) {

        //TODO: Perform caller Authorization here
        //TODO: Perform Validations Here and set sensible defaults

        //Create AdminUser
        request.setId(UUID.randomUUID().toString().substring(0, 8));
        return adminUserRepository.save(request).getId();
    }

    public void deleteAdminUser(String id) {
        adminUserRepository.deleteById(id);
    }
}
