package ru.edgar.nlremake.loader;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.model.Api;
import ru.edgar.nlremake.model.Main;
import ru.edgar.nlremake.model.Servers;
import ru.edgar.nlremake.model.Stories;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.nlremake.network.Interface;
import ru.edgar.nlremake.utils.NetworkUtils;
import ru.edgar.space.EdgarConectV2;

public class GameStarter {
    private Context context;
    private DialogManager dialogManager;
    private String apiLink;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private StartCallback callback;

    public interface StartCallback {
        void onHideSplash();
        void onAuthRequired();
        void onTestClosed();
        void onError(String error);
    }

    public GameStarter(Context context, DialogManager dialogManager) {
        this.context = context;
        this.dialogManager = dialogManager;
    }

    public void setCallback(StartCallback callback) {
        this.callback = callback;
    }

    public void startGame() {
        if(!NetworkUtils.isNetworkAvailable(context)) {
            dialogManager.showDialog("Чтобы продолжить\nподключитесь к интернету!", "Подключиться к интернету и продолжить играть", "Подключиться", null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(NetworkUtils.isNetworkAvailable(context)) {
                        dialogManager.hideDialog();
                        startGame();
                        return;
                    }
                    try {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            dialogManager.hideDialog();
                            startGame();
                            context.startActivity(intent);
                        } else {
                            Intent fallbackIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            if (fallbackIntent.resolveActivity(context.getPackageManager()) != null) {
                                dialogManager.hideDialog();
                                startGame();
                                context.startActivity(fallbackIntent);
                            } else {
                                dialogManager.hideDialog();
                                startGame();
                                android.widget.Toast.makeText(context, "Не удалось открыть настройки сети", android.widget.Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        startGame();
                        e.printStackTrace();
                        android.widget.Toast.makeText(context, "Ошибка: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            }, null);
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://google.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Interface sInterface = retrofit.create(Interface.class);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    apiLink = mFirebaseRemoteConfig.getString("apiNL1");
                } else {
                    Log.e("Google FireBase", "SLIHILAC HOPA");
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("Google FireBase", "Error fetching data from Firebase:", e);
                    } else {
                        Log.e("Google FireBase", "Unknown error occurred while fetching data.");
                    }
                }

                sInterface.getApi(apiLink).enqueue(new Callback<Api>() {
                    public void onResponse(Call<Api> call, Response<Api> response) {
                        if(response.isSuccessful()) {
                            if(response.body() != null) {
                                if(response.body().getLauncherVersion() != 76) {
                                    dialogManager.showDialog("Доступна новая\nверсия клиента!", "Скачать обновление и продолжить играть", "Скачать обновление", "Отмена", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogManager.hideDialog();
                                            if(callback != null) {
                                                callback.onError("Требуется обновление клиента");
                                            }
                                        }
                                    }, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogManager.hideDialog();
                                            startGame();
                                        }
                                    });
                                } else {
                                    if (response.body().getIsTest()) {
                                        if (!response.body().getTestApi()) {
                                            dialogManager.showDialog("Тестовая версия\nклиента закрыта!", "Ожидайте следующих тестов...", "Понял", null, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(callback != null) {
                                                        callback.onTestClosed();
                                                    }
                                                }
                                            }, null);
                                            return;
                                        }
                                        AppConfig.testApi = response.body().getIsTest();
                                    }
                                    AppConfig.apiLink = response.body().getApiLink();
                                    AppConfig.archives.clear();
                                    AppConfig.archives.addAll(response.body().getArchives());
                                    AppConfig.deleted.clear();
                                    AppConfig.deleted.addAll(response.body().getDeleted());
                                    AppConfig.launcher_dan = new String[]{response.body().getLauncherUrl(), response.body().getLauncherPath(), response.body().getLauncherName()};

                                    sInterface.getMain(response.body().getApi()).enqueue(new Callback<Main>() {
                                        @Override
                                        public void onResponse(Call<Main> call, Response<Main> response) {
                                            String storiesLink = response.body().getStories();
                                            AppConfig.verifyAuthUrl = response.body().getVerifyAuth();
                                            AppConfig.resetPassword = response.body().getResetPassword();
                                            AppConfig.characterUrl = response.body().getCharacter();
                                            AppConfig.accountDetailsUrl = response.body().getAccountDetails();
                                            AppConfig.isAccUrl = response.body().getIsAcc();
                                            AppConfig.skinsCDNUrl = response.body().getSkinsCDN();
                                            AppConfig.crashReportUrl = response.body().getCrashReport();
                                            AppConfig.deleteAcc = response.body().getDeleteAcc();

                                            sInterface.getServers(response.body().getServers()).enqueue(new Callback<List<Servers>>() {
                                                @Override
                                                public void onResponse(Call<List<Servers>> call, Response<List<Servers>> response) {
                                                    List<Servers> servers = response.body();
                                                    for (Servers server : servers) {
                                                        AppConfig.serverList.add(new Servers(
                                                                server.getName(),
                                                                server.getColor(),
                                                                server.getStatus(),
                                                                server.getRecommend(),
                                                                server.getNewStatus(),
                                                                server.getEdgarHost(),
                                                                server.getEdgarPort(),
                                                                server.getId()
                                                        ));
                                                    }

                                                    ArrayList<Servers> serversItem = AppConfig.serverList;
                                                    ArrayList<Servers> serversrec = new ArrayList<>();
                                                    ArrayList<Servers> serversnew = new ArrayList<>();
                                                    ArrayList<Servers> serversbce = new ArrayList<>();
                                                    ArrayList<Servers> serverss = new ArrayList<>();

                                                    boolean s = false;
                                                    boolean n = false;
                                                    int i;

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        if (!serversss.getRecommend()) {
                                                            serversbce.add(serversss);
                                                        }
                                                    }

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        if (!serversss.getNewStatus() && serversss.getRecommend()) {
                                                            if (!s) {
                                                                serversrec.add(serversss);
                                                                serversItem.remove(i);
                                                                s = true;
                                                                i--;
                                                            } else {
                                                                serversbce.add(serversss);
                                                            }
                                                        }
                                                    }

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        if (serversss.getNewStatus() && serversss.getRecommend()) {
                                                            if (!n) {
                                                                serversnew.add(serversss);
                                                                serversItem.remove(i);
                                                                n = true;
                                                                i--;
                                                            } else {
                                                                serversbce.add(serversss);
                                                            }
                                                        }
                                                    }
                                                    if (serversrec.size() >= 1) {
                                                        serverss.addAll(serversrec);
                                                    }
                                                    if (serversnew.size() >= 1) {
                                                        serverss.addAll(serversnew);
                                                    }
                                                    serverss.addAll(serversbce);
                                                    AppConfig.serverList = serverss;

                                                    sInterface.getStories(storiesLink).enqueue(new Callback<List<Stories>>() {
                                                        @Override
                                                        public void onResponse(Call<List<Stories>> call, Response<List<Stories>> response) {
                                                            Servers item = (Servers) AppConfig.serverList.get(0);
                                                            EdgarConectV2.host = item.getEdgarHost();
                                                            EdgarConectV2.port = item.getEdgarPort();

                                                            List<Stories> Stories = response.body();
                                                            for (Stories story : Stories) {
                                                                AppConfig.storyList.add(new Stories(story.getImageUrl(), story.getMiniDate()));
                                                            }

                                                            if(AppConfig.isAuth) {
                                                                if(callback != null) {
                                                                    callback.onHideSplash();
                                                                }
                                                            } else {
                                                                if(callback != null) {
                                                                    callback.onAuthRequired();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<List<Stories>> call, Throwable t) {
                                                            dialogManager.showErrorDialog("Не удаётся установить соединение с сервером!\nПовторите попытку позже.", null, "Повторить", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    if(dialogManager.getIsChecked()) {
                                                                        CrashReporter.sendBugReport(
                                                                                (MainScreenActivity) context,
                                                                                AppConfig.mAuth.getUid(),
                                                                                "sInterface.getStories(..)...",
                                                                                t.toString()
                                                                        );
                                                                    }
                                                                    dialogManager.hideDialog();
                                                                    startGame();
                                                                }
                                                            }, true, "Сообщить об ошибке");
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Call<List<Servers>> call, Throwable t) {
                                                    dialogManager.showErrorDialog("Не удаётся установить соединение с сервером!\nПовторите попытку позже.", null, "Повторить", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if(dialogManager.getIsChecked()) {
                                                                CrashReporter.sendBugReport(
                                                                        (MainScreenActivity) context,
                                                                        AppConfig.mAuth.getUid(),
                                                                        "sInterface.getServers(..)...",
                                                                        t.toString()
                                                                );
                                                            }
                                                            dialogManager.hideDialog();
                                                            startGame();
                                                        }
                                                    }, true, "Сообщить об ошибке");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<Main> call, Throwable t) {
                                            dialogManager.showErrorDialog("Не удаётся установить соединение с сервером!\nПовторите попытку позже.", null, "Повторить", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(dialogManager.getIsChecked()) {
                                                        CrashReporter.sendBugReport(
                                                                (MainScreenActivity) context,
                                                                AppConfig.mAuth.getUid(),
                                                                "sInterface.getMain(..)...",
                                                                t.toString()
                                                        );
                                                    }
                                                    dialogManager.hideDialog();
                                                    startGame();
                                                }
                                            }, true, "Сообщить об ошибке");
                                        }
                                    });
                                }
                            } else {
                                dialogManager.showErrorDialog("Не удаётся установить соединение с сервером!\nПовторите попытку позже.", null, "Повторить", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(dialogManager.getIsChecked()) {
                                            CrashReporter.sendBugReport(
                                                    (MainScreenActivity) context,
                                                    AppConfig.mAuth.getUid(),
                                                    "sInterface.getApi(..)... Response == NULL",
                                                    "Ошибка: " + response.code() + " - " + response.message()
                                            );
                                        }
                                        dialogManager.hideDialog();
                                        startGame();
                                    }
                                }, true, "Сообщить об ошибке");
                            }
                        } else {
                            dialogManager.showErrorDialog("Не удаётся установить соединение с сервером!\nПовторите попытку позже.", null, "Повторить", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(dialogManager.getIsChecked()) {
                                        CrashReporter.sendBugReport(
                                                (MainScreenActivity) context,
                                                AppConfig.mAuth.getUid(),
                                                "sInterface.getApi(..)... response.isSuccessful()",
                                                "Ошибка: " + response.code() + " - " + response.message()
                                        );
                                    }
                                    dialogManager.hideDialog();
                                    startGame();
                                }
                            }, true, "Сообщить об ошибке");
                        }
                    }
                    public void onFailure(Call<Api> call, Throwable th) {
                        dialogManager.showErrorDialog("Не удаётся установить соединение с сервером!\nПовторите попытку позже.", null, "Повторить", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(dialogManager.getIsChecked()) {
                                    CrashReporter.sendBugReport(
                                            (MainScreenActivity) context,
                                            AppConfig.mAuth.getUid(),
                                            "sInterface.getApi(..)... onFailure",
                                            th.toString()
                                    );
                                }
                                dialogManager.hideDialog();
                                startGame();
                            }
                        }, true, "Сообщить об ошибке");
                    }
                });
            }
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDialogManager(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }

    public void destroy() {
        if (callback != null) {
            callback = null;
        }
    }
}