package giybat.uz.post.dto;

import giybat.uz.attach.dto.GetAttachDTO;
import giybat.uz.profile.dto.ProfileShortInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostInfoDTO {
    private Integer id;
    private GetAttachDTO photo;
    private ProfileShortInfo user;
    private String content;
    private LocalDateTime createdDate;
}
