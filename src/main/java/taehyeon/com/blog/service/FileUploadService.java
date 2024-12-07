package taehyeon.com.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public boolean uploadFile(MultipartFile file, String email, String type) {
        // 선택된 파일이 있는지 확인
        if (file.isEmpty()) {
            return false;
        }
        try {
            // 저장 타입 이용해 하위 디렉토리 경로 생성
            String userDir = uploadDir + File.separator + email;
            File directory = new File(userDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 이름을 사용자 아이디와 기존파일 이름 값으로 구성
            String fileName = type + '_' + file.getOriginalFilename();
            File destinationFile = new File(directory + File.separator + fileName);
            file.transferTo(destinationFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String findImageFilePath(String email, String type) {
        // 실제 파일 시스템 경로를 사용
        String folderPath = uploadDir + "/" + email + "/";
        File dir = new File(folderPath);
        File[] matchingFiles = dir.listFiles((dir1, name) -> name.startsWith(type + "_"));
        // 파일이 존재하면 웹 경로로 반환
        return (matchingFiles != null && matchingFiles.length > 0)
                ? "/images/" + email + "/" + matchingFiles[0].getName()
                : null;
    }

}