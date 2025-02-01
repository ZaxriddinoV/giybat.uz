package giybat.uz.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatePostDTO {
    @NotBlank(message = "title writing is a must")
    private String title;
    @NotBlank(message = "Content writing is a must")
    private String content;
}
