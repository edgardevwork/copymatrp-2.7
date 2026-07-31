package ru.edgar.nlremake.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.HashMap;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.space.InterfacesManager;
import ru.edgar.space.SAMP;

public class DialogFragment extends AppCompatActivity {

    public ViewGroup viewGroup;
    FrameLayout main_btn_google, bg, bg_2;
    OneTap vk_onetap;
    public FirebaseAuth mAuth;

    public DialogFragment() {
        //super();
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_dialog, (ViewGroup) null);
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
                                            MainScreenActivity.getInstance().mAuth.signInWithEmailAndPassword(id + "@vk.ru", id + "pass").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                                        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
                                                        MainScreenActivity.getInstance().loadSettings();/// загрузка игры после входа
                                                    } else {
                                                        //ошибка
                                                        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
                                                        showNewDialog(false, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", null, new OpenDialogAuth(), null);

                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                if (!isAcc) {
                                    MainScreenActivity.getInstance().mAuth.createUserWithEmailAndPassword(id + "@vk.ru", id + "pass").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                                MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
                                                MainScreenActivity.getInstance().loadSettings();
                                            } else {
                                                //ошибка
                                                MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
                                                showNewDialog(false, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", null, new OpenDialogAuth(), null);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
                                showNewDialog(false, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", null, new OpenDialogAuth(), null);

                            }
                        });

                        // Здесь можете обрабатывать успешную аутентификацию
                    } else {
                        // Если произошла ошибка или токен отсутствует
                        Log.e("VK_AUTH", "Ошибка обработки токена(пустой)!");
                        showNewDialog(false, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", null, new OpenDialogAuth(), null);
                    }
                    return null;
                },
                (oAuth, fail) -> {
                    Log.e("VK_AUTH", "Ошибка обработки токена " + fail.getDescription());
                    showNewDialog(false, "Ошибка авторизации через VK!\nПопробуйте ещё раз.", null,"Понятно", null, new OpenDialogAuth(), null);
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

        bg = viewGroup.findViewById(R.id.bg);
        bg_2 = viewGroup.findViewById(R.id.bg_2);

        mAuth = FirebaseAuth.getInstance();

        viewGroup.setVisibility(View.GONE);
    }

//showNewDialog(false, "Не удаётся установить соединение с сервером!\nПовторите попытку позже.",null, "Повторить", null, new loadJsonRepit(), null);
//
    public void showNewDialog(boolean isAuth, String name, String dname, String b1, String b2, View.OnClickListener click1, View.OnClickListener click2) {
        System.out.println("AuthAoth");
        if(isAuth) {
            bg.setVisibility(View.VISIBLE);
            bg_2.setVisibility(View.GONE);
            viewGroup.setAlpha(0.0f);
            MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);
        } else {
            bg.setVisibility(View.GONE);
            bg_2.setVisibility(View.VISIBLE);
            MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);

            TextView text = viewGroup.findViewById(R.id.textView45);
            text.setText(name);
            TextView text2 = viewGroup.findViewById(R.id.dname);
            if(dname != null) {
                text2.setVisibility(View.VISIBLE);
                text2.setText(dname);
            } else {
                text2.setVisibility(View.GONE);
            }
            TextView text3 = viewGroup.findViewById(R.id.b1);
            if(b1 != null) {
                text3.setVisibility(View.VISIBLE);
                text3.setText(b1);
            } else {
                text3.setVisibility(View.GONE);
            }
            TextView text34 = viewGroup.findViewById(R.id.b2);
            if(b2 != null) {
                text34.setVisibility(View.VISIBLE);
                text34.setText(b2);
            } else {
                text34.setVisibility(View.GONE);
            }
            if(click1 != null) {
                FrameLayout main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
                main_btn_yes.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_yes));
                main_btn_yes.setOnClickListener(click1);
                main_btn_yes.setVisibility(View.VISIBLE);
            } else {
                FrameLayout main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
                main_btn_yes.setVisibility(View.GONE);
            }
            if(click2 != null) {
                FrameLayout main_btn_no = viewGroup.findViewById(R.id.main_btn_no);
                main_btn_no.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_no));
                main_btn_no.setOnClickListener(click2);
                main_btn_no.setVisibility(View.VISIBLE);
            } else {
                FrameLayout main_btn_no = viewGroup.findViewById(R.id.main_btn_no);
                main_btn_no.setVisibility(View.GONE);
            }
        }
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

    public class closeDialog implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
            MainScreenActivity.getInstance().loadSettings();
        }
    }

    public class OpenDialogAuth implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
            showNewDialog(true, "i", "i", null, null, null, null);
        }
    }

    public void show() {
        Point point = new Point();
        MainScreenActivity.getInstance().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
    }

    public void hide() {
        Point point = new Point();
        MainScreenActivity.getInstance().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();

    }
    public class ssetVisibility implements Runnable {
        public ssetVisibility() {
        }

        @Override
        public final void run() {
            viewGroup.setVisibility(View.GONE);
        }
    }
}
