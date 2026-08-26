package ru.edgar.nlremake.fragment.dialogs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.fragment.LoadingFragment;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.nlremake.network.Interface;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.space.InterfacesManager;

public class AuthEmailFragment {

    private int secs = 60;
    private String codeMail;
    private ViewGroup viewGroup;

    private boolean isSendCode = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final String PASSWORD_REGEX = "^[a-zA-Z0-9]{6,30}$";
    private AuthStatus status_auth = AuthStatus.EMAIL_CHECK; // 0 - email, 1 - login, 2 - pases, 3 - code, 4 - recover email, 5 - recover code, 6 - recover creste pass;

    private enum AuthStatus {
        EMAIL_CHECK,
        EMAIL_LOGIN,
        EMAIL_CREATE_PASS,
        EMAIL_CHECK_CODE,
        EMAIL_RECOVER,
        EMAIL_RECOVER_CODE,
        EMAIL_RECOVER_CREATE_PASS;
    }

    private boolean isFreeEmail = true;
    private TextView bigText, littleText, email_error_text, email_pass_error_text;
    private FrameLayout btn_back, main_btn_yes, main_btn_no;
    private LinearLayout email_layout_email, email_layout_pass, email_layout_pass2, email_layout_code;
    private EditText email_layout_email_input, email_layout_pass_input, email_layout_pass2_input, email_layout_code_input;

    public AuthEmailFragment() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_auth_email, (ViewGroup) null);
        MainScreenActivity.getInstance().getMainScreen().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        bigText = viewGroup.findViewById(R.id.bigText);
        littleText = viewGroup.findViewById(R.id.littleText);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.apiLink)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Interface sInterface = retrofit.create(Interface.class);

        email_error_text = viewGroup.findViewById(R.id.email_error_text);
        email_pass_error_text = viewGroup.findViewById(R.id.email_pass_error_text);

        email_layout_email = viewGroup.findViewById(R.id.email_layout_email);
        email_layout_pass = viewGroup.findViewById(R.id.email_layout_pass);
        email_layout_pass2 = viewGroup.findViewById(R.id.email_layout_pass2);
        email_layout_code = viewGroup.findViewById(R.id.email_layout_code);

        email_layout_email_input = viewGroup.findViewById(R.id.email_layout_email_input);
        email_layout_pass_input = viewGroup.findViewById(R.id.email_layout_pass_input);
        email_layout_pass2_input = viewGroup.findViewById(R.id.email_layout_pass2_input);
        email_layout_code_input = viewGroup.findViewById(R.id.email_layout_code_input);

        email_layout_email_input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                animateViewSlideUpHide(email_error_text);
                email_layout_email.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
            }
        });

        email_layout_pass_input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                animateViewSlideUpHide(email_pass_error_text);
                email_layout_pass.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
            }
        });

        email_layout_pass2_input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                animateViewSlideUpHide(email_error_text);
                email_layout_pass2.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
            }
        });

        email_layout_code_input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                animateViewSlideUpHide(email_error_text);
                email_layout_code.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
            }
        });

        main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
        main_btn_yes.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_yes));
        main_btn_yes.setOnClickListener(v -> {
            switch (status_auth) {
                case EMAIL_CHECK:
                    // Проверка формата почты
                    String textEmail = email_layout_email_input.getText().toString();

                    if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                        // Ошибка Неправильный формат почты.
                        email_error_text.setText("Неправильный формат эл. адреса");
                        email_layout_email.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_error_text);
                        return;
                    }

                    // Проверка зарегистрирована ли почта
                    LoadingFragment.getInstance().show();
                    FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String paramValue = userSnapshot.child("email").getValue(String.class);
                                if (paramValue != null) {
                                    if (email_layout_email_input.getText().toString().equals(paramValue.toString())) {
                                        LoadingFragment.getInstance().hide();

                                        isFreeEmail = false;
                                        status_auth = AuthStatus.EMAIL_LOGIN;

                                        email_layout_email_input.setEnabled(false);
                                        email_layout_email_input.setFocusable(false);
                                        email_layout_email_input.setFocusableInTouchMode(false);
                                        //email_layout_email_input.setTextColor(872415231);
                                        email_layout_email.setAlpha(0.5f);

                                        resetPassLayout();
                                        animateViewSlideUp(email_layout_pass);

                                        bigText.setText("Авторизация по эл. почте");
                                        littleText.setText("Введи свой адрес эл. почты, чтобы продолжить\nпроцесс регистрации или авторизовать твой аккаунт\nв игре.");

                                        InputMethodManager immm = (InputMethodManager) MainScreenActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        immm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);

                                        return;
                                    }
                                }
                            }
                            if (isFreeEmail) {
                                LoadingFragment.getInstance().hide();

                                status_auth = AuthStatus.EMAIL_CREATE_PASS;

                                email_layout_email_input.setEnabled(false);
                                email_layout_email_input.setFocusable(false);
                                email_layout_email_input.setFocusableInTouchMode(false);
                                //email_layout_email_input.setTextColor(872415231);
                                email_layout_email.setAlpha(0.5f);

                                resetPassLayout();
                                resetPass2Layout();
                                animateViewSlideUp(email_layout_pass);
                                animateViewSlideUp(email_layout_pass2);

                                main_btn_no.setVisibility(View.GONE);
                                main_btn_no.setOnTouchListener(null);

                                bigText.setText("Зарегистрироваться по\nэл. почте");
                                littleText.setText("Введи свой пароль, чтобы создать аккаунт.");

                                InputMethodManager immm = (InputMethodManager) MainScreenActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                                immm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                    break;
                case EMAIL_LOGIN:
                    // Проверка пароля
                    String pass = email_layout_pass_input.getText().toString().trim();

                    if (pass.isEmpty()) {
                        email_pass_error_text.setText("Введите пароль");
                        email_layout_pass.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_pass_error_text);
                        return;
                    } else {
                        if (!pass.matches(PASSWORD_REGEX)) {
                            DialogManager.getDialogManager().showDialog("Упс!", "Пароль должен состоять минимум из 6 символов, максимум 30 символов. Разрешены буквы(англ) и цифры. Запрещены специальные символы", "Понял", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogManager.getDialogManager().hideDialog();
                                }
                            }, null);
                            return;
                        }
                    }

                    // Вход
                    MainScreenActivity.getInstance().mAuth.signInWithEmailAndPassword("mail" + email_layout_email_input.getText().toString().trim(), email_layout_pass_input.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        hideAuthEmailDialog();
                                        MainScreenActivity.getInstance().onRequestPermissions();
                                    } else {
                                        email_pass_error_text.setText("Введен неправильный пароль");
                                        email_layout_pass.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                                        animateViewSlideUp(email_pass_error_text);
                                    }
                                }
                            });
                    break;
                case EMAIL_CREATE_PASS:
                    // Проверка пароля
                    String p1 = email_layout_pass_input.getText().toString().trim();
                    String p2 = email_layout_pass2_input.getText().toString().trim();

                    if (p1.isEmpty()) {
                        email_pass_error_text.setText("Введите пароль");
                        email_layout_pass.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_pass_error_text);
                        return;
                    } else {
                        if (!p1.matches(PASSWORD_REGEX)) {
                            DialogManager.getDialogManager().showDialog("Упс!", "Пароль должен состоять минимум из 6 символов, максимум 30 символов. Разрешены буквы(англ) и цифры. Запрещены специальные символы", "Понял", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogManager.getDialogManager().hideDialog();
                                }
                            }, null);
                            return;
                        }
                    }

                    if (p2.isEmpty()) {
                        email_error_text.setText("Повторите введенный пароль");
                        email_layout_pass2.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_error_text);
                        return;
                    } else {
                        if (!p2.matches(PASSWORD_REGEX)) {
                            DialogManager.getDialogManager().showDialog("Упс!", "Пароль должен состоять минимум из 6 символов, максимум 30 символов. Разрешены буквы(англ) и цифры. Запрещены специальные символы", "Понял", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogManager.getDialogManager().hideDialog();
                                }
                            }, null);
                            return;
                        }
                    }

                    if (!p1.equals(p2)) {
                        email_error_text.setText("Пароли не совпадают");
                        email_layout_pass2.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_error_text);
                        return;
                    }

                    // Отправка кода
                    LoadingFragment.getInstance().show();

                    String mail = email_layout_email_input.getText().toString();

                    Call<String> call = sInterface.verifyAuth(AppConfig.verifyAuthUrl, mail);

                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            if (response.body() != null && response.isSuccessful()) {
                                LoadingFragment.getInstance().hide();
                                codeMail = response.body();
                                status_auth = AuthStatus.EMAIL_CHECK_CODE;

                                email_layout_pass_input.setEnabled(false);
                                email_layout_pass_input.setFocusable(false);
                                email_layout_pass_input.setFocusableInTouchMode(false);;
                                email_layout_pass.setAlpha(0.5f);

                                email_layout_pass2_input.setEnabled(false);
                                email_layout_pass2_input.setFocusable(false);
                                email_layout_pass2_input.setFocusableInTouchMode(false);;
                                email_layout_pass2.setAlpha(0.5f);

                                resetCodeLayout();
                                animateViewSlideUp(email_layout_code);

                                littleText.setText("Мы отправили код на твой email. Пожалуйста,\nпроверь также папку \"Спам\". ");

                                main_btn_no.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_no));
                                ((TextView) main_btn_no.getChildAt(0)).setText("Отправить снова");
                                main_btn_no.setVisibility(View.VISIBLE);

                                InputMethodManager imm = (InputMethodManager) MainScreenActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
                            } else {
                                LoadingFragment.getInstance().hide();
                                Toast.makeText(MainScreenActivity.getInstance(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            LoadingFragment.getInstance().hide();
                            Toast.makeText(MainScreenActivity.getInstance(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case EMAIL_CHECK_CODE:
                    // Сверка кода
                    if (!codeMail.equals(email_layout_code_input.getText().toString()) || email_layout_code_input.getText().toString().isEmpty()) {
                        email_error_text.setText("Введен неправильный код");
                        animateViewSlideUp(email_error_text);
                        email_layout_code.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        return;
                    }

                    // Регистрация почты
                    MainScreenActivity.getInstance().mAuth.createUserWithEmailAndPassword("mail" + email_layout_email_input.getText().toString().trim(), email_layout_pass_input.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser currentUser = MainScreenActivity.getInstance().mAuth.getCurrentUser();
                                        if(currentUser != null) {
                                            AppConfig.isAuth = true;
                                        } else {
                                            AppConfig.isAuth = false;
                                        }

                                        HashMap<String, Object> Info = new HashMap<>();
                                        Info.put("email", email_layout_email_input.getText().toString().trim());
                                        Info.put("way", 1);
                                        FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Info);
                                        // EDGAR 3.0 NLRemake version от 24.08.2026
                                        DialogManager.getDialogManager().hideAuthEmailDialog();
                                        MainScreenActivity.getInstance().onRequestPermissions();
                                    } else {
                                        DialogManager.getDialogManager().hideAuthEmailDialog();
                                        DialogManager.getDialogManager().showErrorDialog("Ошибка авторизации через почту!\nПопробуйте ещё раз.", null,"Понятно", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(DialogManager.getDialogManager().getIsChecked()) {
                                                    CrashReporter.sendBugReport(MainScreenActivity.getInstance(), MainScreenActivity.getInstance().mAuth.getUid(), ".createUserWithEmailAndPassword(email_layout_email_input", task.getException().toString());
                                                }
                                                DialogManager.getDialogManager().hideDialog();
                                                DialogManager.getDialogManager().showAuthDialog(false);
                                            }
                                        }, true, "Сообщить об ошибке");
                                    }
                                }
                            });
                    break;
                case EMAIL_RECOVER:
                    // Проверка почты
                    String email = email_layout_email_input.getText().toString();

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        // Ошибка Неправильный формат почты.
                        email_error_text.setText("Неправильный формат эл. адреса");
                        email_layout_email.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_error_text);
                        return;
                    }

                    // Проверка зарегистрирована ли почта
                    isFreeEmail = true;
                    FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String paramValue = userSnapshot.child("email").getValue(String.class);
                                if (paramValue != null) {
                                    if (email.equals(paramValue.toString())) {
                                        isFreeEmail = false;

                                        // Отправка кода
                                        LoadingFragment.getInstance().show();

                                        Call<String> call1 = sInterface.verifyAuth(AppConfig.verifyAuthUrl, email);

                                        call1.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {

                                                if (response.body() != null && response.isSuccessful()) {
                                                    LoadingFragment.getInstance().hide();
                                                    codeMail = response.body();
                                                    status_auth = AuthStatus.EMAIL_RECOVER_CODE;

                                                    email_layout_email_input.setEnabled(false);
                                                    email_layout_email_input.setFocusable(false);
                                                    email_layout_email_input.setFocusableInTouchMode(false);
                                                    //email_layout_email_input.setTextColor(872415231);
                                                    email_layout_email.setAlpha(0.5f);

                                                    resetCodeLayout();
                                                    animateViewSlideUp(email_layout_code);

                                                    littleText.setText("Введи код подтверждения, который был направлен\nна твою эл. почту.");

                                                    main_btn_no.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_no));
                                                    ((TextView) main_btn_no.getChildAt(0)).setText("Отправить снова");
                                                    main_btn_no.setVisibility(View.VISIBLE);

                                                    InputMethodManager imm = (InputMethodManager) MainScreenActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
                                                } else {
                                                    LoadingFragment.getInstance().hide();
                                                    Toast.makeText(MainScreenActivity.getInstance(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                //System.out.println(t.getMessage());
                                                LoadingFragment.getInstance().hide();
                                                Toast.makeText(MainScreenActivity.getInstance(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    // Стопаем если нету такой почты
                                    if (isFreeEmail) {
                                        email_error_text.setText("Данная почта не зарегистрирована!");
                                        email_layout_email.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                                        animateViewSlideUp(email_error_text);
                                        return;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                    break;
                case EMAIL_RECOVER_CODE:
                    // Сверка кода
                    if (!codeMail.equals(email_layout_code_input.getText().toString()) || email_layout_code_input.getText().toString().isEmpty()) {
                        email_error_text.setText("Введен неправильный код");
                        animateViewSlideUp(email_error_text);
                        email_layout_code.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        return;
                    }

                    // Переход на другой уровень
                    resetPassLayout();
                    resetPass2Layout();

                    animateViewSlideUpHide(email_layout_code);
                    animateViewSlideUp(email_layout_pass);
                    animateViewSlideUp(email_layout_pass2);

                    main_btn_no.setVisibility(View.GONE);
                    main_btn_no.setOnTouchListener(null);

                    status_auth = AuthStatus.EMAIL_RECOVER_CREATE_PASS;

                    littleText.setText("Введи новый пароль, который будет связан с твоим\nаккаунтом.");
                    break;
                case EMAIL_RECOVER_CREATE_PASS:
                    // Проверка пароля
                    String pass1 = email_layout_pass_input.getText().toString().trim();
                    String pass2 = email_layout_pass2_input.getText().toString().trim();

                    if (pass1.isEmpty()) {
                        email_pass_error_text.setText("Введите пароль");
                        email_layout_pass.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_pass_error_text);
                        return;
                    } else {
                        if (!pass1.matches(PASSWORD_REGEX)) {
                            DialogManager.getDialogManager().showDialog("Упс!", "Пароль должен состоять минимум из 6 символов, максимум 30 символов. Разрешены буквы(англ) и цифры. Запрещены специальные символы", "Понял", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogManager.getDialogManager().hideDialog();
                                }
                            }, null);
                            return;
                        }
                    }

                    if (pass2.isEmpty()) {
                        email_error_text.setText("Повторите введенный пароль");
                        email_layout_pass2.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_error_text);
                        return;
                    } else {
                        if (!pass2.matches(PASSWORD_REGEX)) {
                            DialogManager.getDialogManager().showDialog("Упс!", "Пароль должен состоять минимум из 6 символов, максимум 30 символов. Разрешены буквы(англ) и цифры. Запрещены специальные символы", "Понял", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogManager.getDialogManager().hideDialog();
                                }
                            }, null);
                            return;
                        }
                    }

                    if (!pass1.equals(pass2)) {
                        email_error_text.setText("Пароли не совпадают");
                        email_layout_pass2.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        animateViewSlideUp(email_error_text);
                        return;
                    }

                    // Смена пароля
                    Call<String> call1 = sInterface.resetPassword(AppConfig.resetPassword, email_layout_email_input.getText().toString().trim(), pass1);

                    call1.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            if (response.body() != null && response.isSuccessful()) {
                                showAuthEmailDialog(); // Конец

                                InputMethodManager imm = (InputMethodManager) MainScreenActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
                            } else {
                                System.out.println("edgar + " + call.toString() + " r " + response.body().toString());
                                Toast.makeText(MainScreenActivity.getInstance(), "Ошибка при смене пароля", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            System.out.println("edgar + " + call.toString() + " r " + t.getMessage().toString());
                            Toast.makeText(MainScreenActivity.getInstance(), "Ошибка при смене пароля", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
            }
        });

        main_btn_no = viewGroup.findViewById(R.id.main_btn_no);
        main_btn_no.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_no));
        main_btn_no.setOnClickListener(v -> {
            switch (status_auth) {
                case EMAIL_LOGIN:
                    animateViewSlideUpHide(email_layout_pass);

                    email_layout_email_input.setEnabled(true);
                    email_layout_email_input.setFocusable(true);
                    email_layout_email_input.setFocusableInTouchMode(true);
                    email_layout_email_input.setTextColor(-1);
                    email_layout_email.setAlpha(1.0f);
                case EMAIL_CHECK:
                    status_auth = AuthStatus.EMAIL_RECOVER;
                    bigText.setText("Восстановить пароль");
                    littleText.setText("Введи адрес эл. почты от аккаунта.");
                    main_btn_no.setVisibility(View.GONE);
                    main_btn_no.setOnTouchListener(null);
                    break;
                case EMAIL_CHECK_CODE:
                case EMAIL_RECOVER_CODE:
                    if(!isSendCode) {
                        // Отправка кода
                        LoadingFragment.getInstance().show();
                        String email = email_layout_email_input.getText().toString();

                        Call<String> call = sInterface.verifyAuth(AppConfig.verifyAuthUrl, email);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                if (response.body() != null && response.isSuccessful()) {
                                    LoadingFragment.getInstance().hide();
                                    codeMail = response.body();

                                    main_btn_no.setOnTouchListener(null);
                                    mHandler.post(new Secynds());
                                    main_btn_no.setAlpha(0.5f);
                                    isSendCode = true;

                                    InputMethodManager imm = (InputMethodManager) MainScreenActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
                                } else {
                                    LoadingFragment.getInstance().hide();
                                    Toast.makeText(MainScreenActivity.getInstance(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                LoadingFragment.getInstance().hide();
                                Toast.makeText(MainScreenActivity.getInstance(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            }
        });

        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), btn_back));
        btn_back.setOnClickListener(v -> {
            hideAuthEmailDialog();
            DialogManager.getDialogManager().showAuthDialog(false);
        });

        viewGroup.setVisibility(View.GONE);
    }

    private class Secynds implements Runnable {
        public Secynds() {
        }

        @Override
        public final void run() {
            if (secs > 0) {
                ((TextView) main_btn_no.getChildAt(0)).setText("Отправить еще раз: " + secs+ "c");
                secs = secs + (-1);
                mHandler.postDelayed(this, 1000L);
            } else {
                main_btn_no.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_no));
                ((TextView) main_btn_no.getChildAt(0)).setText("Отправить снова");
                main_btn_no.setAlpha(1.0f);
                isSendCode = false;
                secs = 60;
            }

        }
    }

    private void animateViewSlideUp(final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.getAnimation().cancel();
        }
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);

        boolean isTextView = view instanceof TextView;

        if(isTextView) {
            view.measure(-2, -2);
        } else {
            int fixedHeight = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._28sdp);
            int parentWidth = ((View) view.getParent()).getWidth();
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(fixedHeight, View.MeasureSpec.EXACTLY)
            );
        }

        final int measuredHeight = view.getMeasuredHeight();
        final int topMarginTarget = isTextView ? MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._2sdp) : MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._8sdp);
        final int totalHeightDelta = measuredHeight + topMarginTarget;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = 0;
        params.height = 0;
        view.requestLayout();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();

                if (interpolatedTime == 1.0f) {
                    lp.topMargin = topMarginTarget;
                    lp.height = measuredHeight;
                    view.requestLayout();
                    view.setVisibility(View.VISIBLE);
                    return;
                }

                int currentDelta = (int) (totalHeightDelta * interpolatedTime);
                int calculatedMargin = Math.min(topMarginTarget, currentDelta);

                lp.topMargin = calculatedMargin;
                lp.height = currentDelta - calculatedMargin;
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(isTextView ? 150L : 300L);
        animation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animation);
    }

    private void animateViewSlideUpHide(final View view) {
        if (view.getVisibility() != View.VISIBLE || view.getAnimation() != null) {
            return;
        }

        view.clearAnimation();

        boolean isTextView = view instanceof TextView;

        int measuredHeight;
        if (isTextView) {
            view.measure(-2, -2);
            measuredHeight = view.getMeasuredHeight();
        } else {
            int fixedHeight = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._28sdp);
            measuredHeight = fixedHeight;
        }

        final int topMarginTarget = isTextView
                ? MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._2sdp)
                : MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._8sdp);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();

        final int startTopMargin = params.topMargin;
        final int totalDelta = measuredHeight + Math.abs(startTopMargin - topMarginTarget);

        params.height = measuredHeight;
        params.topMargin = 0;
        view.requestLayout();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();

                if (interpolatedTime == 1.0f) {
                    lp.height = 0;
                    lp.topMargin = topMarginTarget;
                    view.setVisibility(View.GONE);
                    view.requestLayout();
                    return;
                }

                int currentDelta = (int) (totalDelta * interpolatedTime);

                int calculatedMargin = (startTopMargin < topMarginTarget)
                        ? Math.min(topMarginTarget, startTopMargin + currentDelta)
                        : Math.max(topMarginTarget, startTopMargin - currentDelta);

                int visibleHeight = Math.max(0, measuredHeight - currentDelta);

                lp.topMargin = calculatedMargin;
                lp.height = visibleHeight;
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(isTextView ? 150L : 300L);
        animation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animation);
    }

    public void showAuthEmailDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);
        email_layout_email.setVisibility(View.VISIBLE);
        email_layout_pass.setVisibility(View.GONE);
        email_layout_pass2.setVisibility(View.GONE);
        email_layout_code.setVisibility(View.GONE);
        email_error_text.setVisibility(View.GONE);
        email_pass_error_text.setVisibility(View.GONE);

        email_layout_email.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
        email_layout_pass.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
        email_layout_pass2.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
        email_layout_code.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
        ((TextView) main_btn_no.getChildAt(0)).setText("Я не помню пароль");
        main_btn_no.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_no));
        mHandler.removeCallbacksAndMessages(null);
        main_btn_no.setVisibility(View.VISIBLE);
        main_btn_no.setAlpha(1.0f);
        isFreeEmail = false;
        isSendCode = false;
        secs = 60;

        bigText.setText("Авторизация по эл. почте");
        littleText.setText("Пожалуйста, введи свой адрес эл. почты, чтобы\nпродолжить процесс регистрации или авторизовать\nтвой аккаунт в игре.");
        status_auth = AuthStatus.EMAIL_CHECK;
        resetEmailLayout();
        resetPassLayout();
        resetPass2Layout();
        resetCodeLayout();
    }

    private void resetEmailLayout() {
        email_layout_email_input.setEnabled(true);
        email_layout_email_input.setFocusable(true);
        email_layout_email_input.setFocusableInTouchMode(true);
        email_layout_email_input.setTextColor(-1);
        email_layout_email_input.setText("");
        email_layout_email.setAlpha(1.0f);
    }

    private void resetPassLayout() {
        email_layout_pass_input.setEnabled(true);
        email_layout_pass_input.setFocusable(true);
        email_layout_pass_input.setFocusableInTouchMode(true);
        email_layout_pass_input.setTextColor(-1);
        email_layout_pass_input.setText("");
        email_layout_pass.setAlpha(1.0f);
    }

    private void resetPass2Layout() {
        email_layout_pass2_input.setEnabled(true);
        email_layout_pass2_input.setFocusable(true);
        email_layout_pass2_input.setFocusableInTouchMode(true);
        email_layout_pass2_input.setTextColor(-1);
        email_layout_pass2_input.setText("");
        email_layout_pass2.setAlpha(1.0f);
    }

    private void resetCodeLayout() {
        email_layout_code_input.setEnabled(true);
        email_layout_code_input.setFocusable(true);
        email_layout_code_input.setFocusableInTouchMode(true);
        email_layout_code_input.setTextColor(-1);
        email_layout_code_input.setText("");
        email_layout_code.setAlpha(1.0f);
    }

    public void hideAuthEmailDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
    }
}
