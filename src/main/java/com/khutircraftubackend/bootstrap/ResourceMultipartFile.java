package com.khutircraftubackend.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@RequiredArgsConstructor
public class ResourceMultipartFile implements MultipartFile {

	private final Resource resource;
	private final String fileName;

	@Override
	public String getName() {
		return fileName;
	}

	@Nullable
	@Override
	public String getOriginalFilename() {
		return fileName;
	}

	@Nullable
	@Override
	public String getContentType() {
		return "image/jpeg";
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public long getSize() {
		try {
			return resource.contentLength();
		} catch (IOException e) {
			return 0;
		}
	}

	@Override
	public byte[] getBytes() throws IOException {
		return resource.getInputStream().readAllBytes();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return resource.getInputStream();
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		Files.copy(resource.getInputStream(), dest.toPath(),
				REPLACE_EXISTING);
	}
}
