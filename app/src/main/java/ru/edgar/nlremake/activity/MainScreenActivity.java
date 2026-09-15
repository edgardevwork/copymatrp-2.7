package ru.edgar.nlremake.activity;

import android.animation.ArgbEvaluator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.vk.id.VKID;
import java.util.HashMap;
import java.util.List;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.nlremake.loader.LauncherLoader;
import ru.edgar.nlremake.other.Utils;
import ru.edgar.nlremake.service.DownloadService;
import ru.edgar.nlremake.ui.FullHeightVideoView;
import ru.edgar.nlremake.utils.CacheChecker;
import ru.edgar.nlremake.utils.FileUtils;
import ru.edgar.matrp.R;
import ru.edgar.space.SAMP;
import ru.edgar.space.UiManager;

public class MainScreenActivity extends AppCompatActivity {
    private UiManager uiManager = null;
    private NotificationManager notifManager = null;
    private FullHeightVideoView mVideoView;
    private DialogManager dialogManager;
    private ImageView lm_loadicon;
    private LinearLayout loading, downloadBar;
    private static MainScreenActivity instance;
    private FrameLayout mainScreen;
    private TextView procent, progress_text, dw_status;
    private ProgressBar progress;
    private DownloadService downloadService;
    private DownloadService.DownloadCallback downloadCallback;
    private LauncherLoader launcherLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Launcher);
        setContentView(R.layout.activity_mainscreen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initializeOtherService();

        instance = this;
        hideUI();

        loading = (LinearLayout) findViewById(R.id.loading);
        downloadBar = (LinearLayout) findViewById(R.id.downloadBar);
        lm_loadicon = (ImageView) findViewById(R.id.lm_loadicon);
        lm_loadicon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_animation));
        procent = (TextView) findViewById(R.id.procent);
        dw_status = (TextView) findViewById(R.id.dw_status);
        progress_text = (TextView) findViewById(R.id.progress_text);
        progress = (ProgressBar) findViewById(R.id.progress);
        mainScreen = (FrameLayout) findViewById(R.id.mainscreen);
        mVideoView = (FullHeightVideoView) findViewById(R.id.videoView);

        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);
        for (int i10 = 0; i10 < 120; i10++) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setGradientRadius(getResources().getDimensionPixelSize(R.dimen._128sdp));
            if (i10 < 40) {
                float f10 = i10 * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f10, -1119283065, -1283248909)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f10, -1119283065, -1119283065)).intValue()});
            } else if (i10 < 80) {
                float f11 = (i10 - 40) * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f11, -1283248909, -1119283065)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f11, -1119283065, -1283248909)).intValue()});
            } else {
                float f12 = (i10 - 80) * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f12, -1119283065, -1119283065)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f12, -1283248909, -1119283065)).intValue()});
            }
            gradientDrawable.setCornerRadius(getResources().getDimensionPixelSize(R.dimen._4sdp));
            //gradientDrawable.setStroke(getResources().getDimensionPixelSize(R.dimen._1sdp), -8637479);
            animationDrawable.addFrame(gradientDrawable, 16);
        }

        progress.setIndeterminateDrawable(animationDrawable);
        progress.setIndeterminate(false);

        loading.setVisibility(View.VISIBLE);
        downloadBar.setVisibility(View.GONE);

        //VideoUtils.setupVideoPlayer(mVideoView, this.getPackageName());

        uiManager = new UiManager(this);
        dialogManager = uiManager.getTyped(UiManager.DIALOG);

        setupDownloadCallback();
        setupLauncherLoader();

        launcherLoader.load(LauncherLoader.LaunchMode.WITH_GAME_LOADING);
    }

    private void setupDownloadCallback() {
        downloadCallback = new DownloadService.DownloadCallback() {
            @Override
            public void onProgress(int percent, String text) {
                runOnUiThread(() -> {
                    if (percent >= 0) {
                        progress.setProgress(percent);
                        procent.setText(percent + "%");
                        progress_text.setText(text);
                    } else {
                        dw_status.setText("Распаковка архивов");
                        procent.setText(text);
                    }
                });
            }

            @Override
            public void onDownloadStarted() {
                runOnUiThread(() -> {
                    if (downloadBar.getVisibility() == View.GONE) {
                        loading.setVisibility(View.GONE);
                        downloadBar.setVisibility(View.VISIBLE);
                    }
                    dw_status.setText("Загружено файлов");
                });
            }

            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    dw_status.setText(getResources().getString(R.string.launcher_donwload_info_5));
                    checkGameCache();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    dialogManager.showErrorDialog("Произошла ошибка начните заново установку!", null, "Повторить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialogManager.getIsChecked()) {
                                CrashReporter.sendBugReport(MainScreenActivity.getInstance(), AppConfig.mAuth.getUid(), "DownloadService - error()", error);
                            }
                            dialogManager.hideDialog();
                            launcherLoader.load(LauncherLoader.LaunchMode.WITH_GAME_LOADING);
                        }
                    }, true, "Сообщить об ошибке");
                });
            }

            @Override
            public void onApkReady(String path) {
                runOnUiThread(() -> {
                    if (downloadService != null) {
                        downloadService.installApk(path);
                    }
                });
            }
        };
    }

    private void setupLauncherLoader() {
        launcherLoader = new LauncherLoader(this, dialogManager);
        launcherLoader.setCallback(new LauncherLoader.LauncherLoadCallback() {
            @Override
            public void onLoaded(LauncherLoader.LaunchMode mode) {
                if (mode == LauncherLoader.LaunchMode.WITH_GAME_LOADING) {
                    onRequestPermissions();
                }
            }

            @Override
            public void onAuthRequired() {
                dialogManager.showAuthDialog(true);
            }

            @Override
            public void onError(String error) {
                // Обработка ошибки
            }

            @Override
            public void onUpdateRequired(String url, String path, String name) {
                startDownloadApk(url, path, name);
            }

            @Override
            public void onTestClosed() {
                finish();
                onDestroy();
            }
        });
    }

    public static MainScreenActivity getInstance() {
        return instance;
    }

    public FrameLayout getMainScreen() {
        return mainScreen;
    }

    private void hideUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void initializeOtherService() {
        getExternalFilesDir("");
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
        FirebaseAnalytics.getInstance(this);
        FirebaseApp.initializeApp(this);
        try {
            VKID.Companion.init(this);
        } catch (IllegalStateException e) {
            Log.w("VKID_INIT", "VKID уже был инициализирован ранее");
        }
        AppConfig.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = AppConfig.mAuth.getCurrentUser();
        if(currentUser != null) {
            AppConfig.isAuth = true;
        } else {
            AppConfig.isAuth = false;
        }
    }

    public void onRequestPermissions() {
        List<String> permissionsToRequest = FileUtils.getPermissionsToRequest(this);
        if (!permissionsToRequest.isEmpty()) {
            requestPermissions(permissionsToRequest.toArray(new String[0]), 1);
        } else {
            checkGameCache();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean perfect = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    perfect = false;
                    break;
                }
            }
            if (perfect) {
                checkGameCache();
            } else {
                Toast.makeText(this, "Нужны все разрешения для работы приложения", Toast.LENGTH_SHORT).show();
                onRequestPermissions();
            }
        }
    }

    public void checkGameCache() {
        downloadBar.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        CacheChecker cacheChecker = new CacheChecker(this, AppConfig.archives, AppConfig.deleted);
        CacheChecker.CacheCheckResult result = cacheChecker.checkCache();

        FirebaseDatabase.getInstance().getReference().child("Users").child("User-servers").child("Server_0").child(FirebaseAuth.getInstance().getUid()).child("nick").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("edgar", "pon" + snapshot.getValue(String.class));
                if (snapshot.getValue(String.class) == null) {
                    HashMap<String, String> serversInfo = new HashMap<>();
                    serversInfo.put("nick", "ERYHB_hjdcb");// TODO: Null исправить ник! когда буду делать регу
                    FirebaseDatabase.getInstance().getReference().child("Users").child("User-servers").child("Server_0").child(FirebaseAuth.getInstance().getUid()).setValue(serversInfo);
                } else {
                    AppConfig.nickName = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (result.needsDownload()) {
            long finalSi = result.totalSize;
            dialogManager.showDialog("Доступно обновление!", "Размер обновления " + Utils.bytesIntoHumanReadable(result.totalSize) + ".\nХочешь скачать его сейчас?", "Да", "Нет", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogManager.hideDialog();
                    startDownload(result.url, result.path, result.unZip, result.toUnZip);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogManager.hideDialog();
                    dialogManager.showDialog("Предупреждение", "Чтобы продолжить игру, загрузи, пожалуйста,\nдополнительные файлы: они содержат музыку, уровни,\nграфику и прочий важный контент.\nНеобходимо скачать: " + Utils.bytesIntoHumanReadable(finalSi) + "\nЕсли выберешь «Позже», приложение закроется, и ты\nсможешь скачивать все необходимое в любое удобное\nвремя.\nБлагодарим за понимание!", "Скачать", "Позже", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogManager.hideDialog();
                            startDownload(result.url, result.path, result.unZip, result.toUnZip);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            onDestroy();
                        }
                    });
                }
            });
        } else {
            loading.setVisibility(View.VISIBLE);
            downloadBar.setVisibility(View.GONE);
            progress_text.setVisibility(View.VISIBLE);
            AppConfig.isStartGame = true;
            Intent intent = new Intent(MainScreenActivity.getInstance(), SAMP.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    public void startDownload(List<String> url, List<String> path, List<String> unZip, List<String> toUnZip) {
        downloadService = new DownloadService(this);
        downloadService.setCallback(downloadCallback);
        downloadService.startGameDownload(url, path, unZip, toUnZip);
    }

    public void startDownloadApk(String url, String path, String name) {
        downloadService = new DownloadService(this);
        downloadService.setCallback(downloadCallback);
        downloadService.startApkDownload(url, path, name);
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
                                    dialogManager.hideAuthDialog();
                                    FirebaseUser currentUser = AppConfig.mAuth.getCurrentUser();
                                    if(currentUser != null) {
                                        AppConfig.isAuth = true;
                                    } else {
                                        AppConfig.isAuth = false;
                                    }
                                    HashMap<String, Object> Info = new HashMap<>();
                                    Info.put("google-email", AppConfig.mAuth.getCurrentUser().getEmail());
                                    Info.put("way", 2);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Info);
                                    onRequestPermissions();
                                } else {
                                    dialogManager.hideAuthDialog();
                                    dialogManager.showErrorDialog("Ошибка авторизации через Google!", "Попробуйте ещё раз.", "Понятно", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(dialogManager.getIsChecked()) {
                                                CrashReporter.sendBugReport(MainScreenActivity.getInstance(), AppConfig.mAuth.getUid(), "signInWithCredential()", task.getException().toString());
                                            }
                                            dialogManager.hideDialog();
                                            FirebaseUser currentUser = AppConfig.mAuth.getCurrentUser();
                                            if(currentUser != null) {
                                                AppConfig.isAuth = true;
                                            } else {
                                                AppConfig.isAuth = false;
                                            }
                                            if(!AppConfig.isAuth) {
                                                dialogManager.hideDialog();
                                                dialogManager.showAuthDialog(false);
                                            } else onRequestPermissions();
                                        }
                                    }, true, "Сообщить об ошибке");
                                }
                            }
                        });
            } catch (ApiException e) {
            }
        }
    }
}