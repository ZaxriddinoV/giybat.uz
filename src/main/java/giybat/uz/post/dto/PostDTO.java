package giybat.uz.post.dto;

import giybat.uz.attach.dto.GetAttachDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {
    private Integer id;
    private GetAttachDTO attachDTO;
    private String content;
    private LocalDateTime createdDate;
}
