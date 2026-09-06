package ru.edgar.nlremake.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ru.edgar.nlremake.model.Archive;
import ru.edgar.nlremake.model.ArchivePath;
import ru.edgar.nlremake.model.Deleted;

public class FileUtils {
    public static void deleteDirectory(File directory) {
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
    }

    public static long getFileOrDirectorySize(String path) {
        File file = new File(path);
        if (file.isFile()) {
            if (file.getName().equals("gta_sa.set") || file.getName().equals("CINFO.BIN")) {
                return 0;
            }
            return file.length();
        } else if (file.isDirectory()) {
            return calculateDirectorySize(file);
        }
        return 0;
    }

    private static long calculateDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals("settings.ini") || file.getName().equals("MINFO.BIN")) {
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

    public static void clearModelCache(Context context) {
        try {
            File file = new File(context.getExternalFilesDir(null).toString() + "/CINFO.BIN");
            if (file.exists()) {
                file.delete();
            }
            file = new File(context.getExternalFilesDir(null).toString() + "/models/MINFO.BIN");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPermissionsToRequest(Context context) {
        List<String> permissionsToRequest = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.RECORD_AUDIO);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        return permissionsToRequest;
    }

    public static boolean isDeviceRooted() {
        return checkRootFiles() || checkSuBinary();
    }

    private static boolean checkRootFiles() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
        };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkSuBinary() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static long calculateTotalSize(List<Archive> archiveList, List<Deleted> deletedList) {
        long totalSize = 0;
        for (int i = 0; archiveList.size() > i; i++) {
            Archive archive = archiveList.get(i);
            long size = 0;
            for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                ArchivePath archivePaths = archive.getPaths().get(i1);
                size = size + getFileOrDirectorySize(archivePaths.getPath());
            }
            if (archive.getSize() != size) {
                totalSize += archive.getSize();
            }
        }
        return totalSize;
    }
}