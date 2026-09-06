package ru.edgar.nlremake.utils;

import android.content.Context;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ru.edgar.nlremake.model.Archive;
import ru.edgar.nlremake.model.ArchivePath;
import ru.edgar.nlremake.model.Deleted;
import ru.edgar.nlremake.other.Helper;

public class CacheChecker {
    private Context context;
    private List<Archive> archives;
    private List<Deleted> deletedList;

    public CacheChecker(Context context, List<Archive> archives, List<Deleted> deletedList) {
        this.context = context;
        this.archives = archives;
        this.deletedList = deletedList;
    }

    public CacheCheckResult checkCache() {
        List<String> path = new ArrayList<>();
        List<String> unZip = new ArrayList<>();
        List<String> toUnZip = new ArrayList<>();
        List<String> url = new ArrayList<>();
        long totalSize = 0;

        for (int i = 0; deletedList.size() > i; i++) {
            Deleted deleted = deletedList.get(i);
            File f = new File(deleted.getPath());
            if (f.exists()) {
                if (f.isDirectory()) {
                    FileUtils.deleteDirectory(f);
                } else if (f.isFile()) {
                    f.delete();
                }
            }
        }

        for (int i = 0; archives.size() > i; i++) {
            Archive archive = archives.get(i);
            long size = 0;
            for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                ArchivePath archivePaths = archive.getPaths().get(i1);
                size = size + FileUtils.getFileOrDirectorySize(archivePaths.getPath());
            }
            if (archive.getSize() != size) {
                for (int i1 = 0; archive.getPaths().size() > i1; i1++) {
                    ArchivePath archivePaths = archive.getPaths().get(i1);
                    path.add(archivePaths.getPath());
                }
                toUnZip.add(archive.getZip_path());
                unZip.add(archive.getType());
                url.add(archive.getUrls());
                totalSize = totalSize + archive.getSize();
            }
        }

        FileUtils.clearModelCache(context);
        return new CacheCheckResult(path, unZip, toUnZip, url, totalSize);
    }

    public static class CacheCheckResult {
        public final List<String> path;
        public final List<String> unZip;
        public final List<String> toUnZip;
        public final List<String> url;
        public final long totalSize;

        public CacheCheckResult(List<String> path, List<String> unZip, List<String> toUnZip, List<String> url, long totalSize) {
            this.path = path;
            this.unZip = unZip;
            this.toUnZip = toUnZip;
            this.url = url;
            this.totalSize = totalSize;
        }

        public boolean needsDownload() {
            return !url.isEmpty();
        }
    }
}