package com.khutircraftubackend.validated;

import com.khutircraftubackend.product.image.exception.ImageValidationException;
import com.khutircraftubackend.validated.exception.SuspiciousFileException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MimeTypeValidator {

    private static final Tika TIKA = new Tika();

    // TODO Need implement test
    public void validateMimeTypes(List<MultipartFile> files, Set<String> allowedMimeType) {

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String declaredType = file.getContentType();

            if (!allowedMimeType.contains(declaredType)) {
                throw new ImageValidationException(String.format(MimeTypeValidatorResponseMessages.ERROR_MIME_TYPE,
                        file.getOriginalFilename(), file.getContentType()));
            }

            try {
                String detectedMimeType = TIKA.detect(file.getInputStream());

                if (detectedMimeType == null || !allowedMimeType.contains(detectedMimeType)) {
                    throw new SuspiciousFileException(
                            String.format(MimeTypeValidatorResponseMessages.ERROR_MIME_TYPE, declaredType),
                            file.getOriginalFilename(), declaredType, detectedMimeType);
                }
            } catch (IOException e) {
                throw new ImageValidationException(
                        String.format("Не вдалося визначити MIME-тип файлу: %s", fileName));
            }
        }
    }
}
