package taehyeon.com.blog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 사용자 이메일로 디렉토리 생성
            File userDir = new File(uploadDir, email);
            if (!userDir.exists()) {
                Files.createDirectories(userDir.toPath()); // NIO로 디렉토리 생성
            }

            // 파일 이름 구성
            String fileName = email + "_" + type + "_" + file.getOriginalFilename();
            File destinationFile = new File(userDir, fileName);

            // 파일 저장
            file.transferTo(destinationFile);

            response.put("success", "true");
            response.put("message", "파일 업로드에 성공했습니다.");
            response.put("fileName", fileName);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", "false");
            response.put("error", "서버 오류가 발생했습니다. " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
