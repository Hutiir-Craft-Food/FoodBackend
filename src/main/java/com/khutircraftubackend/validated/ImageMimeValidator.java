package com.khutircraftubackend.validated;

import com.khutircraftubackend.exception.httpstatus.BadRequestException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class ImageMimeValidator {

    private static final Tika tika = new Tika();
    private static final String ERROR_MIME_TYPE = "Неприпустимий MIME-тип файлу: %s (тип: %s)";

    // TODO Need implement test
    public void validateMimeTypes(List<MultipartFile> files, String allowedMimeType) {

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            if(!file.getContentType().startsWith(allowedMimeType)){
                throw new BadRequestException(String.format(
                        ERROR_MIME_TYPE, file.getOriginalFilename(), file.getContentType()));
            }

            try {
                String detectedMimeType = tika.detect(file.getInputStream());

                if (detectedMimeType == null || !detectedMimeType.startsWith(allowedMimeType)) {
                    throw new BadRequestException(String.format(
                            ERROR_MIME_TYPE, file.getOriginalFilename(), detectedMimeType));
                    // TODO SCRUM-207 Need to send a notification to the administration. Do it via the method or via
                    // the announcement of a new executionHandler? What data do we send in the notification? Example:
                    // (userId, SessionId, Ip-ad, user-agent, endpoint, dateTime, fileName, MIME-Type, Geolocation?
                    // You can insert the team lead's resolution here
                }
            } catch (IOException e) {
                throw new BadRequestException("Не вдалося визначити MIME-тип файлу " + fileName);
            }
        }
    }
}
