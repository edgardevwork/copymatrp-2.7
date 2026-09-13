package ru.edgar.nlremake.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Random;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.model.Stories;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.other.LauncherUiComponent;
import ru.edgar.space.UiManager;
import ru.edgar.space.SAMP;
import ru.edgar.space.core.ui.dialogs.Dialog;

public class MenuFragment implements LauncherUiComponent {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isCarouselRunning = false;
    private ViewGroup viewGroup = null;
    private FrameLayout nick_name_layout, news, btn_vk, btn_telegram, btn_settings;
    private FrameLayout btn_support, btn_balance, btn_donate, frame_server, btn_play;
    private LinearLayout btn_shop, gift_window;
    private TextView news_date, news_date_two;
    private ImageView news_image, news_image_two;
    private boolean isFirstImageVisible = true;
    private int currentStoryIndex = 0;
    View[] bars;

    public Runnable carouselStoryRunnable;

    private ArrayList<Stories> storiesList;

    @SuppressLint("ClickableViewAccessibility")
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

        bars = new View[7];

        for (int i = 0; i < 7; i++) {
            if (barsContainer.getChildAt(i) instanceof View) {
                bars[i] = barsContainer.getChildAt(i);
                if (i >= storiesList.size())
                    bars[i].setVisibility(View.GONE);
            }
        }

        carouselStoryRunnable = new Runnable() {
            @Override
            public void run() {
                replaceStory();
                handler.postDelayed(this, 5000L);
            }
        };

        nick_name_layout = viewGroup.findViewById(R.id.nick_name_layout);
        nick_name_layout.setOnTouchListener(new UiManager.animClickBtn(activity, nick_name_layout));
        nick_name_layout.setOnClickListener(v -> {
            // Переход на персонажа
            //SAMP.getInstance().AppConfig.mAuth.signOut();
            hide();
            UiManager.getUiManager().getTyped(UiManager.PROFILE).show();
        });

        news = viewGroup.findViewById(R.id.news);
//news.setOnTouchListener(new UiManager.animClickBtn(activity, news));
        news.setOnTouchListener(new View.OnTouchListener() {
            AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.reduce_size);
            AnimatorSet animatorSet1 = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.regain_size);
            boolean isPressed = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction() & 255;

                if (action == MotionEvent.ACTION_DOWN) {
                    // Нажали - уменьшаем
                    if (animatorSet1.isRunning()) {
                        animatorSet1.end();
                    }
                    animatorSet.setTarget(news);
                    animatorSet.start();
                    isPressed = true;
                    return true;

                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    // Отпустили или отменили - возвращаем размер
                    if (animatorSet.isRunning()) {
                        animatorSet.end();
                    }
                    animatorSet1.setTarget(news);
                    animatorSet1.start();

                    // Если это был ACTION_UP и палец был на кнопке - выполняем клик
                    if (action == MotionEvent.ACTION_UP && isPressed) {
                        // Проверяем что палец отпущен в пределах кнопки
                        float x = event.getX();
                        float y = event.getY();
                        if (x >= 0 && x <= v.getWidth() && y >= 0 && y <= v.getHeight()) {
                            // Вызываем performClick чтобы сработал OnClickListener
                            v.performClick();
                        } else {
                            // Палец отпущен вне кнопки - заменяем историю
                            replaceStory();
                        }
                    }

                    isPressed = false;
                    return true;
                }

                return false;
            }
        });

        news.setOnClickListener(v -> {
            // Переход на новости.
            System.out.println("FFFFFFFFFFFFFFFFFFF");
            //Toast.makeText(activity, "Клик", Toast.LENGTH_SHORT).show();
            NotyManager notyManager = UiManager.getUiManager().getTyped(UiManager.NOTY);
            Random random = new Random();
            int i = random.nextInt(10);
            notyManager.show(i, "Клик по истории.", ">>", null, 2);
        });

        btn_vk = viewGroup.findViewById(R.id.btn_vk);
        btn_vk.setOnTouchListener(new UiManager.animClickBtn(activity, btn_vk));
        btn_vk.setOnClickListener(v -> {
            // Переход в вк.
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://vk.com/spacerp_t")));
        });

        btn_telegram = viewGroup.findViewById(R.id.btn_telegram);
        btn_telegram.setOnTouchListener(new UiManager.animClickBtn(activity, btn_telegram));
        btn_telegram.setOnClickListener(v -> {
            // Переход в телеграмм.
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/sp_gamedev")));
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
            hide();
            DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
            dialogManager.showCreateChesterDialog();
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

    public void replaceStory() {
        currentStoryIndex++;
        if (currentStoryIndex >= storiesList.size()) {
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
    }

    @Override
    public void show() {
        if (!isCarouselRunning && carouselStoryRunnable != null) {
            handler.post(carouselStoryRunnable);
            isCarouselRunning = true;
        }

        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    @Override
    public void hide() {
        if (handler != null) {
            handler.removeCallbacks(carouselStoryRunnable); // Удаляем конкретную задачу
            // currentStoryIndex--;
            // handler.removeCallbacksAndMessages(null); // Или всё подряд, если там висят другие таски
        }

        // Защита от ухода индекса в минус
        if (currentStoryIndex > 0) {
            currentStoryIndex--;
        }

        isCarouselRunning = false;

        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
