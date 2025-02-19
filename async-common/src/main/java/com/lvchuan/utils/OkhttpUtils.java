package com.lvchuan.utils;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.Map;

/**
 *
 * @author erp
 */
@Slf4j
@Component
public class OkhttpUtils {


    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static final MediaType XML = MediaType.get("text/xml");

    @Autowired
    private OkHttpClient okHttpClient;

    public String post(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (SocketTimeoutException e) {
            log.error("[OkHttpClientUtils#post] 请求超时。url={}, e={}", url, e);
            throw new RuntimeException("Okhttp timeout.");
        } catch (Exception e) {
            log.error("[OkHttpClientUtils#post] 请求异常。url={}, e={}", url, e);
            return null;
        }
    }

    /**
     * 请求接口暴恐请求头参数
     * @param url url地址
     * @param json 参数
     * @param headMap 请求头map
     * @return 反参
     */
    public String post(String url, String json, Map<String, String> headMap) {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);
        if (CollUtil.isNotEmpty(headMap)) {
            for (Map.Entry<String, String> entry : headMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = requestBuilder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            assert response.body() != null;
            String result = response.body().string();
            log.info("postResponse={}", result);
            return result;
        } catch (SocketTimeoutException e) {
            log.error("[OkHttpClientUtils#post] 请求超时。url={}, e={}", url, e);
            throw new RuntimeException("Okhttp timeout.");
        } catch (Exception e) {
            log.error("[OkHttpClientUtils#post] 请求异常。url={}, e={}", url, e);
            return null;
        }
    }

    public String postXml(String url, String requestBody) {
        RequestBody body = RequestBody.create(XML, requestBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.info("[OkHttpClientUtils#postXml] 返回失败。url={}, response={}", url, response);
                return null;
            }
            return response.body().string();
        } catch (SocketTimeoutException e) {
            log.error("[OkHttpClientUtils#post] 请求超时。url={}, e={}", url, e);
            throw new RuntimeException("Okhttp timeout.");
        } catch (Exception e) {
            log.error("[OkHttpClientUtils#postXml] 请求异常。url={}, e={}", url, e);
            return null;
        }
    }

}
