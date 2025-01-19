package giybat.uz.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse(int success, String message, T data) {
        this.code = success;
        this.message = message;
        this.data = data;
    }
}