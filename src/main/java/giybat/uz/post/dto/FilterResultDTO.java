package giybat.uz.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterResultDTO<T> {
    private List<T> content;
    private Long total;

    public FilterResultDTO(List<T> content, Long total) {
        this.content = content;
        this.total = total;
    }

}
