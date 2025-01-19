package giybat.uz.attach.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachDTO {
    private String id;
    private String originName;
    private Long size;
    private String extension;
    private LocalDateTime createdData;
    private String url;
}
