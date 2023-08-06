package com.challenge.project.http.service;

import com.challenge.project.constants.ThreadsRequestProperty;
import com.challenge.project.http.handler.HttpResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramPasswordEncodingService {

    private final HeaderService headerService;

    private final HttpResponseHandler handler;

    public String getEncryptPassword(String password) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            log.info("Call EncryptPassword Key, PubKey");
            HttpPost httpPost = new HttpPost(ThreadsRequestProperty.ENCRYPT_PASSWORD_SYNC_URL.getProperty());
            headerService.setInstagramLoginApiDefaultHeader(httpPost);
            httpPost.setHeader("X-DEVICE-ID", "android-f25fd50653d51090");
            log.info("Executing request ENCRYPT_PASSWORD_SYNC_URL = {} ", httpPost.getRequestLine());
            UrlEncodedFormEntity entity = getUrlEncodedBodyForgetEncryptPassword();
            httpPost.setEntity(entity);
            HttpResponse execute = (HttpResponse) httpclient.execute(httpPost, handler.getInstagramApiResponseHandler());
            AtomicReference<String> keyId = new AtomicReference<>("");
            AtomicReference<String> pubKey = new AtomicReference<>("");
            Arrays.stream(execute.getAllHeaders())
                    .forEach(h -> {
                        if (h.getName().equals("ig-set-password-encryption-key-id")) {
                            keyId.set(h.getValue());
                        } else if (h.getName().equals("ig-set-password-encryption-pub-key")) {
                            pubKey.set(h.getValue());
                        }
                    });
            return encryptPassword(password,keyId.get(), pubKey.get());

        } catch (Exception e) {
            log.error("Error = {}", e.getMessage());
            return null;
        }
    }


    // Password 암호화 알고리즘
    public String encryptPassword(String password, String enc_id, String enc_pub_key) throws Exception {
        byte[] randKey = new byte[32], iv = new byte[12];
        SecureRandom sran = new SecureRandom();
        sran.nextBytes(randKey);
        sran.nextBytes(iv);
        String time = String.valueOf(System.currentTimeMillis() / 1000);

        String decodedPubKey =
                new String(Base64.getDecoder().decode(enc_pub_key), StandardCharsets.UTF_8)
                        .replaceAll("-(.*)-|\n", "");
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        rsaCipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA")
                .generatePublic(
                        new X509EncodedKeySpec(Base64.getDecoder().decode(decodedPubKey))));
        byte[] randKeyEncrypted = rsaCipher.doFinal(randKey);

        Cipher aesGcmCipher = Cipher.getInstance("AES/GCM/NoPadding");
        aesGcmCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(randKey, "AES"),
                new GCMParameterSpec(128, iv));
        aesGcmCipher.updateAAD(time.getBytes());
        byte[] passwordEncrypted = aesGcmCipher.doFinal(password.getBytes());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(Integer.valueOf(1).byteValue());
        out.write(Integer.valueOf(enc_id).byteValue());
        out.write(iv);
        out.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putChar((char) randKeyEncrypted.length).array());
        out.write(randKeyEncrypted);
        out.write(Arrays.copyOfRange(passwordEncrypted, passwordEncrypted.length - 16,
                passwordEncrypted.length));
        out.write(Arrays.copyOfRange(passwordEncrypted, 0, passwordEncrypted.length - 16));

        return String.format("#PWD_INSTAGRAM:%s:%s:%s", "4", time,
                Base64.getEncoder().encodeToString(out.toByteArray()));
    }

    private UrlEncodedFormEntity getUrlEncodedBodyForgetEncryptPassword() {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("id", "android-"+ generateAndroidId()));
        form.add(new BasicNameValuePair("experiments", ThreadsRequestProperty.EXPERIMENTS_HEADER.getProperty()));
        return new UrlEncodedFormEntity(form, Consts.UTF_8);
    }

    public String generateAndroidId() {
        long maxExclusive = (long) (Math.pow(36, 9)); // 36^9 is equivalent to 1e24 in JavaScript
        long randomValue = new Random().nextLong() & Long.MAX_VALUE; // Generate a non-negative long value
        long randomNumber = randomValue % maxExclusive; // Make sure the value is within the valid range
        return Long.toString(randomNumber, 36); // Convert the random number to base-36 string representation
    }




}
