package com.drone.rental.controller;

import com.drone.rental.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 通用接口控制器
 */
@Api(tags = "通用接口")
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${file.upload-path:uploads/}")
    private String uploadPath;

    @Value("${file.access-url:/uploads/}")
    private String accessUrl;

    private Path uploadDir;

    @PostConstruct
    public void init() {
        // 使用项目根目录下的uploads文件夹
        String userDir = System.getProperty("user.dir");
        uploadDir = Paths.get(userDir, uploadPath);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + uploadDir, e);
        }
    }

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名不能为空");
        }

        // 获取文件扩展名
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // 生成新文件名
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

        // 保存文件
        Path destPath = uploadDir.resolve(newFilename);
        try {
            file.transferTo(destPath.toFile());
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }

        // 返回访问URL
        String fileUrl = accessUrl + newFilename;
        return Result.success(fileUrl);
    }
}
