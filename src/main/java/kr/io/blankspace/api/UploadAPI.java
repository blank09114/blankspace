package kr.io.blankspace.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadAPI
{
    private final S3Client s3;

    @Value("${app.aws.s3.bucket}")
    private String bucket;

    @Value("${app.aws.s3.public-base-url:}")
    private String publicBaseUrl;

    public UploadAPI(S3Client s3)  { this.s3 = s3; }

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".gif");

    // 이미지 업로드
    @PostMapping
    public Map<String, String> uploadImage
    (@RequestParam("file") MultipartFile file, @RequestParam(value = "dir", defaultValue = "misc") String dir) throws IOException  {
        validateImage(file);

        final String safeDir = sanitizeDir(dir);
        final String ext = extractExt(file.getOriginalFilename());
        final String today = LocalDate.now().toString(); // 2026-01-05
        final String key = safeDir + "/" + today + "/" + UUID.randomUUID() + ext;

        PutObjectRequest req = PutObjectRequest.builder()
        .bucket(bucket).key(key).contentType(file.getContentType()).build();
        s3.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        final String url = buildPublicUrl(key);

        return Map.of("url", url, "key", key);
    }

    // 검증
    private void validateImage(MultipartFile file)
    {
        if (file == null || file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "empty file");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not image");

        // 확장자 체크
        String originalName = file.getOriginalFilename();
        String ext = extractExt(originalName);

        if (!ALLOWED_EXTENSIONS.contains(ext))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported image extension");

        // 파일 크기 제한: 10MB
        long max = 10L * 1024 * 1024;
        if (file.getSize() > max)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file too large (max 10MB)");
    }


    private String sanitizeDir(String dir)
    {
        if (!StringUtils.hasText(dir)) return "misc";
        return dir.replaceAll("[^a-zA-Z0-9_\\-/]", "");
    }

    private String extractExt(String originalName)
    {
        if (!StringUtils.hasText(originalName)) return ".png";
        int i = originalName.lastIndexOf('.');
        if (i < 0) return ".png";

        String ext = originalName.substring(i).toLowerCase();
        if (ext.length() > 10) return ".png";
        return ext;
    }

    private String buildPublicUrl(String key)
    {
        if (!StringUtils.hasText(publicBaseUrl)) { return key; }

        String base = publicBaseUrl.replaceAll("/+$", "");
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
        encodedKey = encodedKey.replace("%2F", "/");

        return base + "/" + encodedKey;
    }
}