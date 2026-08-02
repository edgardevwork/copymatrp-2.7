package ru.edgar.nlremake.other;

import ru.edgar.nlremake.activity.MainScreenActivity;

public class Helper {
    //public static String androidPath = "/storage/emulated/0/Android/data/ru.edgar.space/files"; // (const char*)(g_libGTASA+0x63C4B8);
    public static String androidPath = MainScreenActivity.getInstance().getExternalFilesDir("").getAbsolutePath(); // (const char*)(g_libGTASA+0x63C4B8);
}
