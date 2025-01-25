package giybat.uz.post.dto;

import giybat.uz.attach.dto.GetAttachDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {
    private Integer id;
    private GetAttachDTO photo;
    private String title;
    private String content;
    private LocalDateTime createdDate;
}
