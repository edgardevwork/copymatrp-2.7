package ru.edgar.nlremake.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.model.Stories;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.other.LauncherUiComponent;
import ru.edgar.space.UiManager;
import ru.edgar.space.SAMP;

public class MenuFragment implements LauncherUiComponent {

    private final Handler handler = new Handler();
    private ViewGroup viewGroup = null;
    private FrameLayout nick_name_layout, news, btn_vk, btn_telegram, btn_settings;
    private FrameLayout btn_support, btn_balance, btn_donate, frame_server, btn_play;
    private LinearLayout btn_shop, gift_window;
    private TextView news_date, news_date_two;
    private ImageView news_image, news_image_two;
    private boolean isFirstImageVisible = true;
    private int currentStoryIndex = 0;

    public Runnable carouselStoryRunnable;

    private ArrayList<Stories> storiesList;

    @Override
    public void init(Activity activity) {
        if (viewGroup != null && !AppConfig.isStartGame) {
            //Log.e("edgar", "view" + viewGroup.toString());
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_menu, (ViewGroup) null);
        UiManager.getUiManager().getFrontUI().addView(this.viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;

        storiesList = AppConfig.storyList;

        news_date = viewGroup.findViewById(R.id.news_date);
        news_date_two = viewGroup.findViewById(R.id.news_date_two);
        news_image = viewGroup.findViewById(R.id.news_image);
        news_image_two = viewGroup.findViewById(R.id.news_image_two);

        LinearLayout barsContainer = viewGroup.findViewById(R.id.bars);

        View[] bars = new View[7];

        for (int i = 0; i < 7; i++) {
            if (barsContainer.getChildAt(i) instanceof View) {
                bars[i] = barsContainer.getChildAt(i);
                if (i >= 3/*storiesList.size()*/)
                    bars[i].setVisibility(View.GONE);
            }
        }

        carouselStoryRunnable = new Runnable() {
            @Override
            public void run() {
                currentStoryIndex++;
                if (currentStoryIndex >= 3/*storiesList.size()*/) {
                    currentStoryIndex = 0;
                }
                for (int i = 0; i < 6; i++) {
                    if (i == currentStoryIndex) {
                        bars[i].setAlpha(1.0f);
                    } else
                        bars[i].setAlpha(0.5f);
                }

                final TextView visibleTargetText = isFirstImageVisible ? news_date_two : news_date;
                final TextView invisibleTargetText = isFirstImageVisible ? news_date : news_date_two;

                final ImageView visibleTarget = isFirstImageVisible ? news_image_two : news_image;
                final ImageView invisibleTarget = isFirstImageVisible ? news_image : news_image_two;

                invisibleTargetText.animate()
                        .alpha(0.0f)
                        .setDuration(300L)
                        .start();

                invisibleTarget.animate()
                        .alpha(0.0f)
                        .setDuration(300L)
                        .start();

                visibleTargetText.setText(storiesList.get(currentStoryIndex).getMiniDate());
                visibleTargetText.animate()
                        .alpha(1.0f)
                        .setDuration(300L)
                        .start();

                Glide.with(visibleTarget.getContext())
                        .load(storiesList.get(currentStoryIndex).getImageUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                visibleTarget.animate()
                                        .alpha(1.0f)
                                        .setDuration(300L)
                                        .start();

                                isFirstImageVisible = !isFirstImageVisible;
                                return false;
                            }
                        })
                        .into(visibleTarget);

                handler.postDelayed(this, 5000L);
            }
        };

        //handler.post(carouselStoryRunnable);

        nick_name_layout = viewGroup.findViewById(R.id.nick_name_layout);
        nick_name_layout.setOnTouchListener(new UiManager.animClickBtn(activity, nick_name_layout));
        nick_name_layout.setOnClickListener(v -> {
            // Переход на персонажа
            //SAMP.getInstance().AppConfig.mAuth.signOut();
            hide();
            UiManager.getUiManager().getTyped(UiManager.PROFILE).show();
        });

        news = viewGroup.findViewById(R.id.news);
        news.setOnTouchListener(new UiManager.animClickBtn(activity, news));
        news.setOnClickListener(v -> {
            // Переход на новости.
        });

        btn_vk = viewGroup.findViewById(R.id.btn_vk);
        btn_vk.setOnTouchListener(new UiManager.animClickBtn(activity, btn_vk));
        btn_vk.setOnClickListener(v -> {
            // Переход на новости.
        });

        btn_telegram = viewGroup.findViewById(R.id.btn_telegram);
        btn_telegram.setOnTouchListener(new UiManager.animClickBtn(activity, btn_telegram));
        btn_telegram.setOnClickListener(v -> {
            // Переход на новости.
        });

        btn_shop = viewGroup.findViewById(R.id.btn_shop);
        btn_shop.setOnTouchListener(new UiManager.animClickBtn(activity,btn_shop));
        btn_shop.setOnClickListener(v -> {
            // Переход в донат
        });

        btn_settings = viewGroup.findViewById(R.id.btn_settings);
        btn_settings.setOnTouchListener(new UiManager.animClickBtn(activity, btn_settings));
        btn_settings.setOnClickListener(v -> {
            // Переход в настройки
        });

        btn_support = viewGroup.findViewById(R.id.btn_support);
        btn_support.setOnTouchListener(new UiManager.animClickBtn(activity, btn_support));
        btn_support.setOnClickListener(v -> {
            // Переход в поддержку
        });

        btn_balance = viewGroup.findViewById(R.id.btn_balance);
        btn_balance.setOnTouchListener(new UiManager.animClickBtn(activity, btn_balance));
        btn_balance.setOnClickListener(v -> {
            // Переход в хз донат наверное
        });

        btn_donate = viewGroup.findViewById(R.id.btn_donate);
        btn_donate.setOnTouchListener(new UiManager.animClickBtn(activity, btn_donate));
        btn_donate.setOnClickListener(v -> {
            // Переход в донат
        });

        gift_window = viewGroup.findViewById(R.id.gift_window);
        gift_window.setOnTouchListener(new UiManager.animClickBtn(activity, gift_window));
        gift_window.setOnClickListener(v -> {
            // Диалог какой-то
        });

        frame_server = viewGroup.findViewById(R.id.frame_server);
        frame_server.setOnTouchListener(new UiManager.animClickBtn(activity, frame_server));
        frame_server.setOnClickListener(v -> {
            // Переход в выбор сервера
        });

        btn_play = viewGroup.findViewById(R.id.btn_play);
        btn_play.setOnTouchListener(new UiManager.animClickBtn(activity, btn_play));
        btn_play.setOnClickListener(v -> {
            // Проверки
            AppConfig.nickName = "Vanek_Kitok";// AdminPass 2012
            SAMP.getInstance().connectEdgar();
            hide();
        });

        ((TextView) viewGroup.findViewById(R.id.uidtext)).setText(AppConfig.mAuth.getUid());
        viewGroup.setLayoutParams(layoutParams);
        viewGroup.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    @Override
    public void hide() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
