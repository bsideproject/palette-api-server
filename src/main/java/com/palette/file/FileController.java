package com.palette.file;

import com.palette.file.dto.UploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Validated
@RequestMapping("/api/v1")
@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(@Valid @RequestParam(name = "file[]") @Size(min = 1, max = 3) @NotNull ArrayList<MultipartFile> multipartFiles) throws IOException {
        List<String> urls = new ArrayList<>();
        for(int i = 0; i < multipartFiles.size(); i++) {
            MultipartFile file = multipartFiles.get(i);
            String url = fileService.upload(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()), file.getSize());
            urls.add(i, url);
        }
        return ResponseEntity.ok(UploadResponse.of(urls));
    }

    @DeleteMapping("/file")
    public void delete(@RequestParam("url") String url) {
        fileService.delete(url);
    }

    @DeleteMapping("/files")
    public void delete(@RequestParam("url") ArrayList<String> urls) {
        urls.forEach(fileService::delete);
    }
}
