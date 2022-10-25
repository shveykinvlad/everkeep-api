package com.everkeep.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.codec.Hex;

@UtilityClass
public class DigestUtils {

    private static final String SHA3_256 = "SHA3-256";

    @SneakyThrows
    public static String sha256Hex(String value) {
        var digest = MessageDigest.getInstance(SHA3_256);
        var hashAsBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));

        return new String(Hex.encode(hashAsBytes));
    }
}
