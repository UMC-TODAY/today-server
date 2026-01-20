package com.example.todayserver.domain.member.service.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsFileService {
    private final AmazonS3Client amazonS3Client;

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

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}
