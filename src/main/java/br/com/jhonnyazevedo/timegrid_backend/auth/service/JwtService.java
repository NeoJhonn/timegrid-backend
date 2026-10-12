package br.com.jhonnyazevedo.timegrid_backend.auth.service;

import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String secret;
    private final long accessExpirationMinutes;
    private final long refreshExpirationMinutes;

    public JwtService(
            @Value("${timegrid.jwt.secret}") String secret,
            @Value("${timegrid.jwt.expiration-minutes}") long accessExpirationMinutes,
            @Value("${timegrid.jwt.refresh-expiration-minutes}") long refreshExpirationMinutes
    ) {
        this.secret = secret;
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshExpirationMinutes = refreshExpirationMinutes;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, "access", accessExpirationMinutes);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, "refresh", refreshExpirationMinutes);
    }

    public Optional<String> extractEmail(String token) {
        return extractEmail(token, "access");
    }

    public Optional<String> extractEmailFromRefreshToken(String token) {
        return extractEmail(token, "refresh");
    }

    private String generateToken(User user, String type, long expirationMinutes) {
        Instant now = Instant.now();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getEmail());
        payload.put("userId", user.getId().toString());
        payload.put("role", user.getRole().name());
        payload.put("type", type);
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plusSeconds(expirationMinutes * 60).getEpochSecond());

        String encodedHeader = encodeJson(header);
        String encodedPayload = encodeJson(payload);
        String content = encodedHeader + "." + encodedPayload;

        return content + "." + sign(content);
    }

    private Optional<String> extractEmail(String token, String expectedType) {
        try {
            if (!isSignatureValid(token)) {
                return Optional.empty();
            }

            Map<String, Object> payload = extractPayload(token);
            Number expiration = (Number) payload.get("exp");

            if (expiration == null || expiration.longValue() < Instant.now().getEpochSecond()) {
                return Optional.empty();
            }

            String type = (String) payload.get("type");

            if (!expectedType.equals(type)) {
                return Optional.empty();
            }

            return Optional.ofNullable((String) payload.get("sub"));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private boolean isSignatureValid(String token) {
        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            return false;
        }

        String content = parts[0] + "." + parts[1];
        String expectedSignature = sign(content);

        return expectedSignature.equals(parts[2]);
    }

    private Map<String, Object> extractPayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);

        return objectMapper.readValue(payloadBytes, new TypeReference<>() {
        });
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(value);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception exception) {
            throw new IllegalStateException("Nao foi possivel gerar o token JWT", exception);
        }
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(key);

            byte[] signature = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("Nao foi possivel assinar o token JWT", exception);
        }
    }
}
