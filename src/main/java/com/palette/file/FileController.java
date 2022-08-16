package com.palette.file;

import com.palette.file.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RequestMapping("/api/v1")
@RestController
public class FileController {

    private final FileService fileService;

    public FileController (FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(@RequestParam("file")MultipartFile multipartFile) throws IOException {
        String url = fileService.upload(multipartFile.getInputStream(), Objects.requireNonNull(multipartFile.getOriginalFilename()), multipartFile.getSize());
        return ResponseEntity.ok(UploadResponse.of(url));
    }

    @DeleteMapping("/file")
    public void delete(@RequestParam("url") String url) {
        fileService.delete(url);
    }
}
