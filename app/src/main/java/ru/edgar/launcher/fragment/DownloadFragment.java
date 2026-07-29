package ru.edgar.launcher.fragment;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import ru.edgar.launcher.model.Archive;
import ru.edgar.launcher.model.ArchivePath;
import ru.edgar.launcher.model.Deleted;
import ru.edgar.launcher.network.P7ZipApi;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

import ru.edgar.launcher.activity.MainActivity;
import ru.edgar.launcher.network.Helper;
import ru.edgar.launcher.other.Lists;
import ru.edgar.launcher.other.Utils;
import ru.edgar.matrp.R;
import ru.edgar.space.SAMP;

public class DownloadFragment extends MainActivity {

    public ConstraintLayout main_layout;
    public ImageView download_logo;
    public ImageView download_render;
    public TextView download_guide_text;
    public ProgressBar download_progressbar;
    public TextView download_text;
    public TextView faq_text;
    public List<String> urlsst;
    public List<String> toUnnZip;
    public List<String> unnZip;
    public String launcher_path;
    public boolean load = false;
    int i = 0;
    public int idText = 0;
    public int idImage = 0;
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    public final String[] TextInfo = {
            "SPACE - лучшая игра жанра Role Play.",//, открывшая за 2 года 17 серверов
            "В последнем обновлении вам больше не нужно каждый раз вводить пароль заходя на сервер.",
            "У вас есть технические или игровые вопросы? Нажмите на текст внизу экрана.",
            "Команда опытных разработчиков день и ночь работает для обеспечения комфортной игры каждому",
            "На главной странице можно узнать последние новости игры. Так же в настройках можно найти ссылки на наши социальные сети.",
            "У нас доступна удобнейшая авторизация через Google и ВКонтакте."
    };

    public DownloadFragment() {
        super();
        downloadInit();
    }

    public void downloadInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_download, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        main_layout = (ConstraintLayout) viewGroup.findViewById(R.id.main_layout);
        download_logo = (ImageView) viewGroup.findViewById(R.id.download_logo);
        download_render = (ImageView) viewGroup.findViewById(R.id.download_render);
        download_guide_text = (TextView) viewGroup.findViewById(R.id.download_guide_text);
        download_progressbar = (ProgressBar) viewGroup.findViewById(R.id.download_progressbar);
        download_text = (TextView) viewGroup.findViewById(R.id.download_text);
        faq_text = (TextView) viewGroup.findViewById(R.id.faq_text);
        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);
        for (int i10 = 0; i10 < 120; i10++) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setGradientRadius(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._128sdp));
            if (i10 < 40) {
                float f10 = i10 * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f10, -15177593, -13842957)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f10, -15177593, -15177593)).intValue()});
            } else if (i10 < 80) {
                float f11 = (i10 - 40) * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f11, -13842957, -15177593)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f11, -15177593, -13842957)).intValue()});
            } else {
                float f12 = (i10 - 80) * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f12, -15177593, -15177593)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f12, -13842957, -15177593)).intValue()});
            }
            gradientDrawable.setCornerRadius(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._4sdp));
            gradientDrawable.setStroke(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._1sdp), -13391143);
            animationDrawable.addFrame(gradientDrawable, 16);
        }
        download_progressbar.setIndeterminateDrawable(animationDrawable);
        download_progressbar.setIndeterminate(true);
        download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_5));
        SpannableString spannableString = new SpannableString("Проблемы? Мы можем вам помочь!");
        spannableString.setSpan(new StyleSpan(R.font.ttnorms_medium), 10, spannableString.length(), 33);
        spannableString.setSpan(new UnderlineSpan(), 10, spannableString.length(), 33);
        faq_text.setText(spannableString);
        faq_text.setOnClickListener(v -> {
            MainActivity.getMainActivity().faqFragment.show();
        });
        viewGroup.setVisibility(View.GONE);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        main_layout.clearAnimation();
        main_layout.setTranslationY(f10.y);
        main_layout.animate().setDuration(300L).translationY(0.0f).start();
        download_logo.clearAnimation();
        download_logo.setTranslationY(-f10.y);
        download_logo.animate().setDuration(300L).translationY(0.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        main_layout.clearAnimation();
        main_layout.setTranslationY(0.0f);
        main_layout.animate().setDuration(300L).translationY(f10.y).start();
        download_logo.clearAnimation();
        download_logo.setTranslationY(0.0f);
        download_logo.animate().setDuration(300L).translationY(-f10.y).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();
        mHandler.postDelayed(new ssetVisibility(), 300L);
    }

    public class ssetVisibility implements Runnable {
        public ssetVisibility() {
        }

        @Override
        public final void run() {
            viewGroup.setVisibility(View.GONE);
        }
    }

    public void startDownloadApk(String url, String path, String name) {
        show();
        startAnim();

        launcher_path = path;
        new File(launcher_path).delete();

        String pathDownload = path.replace(name, "");

        createDownloadTask(url, pathDownload, true).start();
    }

    private void installApk(String launcher_path) {
        try {
            File file = new File(launcher_path);
            Intent intent;
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkUri = FileProvider.getUriForFile(MainActivity.getMainActivity(), "ru.edgar.matrp" + ".provider", file);
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
                MainActivity.getMainActivity().startActivity(intent);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void startDownload(List<String> url, List<String> path, List<String> unZip, List<String> toUnZip) {
        show();
        startAnim();
        /*String hexColor = String.format("#%06X", (0xFFFFFF & -1119283065));
        System.out.println(hexColor);
        hexColor = String.format("#%06X", (0xFFFFFF & -1283248909));
        System.out.println(hexColor);
        hexColor = String.format("#%06X", (0xFFFFFF & -8637479));
        System.out.println(hexColor);*/
        // #491887 / #186887
        // #832CF3 / #2CC5F3
        // #7C33D9 / #33AAD9
        /*System.out.println(Color.parseColor("#186887"));
        System.out.println(Color.parseColor("#2CC5F3"));
        System.out.println(Color.parseColor("#33AAD9"));*/

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

    private static void deleteDirectory(File directory) {
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

    /*public void CreateDownload(String url, String path) throws IOException {
        new Thread(new Runnable() {
            FileOutputStream file = new FileOutputStream(path);

            InputStream fn;
            URLConnection urlConnection = null;

            public void run() {
                try {
                    URLConnection openConnection = new URL(url).openConnection();
                    this.urlConnection = openConnection;
                    openConnection.connect();
                    int length = this.urlConnection.getContentLength();
                    System.out.println(length);
                    this.fn = this.urlConnection.getInputStream();
                    byte[] data = new byte[4096];
                    int chet = 0;
                    while (true) {
                        int read = this.fn.read(data, 0, 4096);
                        int count = read;
                        if (read == -1) {
                            break;
                        }
                        chet += count;
                        this.file.write(data, 0, count);
                        int finalChet = chet;
                        runOnUiThread(new Runnable() {
                            public final void run() {
                                Download(finalChet, length);
                            }
                        });
                    }
                    FileOutputStream fileOutputStream = this.file;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    InputStream inputStream = this.fn;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (Exception e3) {
                    Log.e("LoaderActivity", "Ошибка " + e3);
                    FileOutputStream fileOutputStream2 = this.file;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    InputStream inputStream2 = this.fn;
                    if (inputStream2 != null) {
                        try {
                            inputStream2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable th) {
                    FileOutputStream fileOutputStream3 = this.file;
                    if (fileOutputStream3 != null) {
                        try {
                            fileOutputStream3.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    InputStream inputStream3 = this.fn;
                    if (inputStream3 != null) {
                        try {
                            inputStream3.close();
                        } catch (IOException e6) {
                            e6.printStackTrace();
                        }
                    }
                    throw th;
                }
                runOnUiThread(new Runnable() {
                    public final void run() {
                        System.out.println(urlsst.size() + " = " + i);
                        if (urlsst.size() > i) {
                            String pathh = toUnnZip.get(i);
                            String urlss = urlsst.get(i);
                            System.out.println(urlss);
                            try {
                                CreateDownload(urlss, pathh);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
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
                    }
                });
            }
        }).start();
    }

    private void Download(int finalChet, int length) {
        long progressPercent = finalChet * 100L / length;

        download_progressbar.setIndeterminate(false);

        download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_6, Float.valueOf(finalChet * 100.0f / length)));
        download_progressbar.setProgress((int) progressPercent);
    }*/

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
                        long progressPercent = soFarBytes * 100L / totalBytes;

                        download_progressbar.setIndeterminate(false);
                        download_progressbar.setProgress((int) progressPercent);
                        if (!isApk) {
                            download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_6, Float.valueOf(soFarBytes * 100.0f / totalBytes)));
                        } else {
                            download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_10, Float.valueOf(soFarBytes * 100.0f / totalBytes)));
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        System.out.println(e.toString() + " XUIIIIIIIIII");
                        Toast.makeText(MainActivity.getMainActivity(), "Произошла ошибка начните заново установку", Toast.LENGTH_SHORT).show();
                        download_progressbar.setIndeterminate(true);
                        download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_5));
                        hide();
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
        download_progressbar.setProgress(100);
        download_progressbar.setIndeterminate(true);
        download_text.setText("Распаковка архивов " + resultString);
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
                        download_progressbar.setIndeterminate(true);
                        download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_5));

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
                        MainFragment.clearModelCache();
                        MainActivity.getMainActivity().startActivity(new Intent(MainActivity.getMainActivity(), SAMP.class));
                        //checkGameFile(path, 0);
                    }
                });
            }
        }.start();
    }

    public void checkGameFile(String path1, int o) {
        List<Archive> archiveList = Lists.archives;
        List<Deleted> deletedList = Lists.deleted;
        int archeve = -1;

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
                size = size + MainFragment.getFileOrDirectorySize(archivePaths.getPath());
                System.out.println(size);
            }
            System.out.println(size + " вес локал");
            if (archive.getSize() == size) {
                System.out.println(archive.getSize() + " == " + size);
                System.out.println("Все ровно");
            } else {
                archeve = i;
            }
        }
        if(archeve != -1) {
            int finalArcheve = archeve;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Archive archive = archiveList.get(finalArcheve);
                    long size = 0;
                    for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                        ArchivePath archivePaths = archive.getPaths().get(i1);
                        size = size + MainFragment.getFileOrDirectorySize(archivePaths.getPath());
                    }
                    if(archive.getSize() != size) {
                        boolean isArch = new File(path1).isFile();
                        if(o == 1) {
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
                            i = 0;
                            String pathh = unnZip.get(i);
                            String urlss = urlsst.get(i);
                            System.out.println(urlss);
                            System.out.println(pathh);
                            createDownloadTask(urlss, pathh, false).start();
                            i++;
                            return;
                        }

                        checkGameFile(path1, 1);
                        return;

                    } else {
                        for (int i = 0; toUnnZip.size() != i; i++) {
                            String un = toUnnZip.get(i);
                            Utils.delete(new File(un));
                            Utils.delete(new File(un + ".temp"));
                        }
                        MainActivity.getMainActivity().startActivity(new Intent(MainActivity.getMainActivity(), SAMP.class));
                    }
                }
            }, 1500L);
        } else {
            MainActivity.getMainActivity().startActivity(new Intent(MainActivity.getMainActivity(), SAMP.class));
        }
    }

    public class UpdateImage implements Runnable {
        public UpdateImage() {
        }

        public final void run() {
            download_render.setTranslationX((float) download_render.getWidth());
            download_render.setAlpha(0.0f);
            StringBuilder v9 = new StringBuilder("render_pic_");
            v9.append(idImage);
            download_render.setImageResource(MainActivity.getMainActivity().getResources().getIdentifier(v9.toString(), "drawable", MainActivity.getMainActivity().getPackageName()));
            download_render.animate().translationX(0.0f).alpha(1.0f).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    public final void startAnim() {
        mHandler.removeCallbacksAndMessages((Object) null);
        idText = new Random().nextInt(TextInfo.length);
        idImage = 0;
        StringBuilder v9 = new StringBuilder("render_pic_");
        v9.append(idImage);
        download_render.setImageResource(MainActivity.getMainActivity().getResources().getIdentifier(v9.toString(), "drawable", MainActivity.getMainActivity().getPackageName()));
        download_guide_text.setText(TextInfo[idText]);
        download_guide_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Update();
            }
        }, 5000);
    }

    public final void Update() {
        mHandler.removeCallbacksAndMessages((Object) null);
        int i10 = idText + 1;
        idText = i10;
        int i11 = idImage + 1;
        idImage = i11;
        if (i10 >= TextInfo.length) {
            idText = 0;
        }
        if (i11 >= 6) {
            idImage = 0;
        }
        download_render.clearAnimation();
        download_render.animate().translationX((float) (-download_render.getWidth())).alpha(0.0f).setDuration(300).setInterpolator(new AccelerateInterpolator()).withEndAction(new UpdateImage()).start();
        download_guide_text.clearAnimation();
        download_guide_text.animate().translationX((float) (-download_guide_text.getWidth())).alpha(0.0f).setDuration(300).setInterpolator(new AccelerateInterpolator()).withEndAction(new textEdit()).start();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Update();
            }
        }, 5000);
    }
    public class textEdit implements Runnable {
        public textEdit() {
        }

        public final void run() {
            download_guide_text.setTranslationX((float) download_guide_text.getWidth());
            download_guide_text.setAlpha(0.0f);
            download_guide_text.setText(TextInfo[idText]);
            download_guide_text.animate().translationX(0.0f).alpha(1.0f).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
        }
    }
}
