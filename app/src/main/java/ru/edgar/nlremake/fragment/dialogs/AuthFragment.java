package ru.edgar.nlremake.fragment.dialogs;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
import java.util.Random;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.fragment.Noty;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.space.InterfacesManager;

public class AuthFragment {

    private ViewGroup viewGroup;
    private FrameLayout main_btn_google;
    private OneTap vk_onetap;
    private CheckBox checkBox, checkBox1;
    private LinearLayout main_btn_email;

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

        checkBox = viewGroup.findViewById(R.id.checkBox);
        checkBox1 = viewGroup.findViewById(R.id.checkBox1);

        StringBuilder sb1 = new StringBuilder();
        String str1 = String.format("Я принимаю <a href=\"https://crmp.pro\">%s</a>", "Пользовательское соглашение (EULA)");
        sb1.append(str1);

        CharSequence content1 = removeUnderlines(Html.fromHtml(sb1.toString()));

        checkBox.setText(content1);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox.setLinkTextColor(Color.parseColor("#b797e0"));// На старой матрешке также было )
        checkBox.setHighlightColor(0);

        StringBuilder sb2 = new StringBuilder();
        String str2 = String.format("Я принимаю <a href=\"https://crmp.pro\">%s</a>", "Политику конфиденциальности");
        sb2.append(str2);

        CharSequence content2 = removeUnderlines(Html.fromHtml(sb2.toString()));

        checkBox1.setText(content2);
        checkBox1.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox1.setLinkTextColor(Color.parseColor("#b797e0"));
        checkBox1.setHighlightColor(0);

        main_btn_google = viewGroup.findViewById(R.id.main_btn_google);
        main_btn_google.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_google));
        main_btn_google.setOnClickListener(v -> {
            if(checkBox.isChecked() && checkBox1.isChecked()) {
                onClickAuthGoogle();
            } else {
                // Уточняющий диалог
                DialogManager.getDialogManager().showDialogCheckBoxes("Ошибка", "Чтобы продолжить, прими пользовательское\nсоглашение и политику конфиденциальности", "Продолжить", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogManager.getDialogManager().hideDialog();
                        if(!checkBox.isChecked() && !checkBox1.isChecked()) {
                            // Открываем уведомление
                            MainScreenActivity.getInstance().getNoty().show(1, "Чтобы продолжить, прими пользовательское соглашение и\nполитику конфиденциальности", null, 5);
                        } else onClickAuthGoogle();
                    }
                }, new CheckBox[]{checkBox, checkBox1});
            }
        });

        vk_onetap = viewGroup.findViewById(R.id.vk_one_tap_button);

        View transparentView = viewGroup.findViewById(R.id.view_vk);
        transparentView.setOnTouchListener(new View.OnTouchListener() {
            AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(MainScreenActivity.getInstance(), R.animator.reduce_size);
            AnimatorSet animatorSet1 = (AnimatorSet) AnimatorInflater.loadAnimator(MainScreenActivity.getInstance(), R.animator.regain_size);

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnimatorSet animatorSet;
                int action = event.getAction() & 255;
                if (action == event.ACTION_DOWN) {
                    if (this.animatorSet1.isRunning()) {
                        this.animatorSet1.end();
                    }
                    this.animatorSet.setTarget(vk_onetap);
                    animatorSet = this.animatorSet;
                } else if (action != event.ACTION_UP && action != event.ACTION_CANCEL) {
                    return false;
                } else {
                    if (this.animatorSet.isRunning()) {
                        this.animatorSet.end();
                    }
                    this.animatorSet1.setTarget(vk_onetap);
                    animatorSet = this.animatorSet1;

                    if (!checkBox.isChecked() || !checkBox1.isChecked()) {
                        // Уточняющий диалог
                        DialogManager.getDialogManager().showDialogCheckBoxes("Ошибка", "Чтобы продолжить, прими пользовательское\nсоглашение и политику конфиденциальности", "Продолжить", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogManager.getDialogManager().hideDialog();
                                if(!checkBox.isChecked() && !checkBox1.isChecked()) {
                                    // Открываем уведомление
                                    MainScreenActivity.getInstance().getNoty().show(1, "Чтобы продолжить, прими пользовательское соглашение и\nполитику конфиденциальности", null, 5);
                                } // Тут должно открываться вк вход, но у меня чет не получаеться отправить клик.
                            }
                        }, new CheckBox[]{checkBox, checkBox1});
                    }
                }
                animatorSet.start();
                if (checkBox.isChecked() && checkBox1.isChecked()) {
                    return false;
                } else {
                    return true;
                }
            }
        });

        vk_onetap.setScenario(OneTapTitleScenario.SignIn);
        vk_onetap.setEnabled(false);
        vk_onetap.setCallbacks(
                (oAuth, accessToken) -> {
                    if (accessToken != null /*&& !TextUtils.isEmpty(accessToken.getToken())*/) {
                        // Обработка успешного входа с токеном
                        // Log.e("VK_AUTH", "Пользователь успешно вошел, токен: " + accessToken.getToken() + accessToken.getScopes());
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
                                                                DialogManager.getDialogManager().showAuthDialog(false);
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
                                                            CrashReporter.sendBugReport(MainScreenActivity.getInstance(), MainScreenActivity.getInstance().mAuth.getUid(), ".createUserWithEmailAndPassword( !task.isSuccessful()", task.getException().toString());
                                                        }
                                                        DialogManager.getDialogManager().hideDialog();
                                                        DialogManager.getDialogManager().showAuthDialog(false);
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
                                        DialogManager.getDialogManager().showAuthDialog(false);
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
                                DialogManager.getDialogManager().showAuthDialog(false);
                            }
                        }, true, "Сообщить об ошибке");
                    }
                    return null;
                },
                (oAuth, fail) -> {
                    //Log.e("VK_AUTH", "Ошибка обработки токена " + fail.getDescription());
                    // ФИКС ДИАЛОГА ОШИБКИ! (КНОПКА НАЗАД - ОШИБКА)
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

        main_btn_email = viewGroup.findViewById(R.id.main_btn_email);
        main_btn_email.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_email));
        main_btn_email.setOnClickListener(v -> {
            if(checkBox.isChecked() && checkBox1.isChecked()) {
                hideAuthDialog();
                DialogManager.getDialogManager().showAuthEmailDialog();
            } else {
                // Уточняющий диалог
                DialogManager.getDialogManager().showDialogCheckBoxes("Ошибка", "Чтобы продолжить, прими пользовательское\nсоглашение и политику конфиденциальности", "Продолжить", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogManager.getDialogManager().hideDialog();
                        if(!checkBox.isChecked() || !checkBox1.isChecked()) {
                            MainScreenActivity.getInstance().getNoty().show(1, "Чтобы продолжить, прими пользовательское соглашение и\nполитику конфиденциальности", null, 5);
                        } else {
                            hideAuthDialog();
                            //MainScreenActivity.getInstance().mAuth.signOut(); - debug
                            DialogManager.getDialogManager().showAuthEmailDialog();
                        }
                    }
                }, new CheckBox[]{checkBox, checkBox1});
            }
        });

        viewGroup.setVisibility(View.GONE);
    }

    private void resetVkButtonState() {
        if (vk_onetap != null) {
            vk_onetap.clearAnimation();
            vk_onetap.setScaleX(1.0f);
            vk_onetap.setScaleY(1.0f);
            vk_onetap.setAlpha(1.0f);
        }
    }

    private CharSequence removeUnderlines(CharSequence p_Text) {
        if (p_Text instanceof Spannable) {
            Spannable s = (Spannable) p_Text;
            // Находим все ссылки в тексте
            URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

            for (URLSpan span : spans) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);
                s.removeSpan(span);

                // Создаем новую ссылку поверх старой, но отключаем подчеркивание
                URLSpan newSpan = new URLSpan(span.getURL()) {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false); // Тот самый переключатель, который убирает линию
                    }
                };
                s.setSpan(newSpan, start, end, 0);
            }
            return s;
        }
        return p_Text;
    }

    public void showAuthDialog(boolean isOnce) {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);
        resetVkButtonState();
        if(isOnce) {
            checkBox.setChecked(false);
            checkBox1.setChecked(false);
        }
    }

    public void hideAuthDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
    }

    private void onClickAuthGoogle() {
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

    private class Auth implements OnCompleteListener<Void> {
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
