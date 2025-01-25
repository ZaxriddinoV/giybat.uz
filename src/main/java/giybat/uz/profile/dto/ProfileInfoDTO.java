package giybat.uz.profile.dto;

import giybat.uz.attach.dto.GetAttachDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileInfoDTO {
    private Integer id;
    private String name;
    private String surname;
    private String username;
    private GetAttachDTO photo;
}
