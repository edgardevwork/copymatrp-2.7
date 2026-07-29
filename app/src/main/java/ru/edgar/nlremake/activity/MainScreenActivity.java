package ru.edgar.nlremake.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.nlremake.model.Api;
import ru.edgar.nlremake.model.Archive;
import ru.edgar.nlremake.model.ArchivePath;
import ru.edgar.nlremake.model.Deleted;
import ru.edgar.nlremake.model.FaqList;
import ru.edgar.nlremake.model.Main;
import ru.edgar.nlremake.model.News;
import ru.edgar.nlremake.model.Servers;
import ru.edgar.nlremake.network.Helper;
import ru.edgar.nlremake.other.Interface;
import ru.edgar.nlremake.other.Lists;
import ru.edgar.nlremake.other.Utils;
import ru.edgar.matrp.R;
import ru.edgar.nlremake.ui.FullHeightVideoView;
import ru.edgar.space.EdgarConectV2;
import ru.edgar.space.SAMP;

public class MainScreenActivity  extends AppCompatActivity {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private NotificationManager notifManager = null;
    private FullHeightVideoView mVideoView;
    public static boolean isAuth = false;
    private static MainScreenActivity instance;
    private FrameLayout mainScreen;
    private FirebaseAuth mAuth;
    private TextView statusBar;
    public static String nickName;

    String apiLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Launcher);
        setContentView(R.layout.activity_mainscreen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        instance = this;
        hideUI();
        statusBar = (TextView) findViewById(R.id.lm_status);
        mainScreen = (FrameLayout) findViewById(R.id.mainscreen);
        mVideoView = (FullHeightVideoView) findViewById(R.id.videoView);
        mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() +"/"+R.raw.loading));

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mVideoView.start();
            }
        });

        mVideoView.start();

        otherInit();

        Log.i("GOOGLE AUTH", "Init Google");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            isAuth = true;
        } else {
            isAuth = false;
        }
        if(!isAuth) {
            onInitAuthGoogle();
        } else onRequestPermissions();
    }

    public static MainScreenActivity getInstance() {
        return instance;
    }

    private boolean netIsAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        return false;
    }

    public void hideUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void otherInit() {
        // Создание папки где будет кеш
        getExternalFilesDir("");
        // Создание канала уведомлений
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (notifManager.getNotificationChannel("space_1") == null) {
            NotificationChannel notificationChannel = new NotificationChannel("space_1", "space", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("space");
            notificationChannel.enableVibration(false);
            notificationChannel.setLightColor(-16711936);
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setVibrationPattern(new long[]{0});
            notifManager.createNotificationChannel(notificationChannel);
        }
        // Иницелизация фаир бейз
        FirebaseAnalytics.getInstance(this);
        FirebaseApp.initializeApp(this);
        FileDownloader.init(this);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onInitAuthGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, options);
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            googleSignInClient.signOut().addOnCompleteListener(this, new a(googleSignInClient));
            return;
        }
        Intent i = googleSignInClient.getSignInIntent();
        startActivityForResult(i, 1234);
    }

    public static class a implements OnCompleteListener<Void> {
        GoogleSignInClient googleSignInClient;
        public a(GoogleSignInClient googleSignInClient) {
            this.googleSignInClient = googleSignInClient;
        }

        @Override // com.google.android.gms.tasks.OnCompleteListener
        public final void onComplete(@NonNull Task<Void> task) {
            MainScreenActivity.getInstance().onInitAuthGoogle();
        }
    }
    public void onRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // Проверяем разрешение на чтение хранилища
        /*if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // Проверяем разрешение на запись в хранилище (для Android < 10)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }*/

        // Проверяем разрешение на запись аудио
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO);
        }

        // Проверяем разрешение на уведомления (только для Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // Запрашиваем все разрешения одним вызовом, если есть что запрашивать
        if (!permissionsToRequest.isEmpty()) {
            requestPermissions(permissionsToRequest.toArray(new String[0]), 1);
        } else {
            loadSettings();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean perfect = true;

            // Проверяем каждое разрешение
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    perfect = false;
                    break;
                }
            }

            if (perfect) {
                // Все разрешения получены
                loadSettings();
            } else {
                // Какое-то разрешение не получено
                Toast.makeText(this, "Нужны все разрешения для работы приложения", Toast.LENGTH_SHORT).show();
                onRequestPermissions();
            }
        }
    }

    public void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
        System.out.println("Директория успешно удалена.");
    }

    public long getFileOrDirectorySize(String path) {
        File file = new File(path);

        if (file.isFile()) {
            if (file.getName().equals("gta_sa.set") || file.getName().equals("CINFO.BIN")) {
                System.out.println(file.getName());
                return 0;
            }
            System.out.println("Это файл: " + path);
            return file.length();
        } else if (file.isDirectory()) {
            System.out.println("Это директория: " + path);
            return calculateDirectorySize(file);
        } else {
            System.out.println("Указанный путь не является ни файлом, ни директорией.");
            return 0;
        }
    }

    private long calculateDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals("settings.ini") || file.getName().equals("MINFO.BIN")) {
                        System.out.println(file.getName());
                        continue;
                    }
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += calculateDirectorySize(file);
                }
            }
        }
        return size;
    }

    public void loadSettings() {
        if(!netIsAvailable()) {
            /*dialogUI = new DialogUI(
                    this,
                    "Подключиться",
                    "Чтобы продолжить\nподключитесь к интернету!", // Заголовок
                    "Подключиться к интернету и продолжить играть" // Описание
            );
            // Устанавливаем действие для Yes кнопки (с анимацией)
            dialogUI.setOnClickYesBtn(new Runnable() {
                @Override
                public void run() {
                    if(netIsAvailable()) {
                        dialogUI.dismiss();
                        loadSettings();
                        return;
                    }
                    try {
                        // Пытаемся открыть настройки Wi-Fi
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            dialogUI.dismiss();
                            loadSettings();
                            startActivity(intent);
                        } else {
                            // Если не получилось, пробуем открыть общие настройки сетей
                            Intent fallbackIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            if (fallbackIntent.resolveActivity(getPackageManager()) != null) {
                                dialogUI.dismiss();
                                loadSettings();
                                startActivity(fallbackIntent);
                            } else {
                                loadSettings();
                                // Если и это не сработало, сообщаем пользователю
                                Toast.makeText(getApplicationContext(), "Не удалось открыть настройки сети", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        loadSettings();
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            // Добавляем диалог в mainScreen с анимацией появления
            dialogUI.showIn(mainScreen);*/
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://crmp.pro/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Interface sInterface = retrofit.create(Interface.class);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1) // 3600 (published)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    apiLink = mFirebaseRemoteConfig.getString("apiSecretS");
                } else {
                    Log.e("Google FireBase", "SLIHILAC HOPA");
                    Exception e = task.getException(); // Получаем исключение
                    if (e != null) {
                        Log.e("Google FireBase", "Error fetching data from Firebase:", e);
                    } else {
                        Log.e("Google FireBase", "Unknown error occurred while fetching data.");
                    }
                }



                sInterface.getApi(apiLink).enqueue(new Callback<Api>() {
                    public void onResponse(Call<Api> call, Response<Api> response) {
                        if(response.isSuccessful())
                        {
                            if(response.body() != null) {
                                if(response.body().getLauncherVersion() != 73) {
                                    /*dialogUI = new DialogUI(
                                            MainScreenActivity.getInstance(),
                                            "Да",
                                            "Нет",
                                            "Доступна новая версия клиента!\nЗагрузить обновление?", // Заголовок
                                            "" // Описание
                                    );
                                    // Устанавливаем действие для Yes кнопки (с анимацией)
                                    dialogUI.setOnClickYesBtn(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialogUI.dismiss();
                                            startDownloadApk(response.body().getLauncherUrl(),
                                                    response.body().getLauncherPath(),
                                                    response.body().getLauncherName());
                                        }
                                    });
                                    dialogUI.setOnClickNoBtn(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialogUI.dismiss();
                                            loadSettings();
                                        }
                                    });
                                    dialogUI.showIn(mainScreen);*/
                                } else {
                                    if (response.body().getIsTest()) {
                                        if (!response.body().getTestApi()) {
                                            /*dialogUI = new DialogUI(
                                                    MainScreenActivity.getInstance(),
                                                    "Да",
                                                    "Нет",
                                                    "Тестовая версия клиента закрыта!\nОжидайте следующих тестов...", // Заголовок
                                                    "" // Описание
                                            );
                                            // Устанавливаем действие для Yes кнопки (с анимацией)
                                            dialogUI.setOnClickYesBtn(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                    onDestroy();
                                                }
                                            });
                                            dialogUI.showIn(mainScreen);*/
                                        }
                                        //testApi = response.body().getTestApi();
                                    }
                                    Lists.archives.clear();
                                    Lists.archives.addAll(response.body().getArchives());
                                    Lists.deleted.clear();
                                    Lists.deleted.addAll(response.body().getDeleted());

                                    Lists.launcher_dan = new String[]{response.body().getLauncherUrl(), response.body().getLauncherPath(), response.body().getLauncherName()};

                                    sInterface.getMain(response.body().getApi()).enqueue(new Callback<Main>() {
                                        @Override
                                        public void onResponse(Call<Main> call, Response<Main> response) {

                                            String storiesLink = response.body().getStories();

                                            String faqLink = response.body().getFaq();

                                            Lists.createCharacterUrl = response.body().getCreateCharacter();

                                            Lists.verifyAuthUrl = response.body().getVerifyAuth();

                                            Lists.accountDetailsUrl = response.body().getAccountDetails();

                                            Lists.isAccUrl = response.body().getIsAcc();

                                            Lists.skinsCDNUrl = response.body().getSkinsCDN();

                                            sInterface.getServers(response.body().getServers()).enqueue(new Callback<List<Servers>>() {
                                                @Override
                                                public void onResponse(Call<List<Servers>> call, Response<List<Servers>> response) {

                                                    List<Servers> servers = response.body();
                                                    for (Servers server : servers) {
                                                        Lists.slist.add(new Servers(server.getName(), server.getColor(), server.getStatus(), server.getRecommend(), server.getNewStatus(), server.getEdgarHost(), server.getEdgarPort(), server.getId()));
                                                    }

                                                    ArrayList<Servers> serversItem = Lists.slist;
                                                    ArrayList<Servers> serversrec = new ArrayList<>();
                                                    ArrayList<Servers> serversnew = new ArrayList<>();
                                                    ArrayList<Servers> serversbce = new ArrayList<>();
                                                    ArrayList<Servers> serverss = new ArrayList<>();

                                                    boolean s = false;
                                                    boolean n = false;
                                                    int i;

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        //Log.e("edgar", "1 id = " + i);
                                                        if (!serversss.getRecommend()) {
                                                            //Log.e("edgar", "1 id = " + i);
                                                            //Log.e("edgar", "recommend = false");
                                                            serversbce.add(serversss);
                                                        }
                                                        //serversItem.remove(i);
                                                    }

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        //Log.e("edgar", "2 id = " + i);
                                                        if (!serversss.getNewStatus() && serversss.getRecommend()) {
                                                            if (!s) {
                                                                //Log.e("edgar", "2 id = " + i);
                                                                //Log.e("edgar", "recommend = true, NewStatus = false (rec)");
                                                                serversrec.add(serversss);
                                                                serversItem.remove(i);
                                                                s = true;
                                                                i--;
                                                            } else {
                                                                serversbce.add(serversss);
                                                                //Log.e("edgar", "recommend = false");
                                                            }
                                                        }
                                                    }

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        //Log.e("edgar", "3 id = " + i);
                                                        if (serversss.getNewStatus() && serversss.getRecommend()) {
                                                            if (!n) {
                                                                //Log.e("edgar", "3 id = " + i);
                                                                //Log.e("edgar", "recommend = true, NewStatus = true (new)");
                                                                serversnew.add(serversss);
                                                                serversItem.remove(i);
                                                                n = true;
                                                                i--;
                                                            } else {
                                                                serversbce.add(serversss);
                                                                //Log.e("edgar", "recommend = false");
                                                            }
                                                        }
                                                    }
                                                    if (serversrec.size() >= 1) {
                                                        serverss.addAll(serversrec);
                                                        //Log.e("edgar", "serversrec.size() > " + serversrec.size());

                                                    }
                                                    if (serversnew.size() >= 1) {
                                                        serverss.addAll(serversnew);
                                                    }
                                                    serverss.addAll(serversbce);
                                                    //Log.e("edgar", "serversItem.3 > " + serverss.size());
                                                    Lists.slist = serverss;

                                                    sInterface.getStories(storiesLink).enqueue(new Callback<List<News>>() {
                                                        @Override
                                                        public void onResponse(Call<List<News>> call, Response<List<News>> response) {

                                                            Servers item = (Servers) Lists.slist.get(0);

                                                            EdgarConectV2.host = item.getEdgarHost();
                                                            EdgarConectV2.port = item.getEdgarPort();

                                                            List<News> news = response.body();

                                                            for (News storie : news) {
                                                                Lists.nlist.add(new News(storie.getImageUrl(), storie.getTitle(), storie.getTitleBig(), storie.getUrl(), storie.getImageFullUrl()));
                                                            }

                                                            sInterface.getFaqList(faqLink).enqueue(new Callback<FaqList>() {
                                                                public void onFailure(Call<FaqList> call, Throwable th) {
                                                                    Toast.makeText(getApplicationContext(), "Ошибка FAQ List", Toast.LENGTH_SHORT).show();
                                                                }

                                                                public void onResponse(Call<FaqList> call, Response<FaqList> response) {
                                                                    if (response.body() != null) {
                                                                        Lists.faqlist.clear();
                                                                        Lists.faqlist.addAll(response.body().getArray());
                                                                    }

                                                                    List<Archive> archiveList = Lists.archives;
                                                                    List<Deleted> deletedList = Lists.deleted;

                                                                    List<String> path = new ArrayList<>();
                                                                    List<String> unZip = new ArrayList<>();
                                                                    List<String> toUnZip = new ArrayList<>();
                                                                    List<String> url = new ArrayList<>();
                                                                    long si = 0;

                                                                    for (int i = 0; deletedList.size() > i; i++) {
                                                                        Deleted deleted = deletedList.get(i);
                                                                        File f = new File(deleted.getPath());
                                                                        if (f.exists()) {
                                                                            if (f.isDirectory()) {
                                                                                deleteDirectory(f);
                                                                            } else if (f.isFile()) {
                                                                                f.delete();
                                                                            }
                                                                        }
                                                                    }

                                                                    for (int i = 0; archiveList.size() > i; i++) {
                                                                        Archive archive = archiveList.get(i);
                                                                        long size = 0;
                                                                        for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                                                                            ArchivePath archivePaths = archive.getPaths().get(i1);
                                                                            size = size + getFileOrDirectorySize(archivePaths.getPath());
                                                                            System.out.println(size);
                                                                        }
                                                                        System.out.println(size + " вес локал");
                                                                        if (archive.getSize() == size) {
                                                                            System.out.println(archive.getSize() + " == " + size);
                                                                            System.out.println("Все ровно");
                                                                        } else {
                                                                            for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                                                                                ArchivePath archivePaths = archive.getPaths().get(i1);
                                                                                path.add(archivePaths.getPath());
                                                                            }
                                                                            toUnZip.add(archive.getZip_path());
                                                                            unZip.add(archive.getType());
                                                                            url.add(archive.getUrls());
                                                                            si = si + archive.getSize();
                                                                            System.out.println(si);
                                                                        }
                                                                    }
                                                                    FirebaseDatabase.getInstance().getReference().child("Users").child("User-servers").child("Server_0").child(FirebaseAuth.getInstance().getUid()).child("nick").addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            Log.e("edgar", "pon" + snapshot.getValue(String.class));
                                                                            if (snapshot.getValue(String.class) == null) {
                                                                                HashMap<String, String> serversInfo = new HashMap<>();
                                                                                serversInfo.put("nick", "ERYHB_hjdcb");
                                                                                FirebaseDatabase.getInstance().getReference().child("Users").child("User-servers").child("Server_0").child(FirebaseAuth.getInstance().getUid()).setValue(serversInfo);
                                                                            } else {
                                                                                nickName = snapshot.getValue(String.class);

                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });

                                                                    clearModelCache();
                                                                    if (!url.isEmpty()) {
                                                                        /*dialogUI = new DialogUI(
                                                                                MainScreenActivity.getInstance(),
                                                                                "Да",
                                                                                "Нет",
                                                                                "Доступно обновление!\nЗагрузить " + Utils.bytesIntoHumanReadable(si) + "?", // Заголовок
                                                                                "" // Описание
                                                                        );
                                                                        // Устанавливаем действие для Yes кнопки
                                                                        dialogUI.setOnClickYesBtn(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                dialogUI.dismiss();
                                                                                startDownload(url, path, unZip, toUnZip);
                                                                            }
                                                                        });
                                                                        // Устанавливаем действие для No кнопки
                                                                        dialogUI.setOnClickNoBtn(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                dialogUI.dismiss();
                                                                                loadSettings();
                                                                            }
                                                                        });
                                                                        dialogUI.showIn(mainScreen);*/
                                                                    } else {
                                                                        Intent intent = new Intent(MainScreenActivity.getInstance(), SAMP.class);
                                                                        startActivity(intent);
                                                                        overridePendingTransition(0, 0);// Установка анимации перехода в 0*
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onFailure(Call<List<News>> call, Throwable t) {
                                                            Toast.makeText(getApplicationContext(), "Ошибка News List", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Call<List<Servers>> call, Throwable t) {
                                                    Toast.makeText(getApplicationContext(), "Ошибка Servers List", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }

                                        @Override
                                        public void onFailure(Call<Main> call, Throwable t) {
                                            Toast.makeText(getApplicationContext(), "Ошибка Main List", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Log.e("api-", "api----");
                                /*dialogUI = new DialogUI(
                                        MainScreenActivity.getInstance(),
                                        "Повторить",
                                        "Не удаётся установить\nсоединение с сервером!", // Заголовок
                                        "Повторите попытку позже." // Описание
                                );
                                // Устанавливаем действие для Yes кнопки (с анимацией)
                                dialogUI.setOnClickYesBtn(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialogUI.dismiss();
                                        loadSettings();
                                    }
                                });
                                dialogUI.showIn(mainScreen);*/
                            }
                        } else {
                            System.out.println(response.body());
                            Log.e("api-", "api---1-");
                            System.err.println("Ошибка: " + response.code() + " - " + response.message());
                            /*dialogUI = new DialogUI(
                                    MainScreenActivity.getInstance(),
                                    "Повторить",
                                    "Не удаётся установить\nсоединение с сервером!", // Заголовок
                                    "Повторите попытку позже." // Описание
                            );
                            // Устанавливаем действие для Yes кнопки (с анимацией)
                            dialogUI.setOnClickYesBtn(new Runnable() {
                                @Override
                                public void run() {
                                    dialogUI.dismiss();
                                    loadSettings();
                                }
                            });
                            dialogUI.showIn(mainScreen);*/
                        }
                    }
                    public void onFailure(Call<Api> call, Throwable th) {
                        Log.e("api-", "api----" + th.toString());
                        /*dialogUI = new DialogUI(
                                MainScreenActivity.getInstance(),
                                "Повторить",
                                "Не удаётся установить\nсоединение с сервером!", // Заголовок
                                "Повторите попытку позже." // Описание
                        );
                        // Устанавливаем действие для Yes кнопки (с анимацией)
                        dialogUI.setOnClickYesBtn(new Runnable() {
                            @Override
                            public void run() {
                                dialogUI.dismiss();
                                loadSettings();
                            }
                        });
                        dialogUI.showIn(mainScreen);*/
                    }
                });
            }
        });
    }

    public List<String> urlsst;
    public List<String> toUnnZip;
    public List<String> unnZip;
    public String launcher_path;

    int i = 0;
    public void startDownload(List<String> url, List<String> path, List<String> unZip, List<String> toUnZip) {
        File directory = new File(Helper.androidPath);
        // Проверка существования директории "/storage/emulated/0/Edgar"
        if (!directory.exists() || !directory.isDirectory()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Директория " + Helper.androidPath + " успешно создана.");
            } else {
                System.out.println("Ошибка при создании директории " + Helper.androidPath);
                return; // Прерываем выполнение, если директория не создана
            }
        }

        for (int i1 = 0; path.size() > i1; i1++) {
            String pat = path.get(i1);
            File f = new File(pat);
            if (f.exists()) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else if (f.isFile()) {
                    f.delete();
                    System.out.println("Файл успешно удален.");
                } else {
                    System.out.println("Не удалось определить файл или директорию.");
                }
            } else {
                System.out.println("Указанный путь не существует.");
            }
        }
        i = 0;
        urlsst = url;
        toUnnZip = toUnZip;
        unnZip = unZip;
        String pathh = unZip.get(i);
        String urlss = url.get(i);
        System.out.println(urlss);
        System.out.println(pathh);
        createDownloadTask(urlss, pathh, false).start();
        i++;
    }
    public void startDownloadApk(String url, String path, String name) {

        launcher_path = path;
        new File(launcher_path).delete();

        String pathDownload = path.replace(name, "");

        createDownloadTask(url, pathDownload, true).start();
    }

    boolean apkIn = true;
    boolean once = false;
    private void installApk(String launcher_path) {
        try {
            File file = new File(launcher_path);
            Intent intent;
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkUri = FileProvider.getUriForFile(MainScreenActivity.getInstance(), "ru.edgar.matrp" + ".provider", file);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(apkUri);
                } else {
                    Uri apkUri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                apkIn = false;
                startActivity(intent);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if(!apkIn) {
                if(!once) {
                    apkIn = true;
                    once = true;
                    installApk(launcher_path);
                }
            }
        }
    }

    private BaseDownloadTask createDownloadTask(String url, String path, boolean isApk) {
        return FileDownloader.getImpl().create(url)
                .setPath(path, true)
                .setCallbackProgressTimes(100)
                .setMinIntervalUpdateSpeed(100)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);

                        if (!isApk) {
                            statusBar.setText(getResources().getString(R.string.launcher_donwload_info_6, soFarBytes * 100.0f / totalBytes));
                        } else {
                            statusBar.setText(getResources().getString(R.string.launcher_donwload_info_10, soFarBytes * 100.0f / totalBytes));
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        System.out.println(e.toString() + " XUIIIIIIIIII");
                        Toast.makeText(getApplicationContext(), "Произошла ошибка начните заново установку", Toast.LENGTH_SHORT).show();
                        statusBar.setText(getResources().getString(R.string.launcher_donwload_info_5));
                        loadSettings();
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String et, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, et, isContinue, soFarBytes, totalBytes);
                        System.out.println("dddddddd");
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        if (!isApk) {
                            System.out.println(urlsst.size() + " = " + i);
                            if (urlsst.size() > i) {
                                String pathh = unnZip.get(i);
                                String urlss = urlsst.get(i);
                                System.out.println(urlss);
                                createDownloadTask(urlss, pathh, false).start();
                                i++;
                            } else {
                                i = 0;
                                if (toUnnZip.size() > i) {
                                    String unn = toUnnZip.get(i);
                                    String unn2 = unnZip.get(i);
                                    unZip(unn, unn2);
                                    i++;
                                }
                            }
                        } else {
                            installApk(launcher_path);
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                    }
                });
    }

    private static void setPermissions(String basePath, Set<PosixFilePermission> permissions, boolean isDirectory) throws IOException {
        Path path = Paths.get(basePath);
        Files.walk(path)
                .filter(p -> isDirectory ? Files.isDirectory(p) : Files.isRegularFile(p))
                .forEach(p -> {
                    try {
                        Files.setPosixFilePermissions(p, permissions);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void unZip(String path, String path2) {
        String mInputFilePath = path;
        String mOutputPath = path2;
        String resultString = path.replace(Helper.androidPath + "/", "");
        statusBar.setText("Распаковка архивов " + resultString);
        new Thread() {
            @Override
            public void run() {
                //int i11 = P7ZipApi.executeCommand(String.format("7z x '%s' '-o%s' -aoa", mInputFilePath, mOutputPath));
                //System.out.println(i11);//1
                try {
                    new ZipFile(new File(mInputFilePath)).extractAll(mOutputPath);
                    Utils.delete(new File(path));
                    Utils.delete(new File(path + ".temp"));
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    if (toUnnZip.size() > i) {
                        String unn = toUnnZip.get(i);
                        String unn2 = unnZip.get(i);
                        unZip(unn, unn2);
                        i++;
                    } else {
                        statusBar.setText(getResources().getString(R.string.launcher_donwload_info_5));

                        String basePath = Helper.androidPath;

                        // Создаем набор разрешений для папок (drwx)
                        Set<PosixFilePermission> folderPermissions = new HashSet<>();
                        folderPermissions.add(PosixFilePermission.OWNER_READ);
                        folderPermissions.add(PosixFilePermission.OWNER_WRITE);
                        folderPermissions.add(PosixFilePermission.OWNER_EXECUTE);

                        // Создаем набор разрешений для файлов (rw)
                        Set<PosixFilePermission> filePermissions = new HashSet<>();
                        filePermissions.add(PosixFilePermission.OWNER_READ);
                        filePermissions.add(PosixFilePermission.OWNER_WRITE);

                        try {
                            // Устанавливаем разрешения для папок
                            setPermissions(basePath, folderPermissions, true);
                            /*setPermissions(basePath + "/texdb", folderPermissions, true);
                            setPermissions(basePath + "/anim", folderPermissions, true);
                            setPermissions(basePath + "/SPACE", folderPermissions, true);
                            setPermissions(basePath + "/data", folderPermissions, true);
                            setPermissions(basePath + "/audio", folderPermissions, true);
                            setPermissions(basePath + "/fonts", folderPermissions, true);
                            setPermissions(basePath + "/Text", folderPermissions, true);
                            setPermissions(basePath + "/Textures", folderPermissions, true);
                            setPermissions(basePath + "/images", folderPermissions, true);*/

                            // Устанавливаем разрешения для файлов
                            setPermissions(basePath, filePermissions, false);
                            /*setPermissions(basePath + "/texdb", filePermissions, false);
                            setPermissions(basePath + "/SPACE", filePermissions, false);
                            setPermissions(basePath + "/images", filePermissions, false);
                            setPermissions(basePath + "/data", filePermissions, false);
                            setPermissions(basePath + "/anim", filePermissions, false);
                            setPermissions(basePath + "/Textures", filePermissions, false);
                            setPermissions(basePath + "/audio", filePermissions, false);
                            setPermissions(basePath + "/fonts", filePermissions, false);
                            setPermissions(basePath + "/Text", filePermissions, false);*/

                            System.out.println("Разрешения установлены успешно.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        clearModelCache();
                        Intent intent = new Intent(MainScreenActivity.getInstance(), SAMP.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0); // Установка анимации перехода в 0*
                        //checkGameFile(path, 0);
                    }
                });
            }
        }.start();
    }

    public static void clearModelCache() {
        try {
            File file = new File(MainScreenActivity.getInstance().getExternalFilesDir(null).toString() + "/CINFO.BIN");
            if (file.exists()) {
                file.delete();
            }

            file = new File(MainScreenActivity.getInstance().getExternalFilesDir(null).toString() + "/models/MINFO.BIN");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if(currentUser != null) {
                                        isAuth = true;
                                    } else {
                                        isAuth = false;
                                    }
                                    onRequestPermissions();
                                } else {
                                   /*dialogUI = new DialogUI(
                                            MainScreenActivity.getInstance(),
                                            "Понятно",
                                            "Ошибка авторизации через Google!", // Заголовок
                                            "Попробуйте ещё раз." // Описание
                                    );
                                    // Устанавливаем действие для Yes кнопки (с анимацией)
                                    dialogUI.setOnClickYesBtn(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialogUI.dismiss();
                                            FirebaseUser currentUser = mAuth.getCurrentUser();
                                            if(currentUser != null) {
                                                isAuth = true;
                                            } else {
                                                isAuth = false;
                                            }
                                            if(!isAuth) {
                                                onInitAuthGoogle();
                                            } else onRequestPermissions();
                                        }
                                    });
                                    dialogUI.showIn(mainScreen);*/
                                }

                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
                Log.e("GOOGLE AUTH", "Error - " + e.getMessage());
                /*dialogUI = new DialogUI(
                        MainScreenActivity.getInstance(),
                        "Понятно",
                        "Ошибка авторизации через Google!", // Заголовок
                        "Попробуйте ещё раз." // Описание
                );
                // Устанавливаем действие для Yes кнопки (с анимацией)
                dialogUI.setOnClickYesBtn(new Runnable() {
                    @Override
                    public void run() {
                        dialogUI.dismiss();
                        loadSettings();
                    }
                });
                dialogUI.showIn(mainScreen);*/
            }
        }
    }
}
