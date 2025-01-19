package giybat.uz.profile.entity;



import giybat.uz.attach.entity.AttachEntity;
import giybat.uz.profile.dto.ProfileDTO;
import giybat.uz.profile.enums.ProfileRole;
import giybat.uz.profile.enums.ProfileStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProfileStatus status = ProfileStatus.ACTIVE;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ProfileRole role;
    @Column(name = "visible")
    private Boolean visible = Boolean.TRUE;
    @Column(name = "created_date")
    private LocalDateTime created_date;
    @Column(name = "photo_id")
    private String photoId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    private AttachEntity photo;

    public ProfileDTO convertToDTO() {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(this.getId());
        profileDTO.setName(this.getName());
        profileDTO.setSurname(this.getSurname());
        profileDTO.setUsername(this.getUsername());
        profileDTO.setPassword(this.getPassword());
        profileDTO.setStatus(this.getStatus());
        profileDTO.setRole(this.getRole());
        return profileDTO;
    }
}
