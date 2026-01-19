package com.fruit.sale.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP客户端工具类
 *
 * @author fruit-sale
 * @since 2025-01-14
 */
public class HttpClientUtil {

    /**
     * 发送POST请求（JSON）
     */
    public static String doPostJson(String url, String jsonBody, Map<String, String> headers) {
        HttpURLConnection connection = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;

        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 设置默认请求头
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // 设置自定义请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 发送请求体
            if (jsonBody != null && !jsonBody.isEmpty()) {
                writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(jsonBody);
                writer.flush();
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } else {
                // 读取错误响应
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
                throw new RuntimeException("HTTP Error: " + responseCode + " - " + errorResponse.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("HTTP请求失败: " + e.getMessage(), e);
        } finally {
            closeQuietly(writer);
            closeQuietly(reader);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 发送GET请求
     */
    public static String doGet(String url, Map<String, String> headers) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setDoInput(true);

            // 设置默认请求头
            connection.setRequestProperty("Accept", "application/json");

            // 设置自定义请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                return ""; // 204 No Content
            } else {
                // 读取错误响应
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    reader = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    throw new RuntimeException("HTTP Error: " + responseCode + " - " + errorResponse.toString());
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("HTTP请求失败: " + e.getMessage(), e);
        } finally {
            closeQuietly(reader);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 安全关闭流
     */
    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // 忽略关闭异常
            }
        }
    }
}