package ru.edgar.nlremake.fragment.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.matrp.R;
import ru.edgar.nlremake.adapter.GenderAdapter;
import ru.edgar.nlremake.data.PlayerData;
import ru.edgar.nlremake.model.Gender;
import ru.edgar.nlremake.model.Server;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.nlremake.network.FirebaseRepository;
import ru.edgar.nlremake.network.Interface;
import ru.edgar.space.EdgarConectV2;
import ru.edgar.space.SAMP;
import ru.edgar.space.UiManager;

public class CreateCharacterFragment {

    private ViewGroup viewGroup;
    private Activity context;
    private LinearLayout btn_back, btn_male, btn_female;
    private FrameLayout next, btn_random;
    private ConstraintLayout serverLayout;
    private TextView serverNumber;

    private TextView error_messange;

    ArrayList<Gender> mGender = new ArrayList<>();

    private RecyclerView recycler;

    private GenderAdapter genderAdapter;

    private EditText mInput;
    private EditText mInput1;
    private EditText mInput2;

    private LinearLayout nameLayout, surnameLayout;

    private String mCurrentInputText = "";

    int iSex = 0;

    private static final int[] maleSkins = {98, 97, 35, 71/*, 113*/};

    private static final int[] femaleSkins = {40, 63};

    public CreateCharacterFragment(Activity activity) {
        if (viewGroup != null && !AppConfig.isStartGame) {
            return;
        }
        context = activity;

        viewGroup = (ViewGroup) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_create_character, (ViewGroup) null);
        UiManager.getUiManager().getFrontUI().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        error_messange = viewGroup.findViewById(R.id.error_messange);

        btn_random = viewGroup.findViewById(R.id.btn_random);
        btn_random.setOnTouchListener(new UiManager.animClickBtn(activity, btn_random));
        btn_random.setOnClickListener(v -> {
            DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
            dialogManager.showDialog("Упс! Данная функиця\nвременно не доступна!", "Но это не повод переживать!\nВозможно уже в ближайщее время ее сделают рабочей :)", "Хорошо", null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogManager.hideDialog();
                }
            }, null);
        });

        mInput = (EditText) viewGroup.findViewById(R.id.name_input);
        mInput1 = (EditText) viewGroup.findViewById(R.id.surname_input);
        mInput2 = (EditText) viewGroup.findViewById(R.id.bonus_input);

        nameLayout = viewGroup.findViewById(R.id.name_layout);
        surnameLayout = viewGroup.findViewById(R.id.surname_layout);

        // 2. Проверка на наличие русских букв и цифр
        Pattern pattern = Pattern.compile("[а-яА-Я0-9]"); //  "[а-яА-Я0-9]+" если хотим найти последовательность
        Matcher matcher = pattern.matcher(mInput.getText().toString());
        Matcher matcher1 = pattern.matcher(mInput1.getText().toString());
        /*String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        mInput.setFilter(true);
        mInput.setFilterKeys(str);
        mInput1.setFilter(true);
        mInput1.setFilterKeys(str);
        mInput2.setFilter(true);
        mInput2.setFilterKeys(str);*/

        next = viewGroup.findViewById(R.id.main_btn_yes);
        next.setOnTouchListener(new UiManager.animClickBtn(activity, next));
        next.setOnClickListener(v -> {
            if(!mInput.getText().toString().isEmpty() && !matcher.find()) {
                if (!mInput1.getText().toString().isEmpty() && !matcher1.find()) {
                    if (mInput1.getText().toString().length() >= 3 && mInput.getText().length() >= 3) {
                        mInput.setText(mInput.getText().toString().substring(0, 1).toUpperCase() + mInput.getText().toString().substring(1));
                        mInput1.setText(mInput1.getText().toString().substring(0, 1).toUpperCase() + mInput1.getText().toString().substring(1));
                        mInput.setSelection(mInput.length());
                        mInput1.setSelection(mInput1.length());
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://google.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        Interface sInterface = retrofit.create(Interface.class);

                        Call<String> call = sInterface.getIsAcc(AppConfig.isAccUrl, AppConfig.serverId, mInput.getText().toString() + "_" + mInput1.getText().toString());

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.body() != null && response.isSuccessful()) {
                                    if (response.body().equals("nezanet")) {
                                        System.out.println(response.body());
                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl("https://google.com/")
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();

                                        Interface sInterface = retrofit.create(Interface.class);

                                        String sex = String.valueOf(iSex); //1 female / 0 male


                                        String skin;

                                        if (sex == "1") {
                                            skin = String.valueOf(femaleSkins[genderAdapter.selectedPosition]);
                                        } else {
                                            skin = String.valueOf(maleSkins[genderAdapter.selectedPosition]);
                                        }

                                        String promo;

                                        if (mInput2.getText().toString().length() <= 0) {
                                            promo = null;
                                        } else {
                                            promo = mInput2.getText().toString();
                                        }

                                        String nickName = mInput.getText().toString() + "_" + mInput1.getText().toString();

                                        Call<String> call1 = sInterface.character(AppConfig.characterUrl, FirebaseAuth.getInstance().getUid(), AppConfig.serverId, nickName, sex, skin, promo);

                                        call1.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call1, Response<String> response) {

                                                if (response.body() != null && response.isSuccessful()) {
                                                    if (response.body().equals("ycpex!")) {
                                                        UiManager.getUiManager().getTyped(UiManager.LOADING).show();
                                                        Server server = AppConfig.serverSelect;
                                                        FirebaseRepository.saveServerInfo(server.getId(), server.getName(), server.getColor(), nickName);

                                                        EdgarConectV2.host = server.getIp();
                                                        EdgarConectV2.port = server.getPort();
                                                        AppConfig.nickName = nickName;

                                                        Retrofit retrofitAccount = new Retrofit.Builder()
                                                                .baseUrl("https://google.com/")
                                                                .addConverterFactory(GsonConverterFactory.create())
                                                                .build();

                                                        Interface accountInterface = retrofitAccount.create(Interface.class);

                                                        accountInterface.getAccountDetails(AppConfig.accountDetailsUrl, AppConfig.mAuth.getUid(),
                                                                AppConfig.serverId).enqueue(new Callback<PlayerData>() {

                                                            @Override
                                                            public void onResponse(Call<PlayerData> call, Response<PlayerData> response) {
                                                                if (response.isSuccessful() && response.body() != null) {

                                                                    PlayerData data = response.body();

                                                                    // PlayerData
                                                                    AppConfig.playerData = data;
                                                                    //System.out.println(data.toString());

                                                                    // ProfileData
                                                                    AppConfig.profileData.clear();

                                                                    if (data.getStatsList() != null) {
                                                                        AppConfig.profileData.addAll(data.getStatsList());
                                                                    }

                                                                    // Данные получены, продолжаем загрузку
                                                                    UiManager.getUiManager().getTyped(UiManager.LOADING).hide();

                                                                    hideCreateCharacterDialog(false);
                                                                    UiManager.getUiManager().getTyped(UiManager.MENU).show();
                                                                } else {
                                                                    DialogManager dialogManager =
                                                                            UiManager.getUiManager().getTyped(UiManager.DIALOG);

                                                                    dialogManager.showErrorDialog(
                                                                            "Не удалось получить данные аккаунта!\nПовторите попытку позже.",
                                                                            null,
                                                                            "Ок",
                                                                            new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    if (dialogManager.getIsChecked()) {
                                                                                        CrashReporter.sendBugReport(
                                                                                                activity,
                                                                                                AppConfig.mAuth.getUid(),
                                                                                                "[CreateChasterFragment] getAccountDetails(..)...",
                                                                                                "if (response.isSuccessful()) {} else { ME }"
                                                                                        );
                                                                                    }
                                                                                    dialogManager.hideDialog();
                                                                                    UiManager.getUiManager().getTyped(UiManager.LOADING).hide();

                                                                                    UiManager.getUiManager().getTyped(UiManager.SERVERS).hide();
                                                                                    UiManager.getUiManager().getTyped(UiManager.MENU).show();

                                                                                }
                                                                            },
                                                                            true,
                                                                            "Сообщить об ошибке"
                                                                    );
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<PlayerData> call, Throwable t) {
                                                                DialogManager dialogManager =
                                                                        UiManager.getUiManager().getTyped(UiManager.DIALOG);

                                                                dialogManager.showErrorDialog(
                                                                        "Не удаётся получить данные аккаунта!\nПовторите попытку позже.",
                                                                        null,
                                                                        "Ок",
                                                                        new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                if (dialogManager.getIsChecked()) {
                                                                                    CrashReporter.sendBugReport(
                                                                                            activity,
                                                                                            AppConfig.mAuth.getUid(),
                                                                                            "[CreateChasterFragment] getAccountDetails(..)...",
                                                                                            t.toString()
                                                                                    );
                                                                                }
                                                                                dialogManager.hideDialog();

                                                                                UiManager.getUiManager().getTyped(UiManager.LOADING).hide();

                                                                                UiManager.getUiManager().getTyped(UiManager.SERVERS).hide();
                                                                                UiManager.getUiManager().getTyped(UiManager.MENU).show();
                                                                            }
                                                                        },
                                                                        true,
                                                                        "Сообщить об ошибке"
                                                                );
                                                            }
                                                        });
                                                    }
                                                    System.out.println(response.body());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call1, Throwable t) {
                                                Toast.makeText(activity, "Ошибка при отправки данных", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        System.out.println(response.body());
                                        //InterfacesManager.getInterfacesManager().getNewDialogFragment().showNewDialog(false, "Упс!", "Придумайте другие имя и фамилию для персонажа и введите его на англиском языке.\nДопустимая длина от 3-х символов","ОК", null, new NewDialogFragment.closeDialog12(), null);

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                System.out.println(t.toString());
                                Toast.makeText(activity, "Ошибка при отправки данных", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        nameLayout.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        surnameLayout.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                        DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
                        dialogManager.showDialog("Упс!", "Придумайте имя и фамилию для персонажа и введите его на англиском языке.\nДопустимая длина — от 3-х символов", "ОК", null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogManager.hideDialog();
                            }
                        }, null);
                    }
                }
            }
        });

        final Pattern latinPattern = Pattern.compile("^[a-zA-Z]+$");

        Runnable validateFields = new Runnable() {
            @Override
            public void run() {
                String text1 = mInput.getText().toString().trim();
                String text2 = mInput1.getText().toString().trim();

                // 1. Проверяем пустоту (если хотя бы одно поле пустое)
                if (text1.isEmpty() || text2.isEmpty()) {
                    next.setAlpha(0.5f);
                    next.setOnTouchListener(null); // Отключаем анимацию клика
                } else {
                    next.setAlpha(1.0f);
                    next.setOnTouchListener(new UiManager.animClickBtn(activity, next)); // Включаем анимацию
                }

                // Сбрасываем фоны по умолчанию
                nameLayout.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
                surnameLayout.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg);
                error_messange.setVisibility(View.GONE);

                // 2. Проверяем наличие запрещенных символов в первом поле (если оно не пустое)
                if (!text1.isEmpty() && !latinPattern.matcher(text1).matches()) {
                    error_messange.setVisibility(View.VISIBLE);
                    nameLayout.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                    // Если есть ошибка, кнопку делать неактивной (опционально, зависит от вашей задумки)
                    next.setAlpha(0.5f);
                    next.setOnTouchListener(null);
                    return; // Прерываем дальнейшую проверку, ошибка уже найдена
                }

                // 3. Проверяем наличие запрещенных символов во втором поле (если оно не пустое)
                if (!text2.isEmpty() && !latinPattern.matcher(text2).matches()) {
                    error_messange.setVisibility(View.VISIBLE);
                    surnameLayout.setBackgroundResource(R.drawable.ic_dialog_nl_input_bg_error);
                    next.setAlpha(0.5f);
                    next.setOnTouchListener(null);
                }
            }
        };

        mInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateFields.run();
            }
        });

        mInput1.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateFields.run();
            }
        });

        /*mInput.setShowSoftInputOnFocus(false);
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        this.mInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            Editable editableText;
            if ((i != 6 && i != 5) || (editableText = this.mInput.getText()) == null) {
                return false;
            }
            this.mCurrentInputText = editableText.toString();
            return false;
        });
        this.mInput.setOnClickListener(view ->
        {
            this.mInput.requestFocus();
            *//*ViewGroup.LayoutParams layoutParams3 = viewGroup.getLayoutParams();
            layoutParams3.height = nvEventQueueActivity.getResources().getDimensionPixelSize(R.dimen._30sdp);
            viewGroup.setLayoutParams(layoutParams3);*//*
            *//*((InputMethodManager) NvEventQueueActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(this.mInput, 1);*//*
        });

        mInput1.setShowSoftInputOnFocus(false);
        this.mInput1.setOnEditorActionListener((textView, i, keyEvent) -> {
            Editable editableText;
            if ((i != 6 && i != 5) || (editableText = this.mInput1.getText()) == null) {
                return false;
            }
            this.mCurrentInputText = editableText.toString();
            return false;
        });
        this.mInput1.setOnClickListener(view ->
        {
            this.mInput1.requestFocus();
            *//*ViewGroup.LayoutParams layoutParams3 = viewGroup.getLayoutParams();
            layoutParams3.height = nvEventQueueActivity.getResources().getDimensionPixelSize(R.dimen._30sdp);
            viewGroup.setLayoutParams(layoutParams3);*//*
            *//*((InputMethodManager) NvEventQueueActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(this.mInput, 1);*//*
        });
        mInput2.setShowSoftInputOnFocus(false);
        this.mInput2.setOnEditorActionListener((textView, i, keyEvent) -> {
            Editable editableText;
            if ((i != 6 && i != 5) || (editableText = this.mInput2.getText()) == null) {
                return false;
            }
            this.mCurrentInputText = editableText.toString();
            return false;
        });
        this.mInput2.setOnClickListener(view ->
        {
            this.mInput2.requestFocus();
            *//*ViewGroup.LayoutParams layoutParams3 = viewGroup.getLayoutParams();
            layoutParams3.height = nvEventQueueActivity.getResources().getDimensionPixelSize(R.dimen._30sdp);
            viewGroup.setLayoutParams(layoutParams3);*//*
            *//*((InputMethodManager) NvEventQueueActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(this.mInput, 1);*//*
        });*/

        serverLayout = viewGroup.findViewById(R.id.server_layout);
        serverNumber = viewGroup.findViewById(R.id.server_number);
        recycler = (RecyclerView) viewGroup.findViewById(R.id.rec);

        recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(layoutManager);

        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new UiManager.animClickBtn(context, btn_back));
        btn_back.setOnClickListener(v -> {
            hideCreateCharacterDialog(true);
        });

        ImageView imagemale = viewGroup.findViewById(R.id.imagemale);
        TextView textmale = viewGroup.findViewById(R.id.textmale);
        ImageView imagefemale = viewGroup.findViewById(R.id.imagefemale);
        TextView textfemale = viewGroup.findViewById(R.id.textfemale);

        btn_male = viewGroup.findViewById(R.id.btn_male);
        btn_male.setOnTouchListener(new UiManager.animClickBtn(context, btn_male));
        btn_male.setOnClickListener(v -> {
            mGender.clear();
            mGender.add(new Gender("98"));
            mGender.add(new Gender("97"));
            mGender.add(new Gender("35"));
            mGender.add(new Gender("71"));
            //mGender.add(new Gender("113")); / Крашит!
            genderAdapter = new GenderAdapter(context, mGender);
            recycler.setAdapter(genderAdapter);
            iSex = 0;
            btn_male.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffffff")));
            btn_female.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8044414A")));
            imagemale.setColorFilter(Color.parseColor("#000000"));
            textmale.setTextColor(Color.parseColor("#000000"));
            imagefemale.setColorFilter(Color.parseColor("#97949d"));
            textfemale.setTextColor(Color.parseColor("#97949d"));
        });

        btn_female = viewGroup.findViewById(R.id.btn_female);
        btn_female.setOnTouchListener(new UiManager.animClickBtn(context, btn_female));
        btn_female.setOnClickListener(v -> {
            mGender.clear();
            mGender.add(new Gender("40"));
            mGender.add(new Gender("63"));
            iSex = 1;
            genderAdapter = new GenderAdapter(context, mGender);
            recycler.setAdapter(genderAdapter);
            btn_male.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8044414A")));
            btn_female.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffffff")));
            imagemale.setColorFilter(Color.parseColor("#97949d"));
            textmale.setTextColor(Color.parseColor("#97949d"));
            imagefemale.setColorFilter(Color.parseColor("#000000"));
            textfemale.setTextColor(Color.parseColor("#000000"));
        });

        // === ЗАЩИТА ОТ СКВОЗНЫХ КЛИКОВ ===
        // Говорим системе, что этот слой сам поглощает все нажатия и не пускает их вниз
        viewGroup.setClickable(true);
        viewGroup.setFocusable(true);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Оставляем пустым. Касание фона просто поглощается и не идет дальше
            }
        });

        viewGroup.setVisibility(View.GONE);
    }

    public void showCreateCharacterDialog() {
        SAMP.getInstance().zoomToPerson(true);
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
        serverLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#" + AppConfig.serverSelect.getColor())));
        serverNumber.setText(AppConfig.serverSelect.getName());
        error_messange.setVisibility(View.GONE);
        next.setAlpha(0.5f);
        next.setOnTouchListener(null);
        mGender.clear();
        mGender.add(new Gender("98"));
        mGender.add(new Gender("97"));
        mGender.add(new Gender("35"));
        mGender.add(new Gender("71"));
        //mGender.add(new Gender("113")); / Крашит!
        genderAdapter = new GenderAdapter(context, mGender);
        recycler.setAdapter(genderAdapter);
    }

    public void hideCreateCharacterDialog(boolean isShowServers) {
        SAMP.getInstance().zoomToPerson(false);
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
        if (isShowServers) {
            UiManager.getUiManager().getTyped(UiManager.SERVERS).show();
        } else {
            UiManager.getUiManager().getTyped(UiManager.MENU).show();
        }
        mInput.setText("");
        mInput1.setText("");
        mInput2.setText("");
    }
}
