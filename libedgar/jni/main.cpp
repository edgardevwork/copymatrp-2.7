//
// Created by EDGAR 3.0 on 30.07.2023.
//
#include <jni.h>
#include <string.h>
#include "obfuscate.h"
//#include "main.h"
//#include "imgui/imgui.h"
//#include "imgui/imgui_draw"
//#include "imgui/imgui_impl_win32.h"
extern "C" {
	//Auht = 28540117
	JNIEXPORT void JNICALL Java_ru_edgar_launcher_activity_MainActivity_Connect(JNIEnv *env, jobject thiz) {
	 const char/*jstring*/ *ponel = OBFUSCATE("EDGAR 3.0");
	  const char/*jstring*/ *ponelpon = OBFUSCATE("EDGAR 3.0  t.me/edgar_sliv");
	 const char/*jstring*/ *ponelp = OBFUSCATE("Укажите ник в окно ниже (Dev_Edgar)");
	 const char/*jstring*/ *ed = OBFUSCATE("https://crmp.pro/edgar.php");
			jclass edMainClass = env->FindClass("ru/edgar/launcher/activity/MainActivity");
			//
			    jclass strClass = env->FindClass("java/lang/String");
				jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
				jstring encoding = env->NewStringUTF("UTF-8");
				jbyteArray bytes = env->NewByteArray(strlen(ponel));
				env->SetByteArrayRegion(bytes, 0, strlen(ponel), (jbyte*)ponel);
				jstring str1 = (jstring)env->NewObject(strClass, ctorID, bytes, encoding);
				//
				jclass strClass1 = env->FindClass("java/lang/String");
				jmethodID ctorID1 = env->GetMethodID(strClass1, "<init>", "([BLjava/lang/String;)V");
				jstring encoding1 = env->NewStringUTF("UTF-8");
				jbyteArray bytes1 = env->NewByteArray(strlen(ponelp));
				env->SetByteArrayRegion(bytes1, 0, strlen(ponelp), (jbyte*)ponelp);
				jstring str2 = (jstring)env->NewObject(strClass1, ctorID1, bytes1, encoding1);
				//
				jclass strClass3 = env->FindClass("java/lang/String");
				jmethodID ctorID3 = env->GetMethodID(strClass3, "<init>", "([BLjava/lang/String;)V");
				jstring encoding3 = env->NewStringUTF("UTF-8");
				jbyteArray bytes3 = env->NewByteArray(strlen(ponelpon));
				env->SetByteArrayRegion(bytes3, 0, strlen(ponelpon), (jbyte*)ponelpon);
				jstring str3 = (jstring)env->NewObject(strClass3, ctorID3, bytes3, encoding3);
				
				jbyteArray bytes4 = env->NewByteArray(strlen(ed));
				env->SetByteArrayRegion(bytes4, 0, strlen(ed), (jbyte*)ed);
				jstring str4 = (jstring)env->NewObject(strClass3, ctorID3, bytes4, encoding3);
		    //
		    //
			 jmethodID s_conect = env->GetMethodID(edMainClass, "Auhtch", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
             env->CallVoidMethod(thiz, s_conect, str1,  str2, str3, str4);
            // env->DeleteGlobalRef(thiz);
	}
	//Rgdjsskhuidsyh704fdj8sdaa3327977jfss997gsd
	JNIEXPORT void JNICALL Java_ru_edgar_launcher_activity_MainActivity_Rgdjsskhuidsyh704fdj8sdaa3327977jfss997gsd(JNIEnv *env, jobject thiz) {}
}