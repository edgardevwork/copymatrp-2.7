package ru.edgar.nlremake.fragment.dialogs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario;
import com.vk.id.onetap.xml.OneTap;

import java.util.HashMap;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.space.InterfacesManager;

public class AuthFragment {

    public ViewGroup viewGroup;
    FrameLayout main_btn_google;
    OneTap vk_onetap;

    public AuthFragment() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_auth, (ViewGroup) null);
        MainScreenActivity.getInstance().getMainScreen().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        main_btn_google = viewGroup.findViewById(R.id.main_btn_google);
        main_btn_google.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_google));
        main_btn_google.setOnClickListener(v -> {
            onClickAuthGoogle();
        });
        vk_onetap = viewGroup.findViewById(R.id.vk_one_tap_button);
        vk_onetap.setScenario(OneTapTitleScenario.SignIn);
        //vk_onetap.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), vk_onetap));
        vk_onetap.setCallbacks(
                (oAuth, accessToken) -> {
                    if (accessToken != null/* && !TextUtils.isEmpty(accessToken.getToken())*/) {
                        // Обработка успешного входа с токеном
                        //Log.e("VK_AUTH", "Пользователь успешно вошел, токен: " + accessToken.getToken() + accessToken.getScopes());
                        // Использование обработчика:

                        Long id = accessToken.getUserID();
                        FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean isAcc = false;
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    Long paramValue = userSnapshot.child("vk-id").getValue(Long.class);
                                    if (paramValue != null) {
                                        if (paramValue.equals(id)) {
                                            isAcc = true;
                                            MainScreenActivity.getInstance().mAuth.signInWithEmailAndPassword(id + "@vk.com", id + "pass").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){
                                                        FirebaseUser currentUser = MainScreenActivity.getInstance().mAuth.getCurrentUser();
                                                        if(currentUser != null) {
                                                            MainScreenActivity.isAuth = true;
                                                        } else {
                                                            MainScreenActivity.isAuth = false;
                                                        }///обработчик успеха

                                                        HashMap<String, Object> Info = new HashMap<>();
                                                        Info.put("first_name", accessToken.getUserData().getFirstName());
                                                        Info.put("last_name", accessToken.getUserData().getLastName());
                                                        Info.put("domain", "id" + id);
                                                        Info.put("screen_name", "id" + id);
                                                        Info.put("vk-id", id);
                                                        Info.put("way", 3);
                                                        FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Info);
                                                        DialogManager.getDialogManager().hideAuthDialog();
                                                        MainScreenActivity.getInstance().loadSettings();/// загрузка игры после входа
                                                    } else {
                                                        //ошибка
                                                        DialogManager.getDialogManager().hideAuthDialog();
                                                        DialogManager.getDialogManager().showDialog("Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", null, new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                DialogManager.getDialogManager().hideDialog();
                                                                DialogManager.getDialogManager().showAuthDialog();
                                                            }
                                                        }, null);

                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                if (!isAcc) {
                                    MainScreenActivity.getInstance().mAuth.createUserWithEmailAndPassword(id + "@vk.com", id + "pass").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                FirebaseUser currentUser = MainScreenActivity.getInstance().mAuth.getCurrentUser();
                                                if(currentUser != null) {
                                                    MainScreenActivity.isAuth = true;
                                                } else {
                                                    MainScreenActivity.isAuth = false;
                                                }

                                                HashMap<String, Object> Info = new HashMap<>();
                                                Info.put("first_name", accessToken.getUserData().getFirstName());
                                                Info.put("last_name", accessToken.getUserData().getLastName());
                                                Info.put("domain", "id" + id);
                                                Info.put("screen_name", "id" + id);
                                                Info.put("vk-id", id);
                                                Info.put("way", 3);
                                                FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Info);
                                                // EDGAR 3.0 NLRemake version от 31.07.2026
                                                DialogManager.getDialogManager().hideAuthDialog();
                                                MainScreenActivity.getInstance().loadSettings();
                                            } else {
                                                DialogManager.getDialogManager().hideAuthDialog();
                                                DialogManager.getDialogManager().showErrorDialog("Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if(DialogManager.getDialogManager().getIsChecked()) {
                                                            CrashReporter.sendBugReport(MainScreenActivity.getInstance(), MainScreenActivity.getInstance().mAuth.getUid(), ".createUserWithEmailAndPassword( !task.isSuccessful()", task.toString());
                                                        }
                                                        DialogManager.getDialogManager().hideDialog();
                                                        DialogManager.getDialogManager().showAuthDialog();
                                                    }
                                                }, true, "Сообщить об ошибке");
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                DialogManager.getDialogManager().hideAuthDialog();
                                DialogManager.getDialogManager().showErrorDialog("Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(DialogManager.getDialogManager().getIsChecked()) {
                                            CrashReporter.sendBugReport(MainScreenActivity.getInstance(), MainScreenActivity.getInstance().mAuth.getUid(), "(oAuth, accessToken) - onCancelled", error.toString());
                                        }
                                        DialogManager.getDialogManager().hideDialog();
                                        DialogManager.getDialogManager().showAuthDialog();
                                    }
                                }, true, "Сообщить об ошибке");

                            }
                        });

                        // Здесь можете обрабатывать успешную аутентификацию
                    } else {
                        // Если произошла ошибка или токен отсутствует
                        //Log.e("VK_AUTH", "Ошибка обработки токена(пустой)!");
                        DialogManager.getDialogManager().hideAuthDialog();
                        DialogManager.getDialogManager().showErrorDialog("Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(DialogManager.getDialogManager().getIsChecked()) {
                                    CrashReporter.sendBugReport(MainScreenActivity.getInstance(), MainScreenActivity.getInstance().mAuth.getUid(), "(oAuth, accessToken)", "Ошибка обработки токена(пустой)!");
                                }
                                DialogManager.getDialogManager().hideDialog();
                                DialogManager.getDialogManager().showAuthDialog();
                            }
                        }, true, "Сообщить об ошибке");
                    }
                    return null;
                },
                (oAuth, fail) -> {
                    //Log.e("VK_AUTH", "Ошибка обработки токена " + fail.getDescription());
                    DialogManager.getDialogManager().hideAuthDialog();
                    DialogManager.getDialogManager().showErrorDialog("Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(DialogManager.getDialogManager().getIsChecked()) {
                                CrashReporter.sendBugReport(MainScreenActivity.getInstance(), MainScreenActivity.getInstance().mAuth.getUid(), "(oAuth, fail)", "Ошибка обработки токена " + fail.getDescription());
                            }
                            DialogManager.getDialogManager().hideDialog();
                            DialogManager.getDialogManager().showAuthDialog();
                        }
                    }, true, "Сообщить об ошибке");
                    return null;
                },
                (data, completion) -> {
                    /*if(!completion) {
                        MainScreenActivity.getInstance().getNewDialogFragment().showNewDialog(false, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", null, new NewDialogFragment.closeDialog1(), null);
                    }*/
                    //Log.e("VK_AUTH", "Ошибка обработки токена / Не ошибка");
                    //Toast.makeText(getApplicationContext(), "Ошибка другая!", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );

        viewGroup.setVisibility(View.GONE);
    }

    void showAuthDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);
    }

    void hideAuthDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
    }

    public void onClickAuthGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(MainScreenActivity.getInstance().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainScreenActivity.getInstance(), options);
        if (GoogleSignIn.getLastSignedInAccount(MainScreenActivity.getInstance()) != null) {
            googleSignInClient.signOut().addOnCompleteListener(MainScreenActivity.getInstance(), new Auth(googleSignInClient));
            return;
        }
        Intent i = googleSignInClient.getSignInIntent();
        MainScreenActivity.getInstance().startActivityForResult(i, 1234);
    }

    public class Auth implements OnCompleteListener<Void> {
        GoogleSignInClient googleSignInClient1;

        public Auth(GoogleSignInClient googleSignInClient) {
            googleSignInClient1 = googleSignInClient;
        }

        @Override // com.google.android.gms.tasks.OnCompleteListener
        public final void onComplete(@NonNull Task<Void> task) {
            onClickAuthGoogle();
        }
    }
}
