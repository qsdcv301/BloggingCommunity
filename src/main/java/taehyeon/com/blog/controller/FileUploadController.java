package taehyeon.com.blog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email,
            @RequestParam("type") String type) {
        Map<String, String> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("success", "false");
            response.put("error", "파일을 선택하세요.");
            return ResponseEntity.ok(response);
        }

        try {
            // 사용자 이메일을 이용해 하위 디렉토리 경로 생성
            String userDir = uploadDir + File.separator + email;
            File directory = new File(userDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 이름을 사용자 이메일과 파라미터 값으로 구성
            String fileName = email + "_" + type + "_" + file.getOriginalFilename();
            File destinationFile = new File(directory + File.separator + fileName);
            file.transferTo(destinationFile);
            response.put("success", "false");
            response.put("error", "파일 업로드에 성공했습니다. '" + fileName + "'");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", "false");
            response.put("error", "서버 오류가 발생했습니다.");
            return ResponseEntity.ok(response);
        }
    }

}
