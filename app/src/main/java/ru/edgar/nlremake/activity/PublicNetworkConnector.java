package ru.edgar.nlremake.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Автономный публичный коннектор API Матрёшка RP.
 * Не требует библиотек Volley / OkHttp. Работает на встроенном HttpURLConnection.
 */
public class PublicNetworkConnector {

    /**
     *         // Инициализируем коннектор
     *         PublicNetworkConnector connector = new PublicNetworkConnector(this);
     *
     *         // НАЗНАЧЕНИЕ ПАРАМЕТРОВ ВРУЧНУЮ (подставьте свои тестовые данные)
     *         connector.accountId = 123456;                         // ID аккаунта игрока
     *         connector.sessionHash = "a1b2c3d4e5f6g7h8i9j0klmnopqrst"; // Хэш сессии авторизации
     *         connector.launcherDeviceHash = "custom_test_hash_111";   // Хэш устройства (можно null)
     *
     *         // Запускаем запрос — он автоматически подставит назначенные вами данные в URL
     *         connector.requestAccountDetails();
     */

    private static final String TAG = "MatreshkaNetwork";
    public final MainScreenActivity mainActivity;

    // Переменные сессии, считываемые автоматически
    public int accountId = -1;
    public String sessionHash = "null";
    public String launcherDeviceHash = null;

    public PublicNetworkConnector(MainScreenActivity mainActivity) {
        this.mainActivity = mainActivity;
        // Сразу ищем параметры в памяти приложения
        this.loadSessionParameters();
    }

    /**
     * Автоматический поиск и загрузка параметров сессии из настроек и интентов лаунчера
     */
    public void loadSessionParameters() {
        if (this.mainActivity == null) return;

        // 1. Читаем данные из оригинального файла SharedPreferences Матрёшки
        SharedPreferences sharedPreferences = this.mainActivity.getSharedPreferences("com.matreshkarp.game.LAUNCHER_AUTH", Context.MODE_PRIVATE);
        this.accountId = sharedPreferences.getInt("account_id", -1);
        this.sessionHash = sharedPreferences.getString("session_hash", "null");
        this.launcherDeviceHash = sharedPreferences.getString("launcher_device_hash", null);

        // 2. Если в хранилище пусто, проверяем Intent запуска
        if (this.accountId == -1) {
            Intent intent = this.mainActivity.getIntent();
            if (intent != null) {
                int intExtra = intent.getIntExtra("accountId", -1);
                String stringExtra = intent.getStringExtra("sessionHash");
                String stringExtra2 = intent.getStringExtra("launcherDeviceHash");

                if (intExtra != -1) {
                    this.accountId = intExtra;
                    this.sessionHash = stringExtra;
                    this.launcherDeviceHash = stringExtra2;
                    System.out.println("[Matreshka] Параметры перехвачены из Intent!");
                }
            }
        }

        // Вывод найденных параметров в консоль для контроля
        System.out.println("====== [Matreshka] НАЙДЕННЫЕ ПАРАМЕТРЫ СЕССИИ ======");
        System.out.println("account_id: " + this.accountId);
        System.out.println("session_hash: " + this.sessionHash);
        System.out.println("launcher_device_hash: " + this.launcherDeviceHash);
        System.out.println("=================================================");
    }

    /**
     * Сборка подписанного URL и запуск асинхронного сетевого HTTP-запроса профиля
     */
    public void requestAccountDetails() {
        this.loadSessionParameters();

        /*if (this.accountId == -1) {
            System.out.println("[Matreshka] Отмена запроса: Игрок не авторизован (account_id = -1)");
            return;
        }*/

        String baseUrl = "https://moblauncher.matrp.ru/api/Requests/AccountDetails.php";
        String finalUrl;

        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(baseUrl).append("?");

            // Автоматически подставляем извлеченные параметры
            urlBuilder.append("accountId=").append(URLEncoder.encode(String.valueOf("21957543"), "UTF-8"));
            urlBuilder.append("&sessionHash=").append(URLEncoder.encode(String.valueOf(this.sessionHash), "UTF-8"));

            // Генерируем оригинальный android_id устройства
            String androidId = Settings.Secure.getString(this.mainActivity.getContentResolver(), "android_id");
            urlBuilder.append("&deviceHash=").append(URLEncoder.encode(String.valueOf(androidId), "UTF-8"));

            // Добавляем старый хэш устройства, если он есть
            if (this.launcherDeviceHash != null) {
                urlBuilder.append("&oldDeviceHash=").append(URLEncoder.encode(String.valueOf(this.launcherDeviceHash), "UTF-8"));
            }

            urlBuilder.append("&client=").append(URLEncoder.encode("prod", "UTF-8"));
            finalUrl = urlBuilder.toString();

        } catch (UnsupportedEncodingException e) {
            System.out.println("[Matreshka] Ошибка кодирования URL parameters");
            e.printStackTrace();
            return;
        }

        System.out.println("[Matreshka] Отправка HTTP-запроса на URL: " + finalUrl);

        // Так как запросы в сеть нельзя делать в главном потоке Android, запускаем фоновую задачу
        new HttpGetTask().execute(finalUrl);
    }

    /**
     * Обработка ответа сервера и вывод всей информации в консоль (Аналог case 12)
     */
    public void handleServerResponse(String rawResponse) {
        System.out.println("==================================================");
        System.out.println("[Matreshka Console Output] СЫРОЙ ОТВЕТ СЕРВЕРА:");
        System.out.println(rawResponse);
        System.out.println("==================================================");

        Log.d(TAG, "Raw Response: " + rawResponse);

        try {
            JSONObject jsonObject = new JSONObject(rawResponse);

            if (jsonObject.has("type")) {
                String type = jsonObject.getString("type").toUpperCase(Locale.ROOT);
                System.out.println("[Matreshka JSON -> type]: " + type);

                if (type.equals("UNAUTHORIZED")) {
                    System.out.println("[Matreshka Status] Внимание: Запрос отклонен сервером (Не авторизован)!");
                }
            }

            if (jsonObject.has("text")) {
                String text = jsonObject.getString("text");
                System.out.println("[Matreshka JSON -> text]: " + text);
            }

        } catch (JSONException e) {
            System.out.println("[Matreshka JSON Error] Ошибка: Ответ не является валидным JSON-объектом!");
            e.printStackTrace();
        }
    }

    /**
     * Внутренний стандартный класс Android для фонового выполнения сетевого HTTP-запроса
     */
    private class HttpGetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); // 5 секунд таймаут ожидания
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return "{\"type\":\"error\", \"text\":\"HTTP Error code: " + responseCode + "\"}";
                }

                StringBuilder result = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();

            } catch (Exception e) {
                return "{\"type\":\"error\", \"text\":\"Network Exception: " + e.getMessage() + "\"}";
            } finally {
                if (connection != null) connection.disconnect();
                try {
                    if (reader != null) reader.close();
                } catch (Exception ignored) {}
            }
        }

        @Override
        protected void onPostExecute(String response) {
            // Возвращаем результат в главный поток и печатаем в консоль
            handleServerResponse(response);
        }
    }
}
