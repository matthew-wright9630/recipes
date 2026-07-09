package com.matthew.recipe_backend.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.models.UserImage;
import com.matthew.recipe_backend.repositories.UserImageRepository;

import net.coobird.thumbnailator.Thumbnails;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class UserImageService {

    private final UserImageRepository userImageRepository;
    private final S3Client s3Client;

    public UserImageService(UserImageRepository userImageRepository, S3Client s3Client) {
        this.userImageRepository = userImageRepository;
        this.s3Client = s3Client;
    }

    public String processAndUploadImage(MultipartFile file, User user) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !List.of("image/jpeg", "image/png", "image/webp")
                .contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("File exceeds maximum size of 5MB");
        }

        String baseKey = UUID.randomUUID().toString();
        ByteArrayOutputStream mediumStream = new ByteArrayOutputStream();
        ByteArrayOutputStream thumbStream = new ByteArrayOutputStream();

        byte[] imageBytes = file.getBytes();

        Thumbnails.of(new ByteArrayInputStream(imageBytes))
                .size(800, 600)
                .keepAspectRatio(true)
                .outputFormat("jpg")
                .toOutputStream(mediumStream);

        Thumbnails.of(new ByteArrayInputStream(imageBytes))
                .size(400, 300)
                .keepAspectRatio(true)
                .outputFormat("jpg")
                .toOutputStream(thumbStream);

        saveImage(mediumStream, baseKey + "-medium.jpg");
        saveImage(thumbStream, baseKey + "-thumb.jpg");

        UserImage userImage = new UserImage(user, baseKey, OffsetDateTime.now());
        userImageRepository.save(userImage);

        return baseKey;
    }

    @Value("${aws.bucket}")
    private String bucket;

    private void saveImage(ByteArrayOutputStream stream, String filename) {

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key("recipes/" + filename)
                        .contentType("image/jpeg")
                        .build(),
                RequestBody.fromBytes(stream.toByteArray()));
    }

    public List<String> getImagesByUser(User user) {
        return userImageRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(UserImage::getBaseKey)
                .toList();
    }
}
