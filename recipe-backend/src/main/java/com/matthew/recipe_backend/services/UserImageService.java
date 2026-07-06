package com.matthew.recipe_backend.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.models.UserImage;
import com.matthew.recipe_backend.repositories.UserImageRepository;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class UserImageService {

    private final UserImageRepository userImageRepository;

    public UserImageService(UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
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

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("File exceeds maximum size of 5MB");
        }

        String baseKey = UUID.randomUUID().toString();

        ByteArrayOutputStream mediumStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(800, 600)
                .keepAspectRatio(true)
                .outputFormat("jpg")
                .toOutputStream(mediumStream);

        ByteArrayOutputStream thumbStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
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

    private void saveImage(ByteArrayOutputStream stream, String filename) throws IOException {
        // local storage for now, swap for S3 later
        Path path = Paths.get("uploads/" + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, stream.toByteArray());
    }

    public List<String> getImagesByUser(User user) {
        return userImageRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(UserImage::getBaseKey)
                .toList();
    }
}
