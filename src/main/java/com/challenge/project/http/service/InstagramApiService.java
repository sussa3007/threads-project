package com.challenge.project.http.service;

import com.challenge.project.constants.ErrorCode;
import com.challenge.project.exception.ServiceLogicException;
import com.challenge.project.http.dto.BkClientContextDto;
import com.challenge.project.http.dto.ParamsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramApiService {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Gson gson = new Gson();

    private String EXPERIMENTS = "ig_android_fci_onboarding_friend_search,ig_android_device_detection_info_upload,ig_android_account_linking_upsell_universe,ig_android_direct_main_tab_universe_v2,ig_android_allow_account_switch_once_media_upload_finish_universe,ig_android_sign_in_help_only_one_account_family_universe,ig_android_sms_retriever_backtest_universe,ig_android_direct_add_direct_to_android_native_photo_share_sheet,ig_android_spatial_account_switch_universe,ig_growth_android_profile_pic_prefill_with_fb_pic_2,ig_account_identity_logged_out_signals_global_holdout_universe,ig_android_prefill_main_account_username_on_login_screen_universe,ig_android_login_identifier_fuzzy_match,ig_android_mas_remove_close_friends_entrypoint,ig_android_shared_email_reg_universe,ig_android_video_render_codec_low_memory_gc,ig_android_custom_transitions_universe,ig_android_push_fcm,multiple_account_recovery_universe,ig_android_show_login_info_reminder_universe,ig_android_email_fuzzy_matching_universe,ig_android_one_tap_aymh_redesign_universe,ig_android_direct_send_like_from_notification,ig_android_suma_landing_page,ig_android_prefetch_debug_dialog,ig_android_smartlock_hints_universe,ig_android_black_out,ig_activation_global_discretionary_sms_holdout,ig_android_video_ffmpegutil_pts_fix,ig_android_multi_tap_login_new,ig_save_smartlock_universe,ig_android_caption_typeahead_fix_on_o_universe,ig_android_enable_keyboardlistener_redesign,ig_android_sign_in_password_visibility_universe,ig_android_nux_add_email_device,ig_android_direct_remove_view_mode_stickiness_universe,ig_android_hide_contacts_list_in_nux,ig_android_new_users_one_tap_holdout_universe,ig_android_ingestion_video_support_hevc_decoding,ig_android_mas_notification_badging_universe,ig_android_secondary_account_in_main_reg_flow_universe,ig_android_secondary_account_creation_universe,ig_android_account_recovery_auto_login,ig_android_pwd_encrytpion,ig_android_bottom_sheet_keyboard_leaks,ig_android_sim_info_upload,ig_android_mobile_http_flow_device_universe,ig_android_hide_fb_button_when_not_installed_universe,ig_android_account_linking_on_concurrent_user_session_infra_universe,ig_android_targeted_one_tap_upsell_universe,ig_android_gmail_oauth_in_reg,ig_android_account_linking_flow_shorten_universe,ig_android_vc_interop_use_test_igid_universe,ig_android_notification_unpack_universe,ig_android_registration_confirmation_code_universe,ig_android_device_based_country_verification,ig_android_log_suggested_users_cache_on_error,ig_android_reg_modularization_universe,ig_android_device_verification_separate_endpoint,ig_android_universe_noticiation_channels,ig_android_account_linking_universe,ig_android_hsite_prefill_new_carrier,ig_android_one_login_toast_universe,ig_android_retry_create_account_universe,ig_android_family_apps_user_values_provider_universe,ig_android_reg_nux_headers_cleanup_universe,ig_android_mas_ui_polish_universe,ig_android_device_info_foreground_reporting,ig_android_shortcuts_2019,ig_android_device_verification_fb_signup,ig_android_onetaplogin_optimization,ig_android_passwordless_account_password_creation_universe,ig_android_black_out_toggle_universe,ig_video_debug_overlay,ig_android_ask_for_permissions_on_reg,ig_assisted_login_universe,ig_android_security_intent_switchoff,ig_android_device_info_job_based_reporting,ig_android_add_account_button_in_profile_mas_universe,ig_android_add_dialog_when_delinking_from_child_account_universe,ig_android_passwordless_auth,ig_radio_button_universe_2,ig_android_direct_main_tab_account_switch,ig_android_recovery_one_tap_holdout_universe,ig_android_modularized_dynamic_nux_universe,ig_android_fb_account_linking_sampling_freq_universe,ig_android_fix_sms_read_lollipop,ig_android_access_flow_prefil";

    public String requestLogin(String username, String password) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String encryptPassword = getEncryptPassword(password);
            UrlEncodedFormEntity entity = makeRequestLoginPostEntity(encryptPassword, username);
            log.info("Call Request Instagram Login");
            HttpPost httpPost = new HttpPost("https://i.instagram.com/api/v1/bloks/apps/com.bloks.www.bloks.caa.login.async.send_login_request/");
            setDefaultHeader(httpPost);
            log.info("Executing request InstagramAPI = {} ", httpPost.getRequestLine());
            System.out.println(Arrays.toString(httpPost.getAllHeaders()));
            httpPost.setEntity(entity);
            String execute = (String) httpclient.execute(httpPost, getResponseHandler());
            if (execute.contains("IG-Set-Authorization")) {
                JsonElement jsonElement = JsonParser.parseString(execute);
                String getString = jsonElement.getAsJsonObject().get("layout")
                        .getAsJsonObject().get("bloks_payload")
                        .getAsJsonObject().get("tree").getAsJsonObject().get("㐟").getAsJsonObject().get("#").getAsString().replaceAll("\\\\","");
                String substring = getString.substring(getString.indexOf("\"headers\":\"") + 1, getString.lastIndexOf("}\""));
                int start = substring.indexOf("\"{");
                int end = substring.indexOf("}\"");
                String authBody = substring.substring(start + 1, end + 1).replaceAll("\"\\{","{")+"}";
                String result = JsonParser.parseString(authBody)
                        .getAsJsonObject()
                        .get("IG-Set-Authorization")
                        .getAsString()
                        .replaceAll("Bearer IGT:2:", "");
                System.out.println(result);
            }
            return null;
        } catch (Exception e) {
            log.error("Error = {}", e.getMessage());
            return null;
        }
    }



    private String getEncryptPassword(String password) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            log.info("Call EncryptPassword Key, PubKey");
            HttpPost httpPost = new HttpPost("https://i.instagram.com/api/v1/qe/sync/");
            setDefaultHeader(httpPost);
            httpPost.setHeader("X-DEVICE-ID", "android-f25fd50653d51090");
            log.info("Executing request InstagramAPI = {} ", httpPost.getRequestLine());
            UrlEncodedFormEntity entity = getUrlEncodedBodyForgetEncryptPassword();
            httpPost.setEntity(entity);
            HttpResponse execute = (HttpResponse) httpclient.execute(httpPost, getResponseHandler());
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

    private void setDefaultHeader(HttpPost httpPost) {
        httpPost.setHeader("User-Agent", "Barcelona 289.0.0.77.109 Android");
        httpPost.setHeader("Sec-Fetch-Site", "same-origin");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    }

    private UrlEncodedFormEntity getUrlEncodedBodyForgetEncryptPassword() {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("id", "android-f25fd50653d51090"));
        form.add(new BasicNameValuePair("experiments", EXPERIMENTS));
        return new UrlEncodedFormEntity(form, Consts.UTF_8);
    }

    private UrlEncodedFormEntity makeRequestLoginPostEntity(String encryptPassword, String username) {
        Map<String, String> clientInputParams = Map.of(
                "password", encryptPassword,
                "contact_point", username,
                "device_id", "android-" + generateAndroidId()
        );
        Map<String, String> serverParams = Map.of(
                "credential_type", "password",
                "device_id", "android-" + generateAndroidId()
        );
        ParamsDto paramsDto = ParamsDto.builder()
                .client_input_params(clientInputParams)
                .server_params(serverParams)
                .build();
        BkClientContextDto bkClientContextDto = BkClientContextDto.builder()
                .bloks_version("5f56efad68e1edec7801f630b5c122704ec5378adbee6609a448f105f34a9c73")
                .styles_id("instagram")
                .build();
        return getUrlEncodedBodyForRequestLogin(paramsDto, bkClientContextDto);
    }


    private String generateAndroidId() {
        long maxExclusive = (long) (Math.pow(36, 9)); // 36^9 is equivalent to 1e24 in JavaScript
        long randomValue = new Random().nextLong() & Long.MAX_VALUE; // Generate a non-negative long value
        long randomNumber = randomValue % maxExclusive; // Make sure the value is within the valid range
        return Long.toString(randomNumber, 36); // Convert the random number to base-36 string representation
    }

    private UrlEncodedFormEntity getUrlEncodedBodyForRequestLogin(ParamsDto paramsDto, BkClientContextDto bkClientContextDto) {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("params", gson.toJson(paramsDto)));
        form.add(new BasicNameValuePair("bk_client_context", gson.toJson(bkClientContextDto)));
        form.add(new BasicNameValuePair("bloks_versioning_id", bkClientContextDto.getBloks_version()));
        return new UrlEncodedFormEntity(form, Consts.UTF_8);
    }

    private String encryptPassword(String password, String enc_id, String enc_pub_key) throws Exception {
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

        // Write to final byte array
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



    private ResponseHandler<?> getResponseHandler() {
        return response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity responseBody = response.getEntity();
                //Todo : response를 한번 이상 파싱하면 예외 발생
                String res = EntityUtils.toString(responseBody);
                if (res.contains("㐟")) {
                    log.info("Return = {}", "response body");
                    return res;
                } else {
                    log.info("Return = {}", "response");
                    return response;
                }
            } else {
                if (status == 404) {
                    throw new ServiceLogicException(ErrorCode.NOT_FOUND);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);

                }
            }
        };
    }

}
