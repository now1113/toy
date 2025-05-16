package site.kimnow.toy.common.exception;


import org.springframework.http.HttpStatus;

import java.io.Serializable;

public interface ErrorCode extends Serializable {
    String getMessage();
    int getStatusCode();

    default HttpStatus getStatus() {
        return HttpStatus.valueOf(getStatusCode());
    }
}
