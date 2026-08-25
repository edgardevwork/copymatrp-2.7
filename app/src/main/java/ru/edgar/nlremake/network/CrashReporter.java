package ru.edgar.nlremake.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CrashReporter {

    private static final String SERVER_URL = ConfigLinks.crashReportUrl; // Замените на URL вашего сервера

    /**
     * Главный публичный метод отправки репорта об ошибке.
     * Вызывать из блоков catch(Exception e) или при сбое логики.
     *
     * @param context    Контекст активити
     * @param uid        ID пользователя (если есть, иначе null)
     * @param location   Участок кода/Класс/Метод, где произошел баг
     * @param errorText  Текст ошибки или e.getMessage() / e.toString()
     */
    public static void sendBugReport(Context context, String uid, String location, String errorText) {
        try {
            JSONObject reportJson = new JSONObject();

            // 1. Идентификаторы и локация бага
            reportJson.put("uid", uid != null ? uid : JSONObject.NULL);
            reportJson.put("location", location);
            reportJson.put("error_text", errorText);

            // 2. Время (в формате ГГГГ-ММ-ДД ЧЧ:ММ:СС)
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            reportJson.put("timestamp", currentTime);

            // 3. Данные об устройстве
            reportJson.put("device_brand", Build.BRAND);       // Производитель (например, Samsung, Xiaomi)
            reportJson.put("device_model", Build.MODEL);       // Модель (например, POCO X3, Redmi Note 10)
            reportJson.put("android_version", Build.VERSION.RELEASE); // Версия Андроид (например, 13)
            reportJson.put("android_sdk", Build.VERSION.SDK_INT);     // API Level (например, 33)

            // 4. Версия вашего приложения
            String appVersion = "unknown";
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                appVersion = pInfo.versionName;
            } catch (Exception ignored) {}
            reportJson.put("app_version", appVersion);

            // 5. Тип интернет-соединения
            reportJson.put("network_type", getNetworkType(context));

            // Запускаем асинхронную отправку на сервер
            new SendReportTask().execute(reportJson.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Утилитный метод для определения типа сети
    private static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return "WIFI";
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return "CELLULAR";
            }
        }
        return "OFFLINE";
    }

    // Асинхронная отправка JSON через POST запрос
    private static class SendReportTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(SERVER_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = params[0].getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                Log.e("CrashReporter", "Не удалось отправить репорт на сервер: " + e.getMessage());
                return false;
            } finally {
                if (conn != null) conn.disconnect();
            }
        }
    }
}