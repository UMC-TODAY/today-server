package com.example.todayserver.domain.member.service.util;

import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsFileService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveProfileImg(MultipartFile multipartFile, Long memberId) throws IOException {
        return uploadProfileImg(multipartFile, memberId);
    }

    public String uploadProfileImg(MultipartFile file, Long memberId) throws IOException {
        if (file.getContentType() == null ||
                !file.getContentType().startsWith("image/")) {
            throw new MemberException(MemberErrorCode.FILE_TYPE_ERROR);
        }

        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String fileName = "profile/" + memberId + "/" + UUID.randomUUID() + ext;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return getPublicUrl(fileName);
    }

    private String getPublicUrl(String key) {
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }
}
