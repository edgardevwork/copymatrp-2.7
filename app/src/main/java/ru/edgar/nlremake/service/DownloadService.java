package ru.edgar.nlremake.service;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.other.Utils;

public class DownloadService extends Service {

    private final Context appContext;
    private final Activity activity;
    private NotificationManager notifManager;

    private DownloadCallback callback;
    private List<String> urlsst;
    private List<String> toUnnZip;
    private List<String> unnZip;
    private String launcher_path;
    private int i = 0;
    private long maxSizeFiles = 0;
    private long progressSizeFiles = 0;
    private long lastNotifUpdateTime = 0;
    private boolean apkIn = true;
    private boolean once = false;
    private boolean isApkDownload = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface DownloadCallback {
        void onProgress(int percent, String text);
        void onComplete();
        void onError(String error);
        void onApkReady(String path);
        void onDownloadStarted();   // НОВОЕ: показать downloadBar и статус "Загружено файлов"
    }

    public DownloadService(Context context) {
        if (context == null) throw new IllegalArgumentException("Context == null");
        this.appContext = context.getApplicationContext();
        this.activity = (context instanceof Activity) ? (Activity) context : null;
        FileDownloader.setup(context);
        initNotification();
    }

    private void initNotification() {
        notifManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifManager.getNotificationChannel("space_1") == null) {
            NotificationChannel channel = new NotificationChannel(
                    "space_1", "space", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("space");
            channel.enableVibration(false);
            channel.setLightColor(-16711936);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setVibrationPattern(new long[]{0});
            notifManager.createNotificationChannel(channel);
        }
    }

    public void setCallback(DownloadCallback callback) {
        this.callback = callback;
    }

    private void runOnUi(Runnable r) {
        Activity a = MainScreenActivity.getInstance();
        if (a != null) a.runOnUiThread(r);
        else if (activity != null) activity.runOnUiThread(r);
        else r.run();
    }

    public void startGameDownload(List<String> url, List<String> path, List<String> unZip, List<String> toUnZip) {
        isApkDownload = false;
        File directory = new File(AppConfig.getAndroidPath(MainScreenActivity.getInstance()));
        if (!directory.exists() || !directory.isDirectory()) {
            boolean created = directory.mkdirs();
            if (!created) {
                if (callback != null) runOnUi(() -> callback.onError("Ошибка создания директории"));
                return;
            }
        }
        for (int i1 = 0; path.size() > i1; i1++) {
            String pat = path.get(i1);
            File f = new File(pat);
            if (f.exists()) {
                if (f.isDirectory()) deleteDirectory(f);
                else if (f.isFile()) f.delete();
            }
        }
        i = 0;
        urlsst = url;
        maxSizeFiles = 0;
        progressSizeFiles = 0;
        new Thread(() -> {
            for (String fileUrl : url) {
                HttpURLConnection connection = null;
                try {
                    URL url1 = new URL(fileUrl);
                    connection = (HttpURLConnection) url1.openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        long size = connection.getContentLengthLong();
                        if (size > 0) maxSizeFiles += size;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                }
            }
        }).start();
        toUnnZip = toUnZip;
        unnZip = unZip;
        String pathh = unZip.get(i);
        String urlss = url.get(i);
        createDownloadTask(urlss, pathh, false).start();
        i++;
    }

    public void startApkDownload(String url, String path, String name) {
        isApkDownload = true;
        launcher_path = path;
        new File(launcher_path).delete();
        String pathDownload = path.replace(name, "");
        createDownloadTask(url, pathDownload, true).start();
    }

    public long getMaxSizeFiles() { return maxSizeFiles; }
    public long getProgressSizeFiles() { return progressSizeFiles; }

    private BaseDownloadTask createDownloadTask(String url, String path, boolean isApk) {
        final int notificationId = 1;
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, "space_1")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Загрузка обновления")
                .setContentText("Скачивание...")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOnlyAlertOnce(true);

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
                        int loading_procent = 0;
                        String formattedText;
                        if (!isApk) {
                            if (maxSizeFiles > 0) {
                                loading_procent = (int) (((soFarBytes + progressSizeFiles) * 100L) / maxSizeFiles);
                            }
                            loading_procent = Math.max(0, Math.min(100, loading_procent));
                            formattedText = String.format("(%d / %d МБ)", (soFarBytes + progressSizeFiles) / 1048576, maxSizeFiles / 1048576);
                        } else {
                            if (totalBytes > 0) {
                                loading_procent = (int) ((soFarBytes * 100L) / totalBytes);
                            }
                            loading_procent = Math.max(0, Math.min(100, loading_procent));
                            formattedText = String.format("(%d / %d МБ)", soFarBytes / 1048576, totalBytes / 1048576);
                        }
                        final int finalPercent = loading_procent;
                        final String finalText = formattedText;
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastNotifUpdateTime > 350 || finalPercent == 100) {
                            lastNotifUpdateTime = currentTime;
                            builder.setContentText(finalPercent + "%");
                            notifManager.notify(notificationId, builder.build());
                        }
                        if (callback != null) runOnUi(() -> callback.onProgress(finalPercent, finalText));
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        builder.setContentText("Ошибка при загрузке обновления").setOngoing(false);
                        notifManager.notify(notificationId, builder.build());
                        if (callback != null) runOnUi(() -> callback.onError(e.toString()));
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String et, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, et, isContinue, soFarBytes, totalBytes);
                        notifManager.notify(notificationId, builder.build());

                        // НОВОЕ: сообщаем UI, что загрузка началась (показать downloadBar)
                        if (callback != null) runOnUi(() -> callback.onDownloadStarted());

                        int loading_procent = 0;
                        final String formattedText;
                        if (!isApk) {
                            if (maxSizeFiles > 0) {
                                loading_procent = (int) (((soFarBytes + progressSizeFiles) * 100L) / maxSizeFiles);
                            }
                            loading_procent = Math.max(0, Math.min(100, loading_procent));
                            formattedText = String.format("(%d / %d МБ)", (soFarBytes + progressSizeFiles) / 1048576, maxSizeFiles / 1048576);
                        } else {
                            if (totalBytes > 0) {
                                loading_procent = (int) ((soFarBytes * 100L) / totalBytes);
                            }
                            loading_procent = Math.max(0, Math.min(100, loading_procent));
                            formattedText = String.format("(%d / %d МБ)", soFarBytes / 1048576, totalBytes / 1048576);
                        }
                        final int finalPercent = loading_procent;
                        if (callback != null) runOnUi(() -> callback.onProgress(finalPercent, formattedText));
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        int idF = i - 1;
                        String basePath;
                        if(isApk) {
                            basePath = path;
                        } else {
                            basePath = unnZip.get(idF);
                        }
                        Set<PosixFilePermission> filePermissions = new HashSet<>();
                        filePermissions.add(PosixFilePermission.OWNER_READ);
                        filePermissions.add(PosixFilePermission.OWNER_WRITE);
                        try {
                            setPermissions(basePath, filePermissions, false);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!isApk) {
                            progressSizeFiles += task.getTotalBytes();
                            if (urlsst.size() > i) {
                                String pathh = unnZip.get(i);
                                String urlss = urlsst.get(i);
                                createDownloadTask(urlss, pathh, false).start();
                                i++;
                            } else {
                                builder.setContentText("Завершено!").setOngoing(false);
                                notifManager.notify(notificationId, builder.build());
                                // НОВОЕ: ставим прогресс 100
                                if (callback != null) runOnUi(() -> callback.onProgress(100, ""));
                                i = 0;
                                if (toUnnZip.size() > i) {
                                    String unn = toUnnZip.get(i);
                                    String unn2 = unnZip.get(i);
                                    unZip(unn, unn2);
                                    i++;
                                }
                            }
                        } else {
                            i = 0;
                            builder.setContentText("Завершено!").setOngoing(false);
                            notifManager.notify(notificationId, builder.build());
                            if (callback != null) runOnUi(() -> callback.onApkReady(launcher_path));
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                    }
                });
    }

    private void unZip(String path, String path2) {
        String mInputFilePath = path;
        String mOutputPath = path2;
        if (callback != null) {
            final int curI = i;
            runOnUi(() -> callback.onProgress(-1, String.format("%d / %d", curI + 1, toUnnZip.size())));
        }
        new Thread() {
            @Override
            public void run() {
                int idF = i - 1;
                String basePath = unnZip.get(idF);
                try {
                    Set<PosixFilePermission> folderPermissions = new HashSet<>();
                    folderPermissions.add(PosixFilePermission.OWNER_READ);
                    folderPermissions.add(PosixFilePermission.OWNER_WRITE);
                    folderPermissions.add(PosixFilePermission.OWNER_EXECUTE);
                    Set<PosixFilePermission> filePermissions = new HashSet<>();
                    filePermissions.add(PosixFilePermission.OWNER_READ);
                    filePermissions.add(PosixFilePermission.OWNER_WRITE);
                    try {
                        setPermissions(basePath, folderPermissions, true);
                        setPermissions(basePath, filePermissions, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    new ZipFile(new File(mInputFilePath)).extractAll(mOutputPath);
                    Utils.delete(new File(path));
                    Utils.delete(new File(path + ".temp"));
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                if (toUnnZip.size() > i) {
                    String unn = toUnnZip.get(i);
                    String unn2 = unnZip.get(i);
                    unZip(unn, unn2);
                    i++;
                } else {
                    String basePathFinal = AppConfig.getAndroidPath(MainScreenActivity.getInstance());
                    Set<PosixFilePermission> folderPermissions = new HashSet<>();
                    folderPermissions.add(PosixFilePermission.OWNER_READ);
                    folderPermissions.add(PosixFilePermission.OWNER_WRITE);
                    folderPermissions.add(PosixFilePermission.OWNER_EXECUTE);
                    Set<PosixFilePermission> filePermissions = new HashSet<>();
                    filePermissions.add(PosixFilePermission.OWNER_READ);
                    filePermissions.add(PosixFilePermission.OWNER_WRITE);
                    try {
                        setPermissions(basePathFinal, folderPermissions, true);
                        setPermissions(basePathFinal + "/texdb", folderPermissions, true);
                        setPermissions(basePathFinal + "/anim", folderPermissions, true);
                        setPermissions(basePathFinal + "/SPACE", folderPermissions, true);
                        setPermissions(basePathFinal + "/data", folderPermissions, true);
                        setPermissions(basePathFinal + "/audio", folderPermissions, true);
                        setPermissions(basePathFinal + "/fonts", folderPermissions, true);
                        setPermissions(basePathFinal + "/Text", folderPermissions, true);
                        setPermissions(basePathFinal + "/Textures", folderPermissions, true);
                        setPermissions(basePathFinal + "/images", folderPermissions, true);
                        setPermissions(basePathFinal, filePermissions, false);
                        setPermissions(basePathFinal + "/texdb", filePermissions, false);
                        setPermissions(basePathFinal + "/SPACE", filePermissions, false);
                        setPermissions(basePathFinal + "/images", filePermissions, false);
                        setPermissions(basePathFinal + "/data", filePermissions, false);
                        setPermissions(basePathFinal + "/anim", filePermissions, false);
                        setPermissions(basePathFinal + "/Textures", filePermissions, false);
                        setPermissions(basePathFinal + "/audio", filePermissions, false);
                        setPermissions(basePathFinal + "/fonts", filePermissions, false);
                        setPermissions(basePathFinal + "/Text", filePermissions, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    i = 0;
                    if (callback != null) runOnUi(() -> callback.onComplete());
                }
            }
        }.start();
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

    public void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) deleteDirectory(file);
                else file.delete();
            }
        }
        directory.delete();
    }

    public void installApk(String launcher_path) {
        try {
            File file = new File(launcher_path);
            Intent intent;
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    android.net.Uri apkUri = androidx.core.content.FileProvider.getUriForFile(
                            appContext, "ru.edgar.matrp" + ".provider", file);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setData(apkUri);
                } else {
                    android.net.Uri apkUri = android.net.Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                apkIn = false;
                Activity a = MainScreenActivity.getInstance();
                if (a != null) a.startActivity(intent);
                else if (activity != null) activity.startActivity(intent);
                else appContext.startActivity(intent);
            }
        } catch (Exception e) {
            if (!apkIn) {
                if (!once) {
                    apkIn = true;
                    once = true;
                    installApk(launcher_path);
                }
            }
        }
    }
}