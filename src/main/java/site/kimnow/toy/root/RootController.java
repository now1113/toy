package site.kimnow.toy.root;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("text/html;charset=UTF-8"))
                .body("<h1>Toy 프로젝트 API 서버입니다.</h1>");
    }
}
