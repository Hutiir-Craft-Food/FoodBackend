package com.khutircraftubackend.validated;

import com.khutircraftubackend.product.image.exception.ImageNotFoundException;
import com.khutircraftubackend.product.image.exception.ImageValidationException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class ImageMimeValidator {

    private static final Tika TIKA = new Tika();
    private static final String ERROR_MIME_TYPE = "Неприпустимий MIME-тип файлу: %s (тип: %s)";

    // TODO Need implement test
    public void validateMimeTypes(List<MultipartFile> files, Set<String> allowedMimeType) {

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();

            if(!allowedMimeType.contains(contentType)){
                throw new ImageValidationException(String.format(
                        ERROR_MIME_TYPE, file.getOriginalFilename(), file.getContentType()));
            }

            try {
                String detectedMimeType = TIKA.detect(file.getInputStream());

                if (detectedMimeType == null || !allowedMimeType.contains(detectedMimeType)) {
                    throw new ImageNotFoundException(String.format(
                            ERROR_MIME_TYPE, file.getOriginalFilename(), detectedMimeType));
                    // TODO SCRUM-207 Need to send a notification to the administration. Do it via the method or via
                    // the announcement of a new executionHandler? What data do we send in the notification? Example:
                    // (userId, SessionId, Ip-ad, user-agent, endpoint, dateTime, fileName, MIME-Type, Geolocation?
                    // You can insert the team lead's resolution here
                }
            } catch (IOException e) {
                throw new ImageValidationException(
                        String.format("Не вдалося визначити MIME-тип файлу: %s", fileName));
            }
        }
    }
}
