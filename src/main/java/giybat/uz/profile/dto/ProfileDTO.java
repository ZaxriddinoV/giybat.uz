package giybat.uz.profile.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import giybat.uz.attach.entity.AttachEntity;
import giybat.uz.profile.entity.ProfileEntity;
import giybat.uz.profile.enums.ProfileRole;
import giybat.uz.profile.enums.ProfileStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO {
    private Integer id;
    @NotBlank(message = "Name not found")
    private String name;
    @NotBlank(message = "Surname not found")
    private String surname;
    @NotBlank(message = "Email not found")
    private String username;
    @NotBlank(message = "Password not found")
    private String password;
    private AttachEntity photo;
    private ProfileStatus status;
    private ProfileRole role;
    private String JwtToken;
    private String refreshToken;

    public ProfileEntity mapToEntity() {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setName(this.getName());
        profileEntity.setSurname(this.getSurname());
        profileEntity.setPassword(this.getPassword());
        profileEntity.setRole(this.getRole());
        profileEntity.setUsername(this.getUsername());
        profileEntity.setStatus(this.getStatus());
        profileEntity.setCreated_date(LocalDateTime.now());
        profileEntity.setVisible(Boolean.TRUE);
        return profileEntity;
    }
}
