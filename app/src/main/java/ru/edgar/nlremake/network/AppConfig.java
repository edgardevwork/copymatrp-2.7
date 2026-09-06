package ru.edgar.nlremake.network;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AppConfig {
	public static String nickName = "";
	public static boolean isAuth = false;
	public static boolean isStartGame = false;

	public static FirebaseAuth mAuth;

	public static ArrayList archives = new ArrayList<>();
	public static ArrayList deleted = new ArrayList<>();

	public static ArrayList msglist = new ArrayList<>();
	public static ArrayList storyList = new ArrayList<>();
	public static ArrayList serverList = new ArrayList<>();

	public static String characterUrl;
	public static String verifyAuthUrl;//добавь ид код реги авторизации и т д
	public static String resetPassword;
	public static String accountDetailsUrl;
	public static String isAccUrl;
	public static String skinsCDNUrl;
	public static String crashReportUrl;
	public static String deleteAcc;
	public static String[] launcher_dan = new String[5];

	public static String apiLink;
	public static boolean testApi = false;
}