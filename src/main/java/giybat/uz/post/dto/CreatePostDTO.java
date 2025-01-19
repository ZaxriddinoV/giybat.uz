package giybat.uz.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostDTO {
    private String photoId;
    @NotBlank(message = "Content writing is a must")
    private String content;
}
