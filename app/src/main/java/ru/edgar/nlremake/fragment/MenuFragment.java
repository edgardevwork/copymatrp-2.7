package ru.edgar.nlremake.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nvidia.devtech.NvEventQueueActivity;

import java.util.ArrayList;
import java.util.List;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.model.News;
import ru.edgar.nlremake.network.Lists;
import ru.edgar.space.InterfacesManager;
import ru.edgar.space.SAMP;

public class MenuFragment {

    public NvEventQueueActivity nvEventQueueActivity = null;
    public final Handler handler = new Handler();
    public ViewGroup viewGroup = null;
    public FrameLayout nick_name_layout, news, btn_vk, btn_telegram, btn_settings;
    public FrameLayout btn_support, btn_balance, btn_donate, frame_server, btn_play;
    public LinearLayout btn_shop, gift_window;
    private ImageView news_image, news_image_two;
    private boolean isFirstImageVisible = true;
    private int currentStoryIndex = 0;

    private Runnable carouselStoryRunnable;

    private ArrayList<News> storiesList;

    public MenuFragment(NvEventQueueActivity nvEventQueueActivity, int guiId) {
        this.nvEventQueueActivity = nvEventQueueActivity;
        viewGroup = InterfacesManager.getInterfacesManager().viewGroup[guiId];
        init();
    }

    public void init() {
        if (viewGroup != null) {
            //Log.e("edgar", "view" + viewGroup.toString());
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) SAMP.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_menu, (ViewGroup) null);
        SAMP.getInstance().getFrontUILayout().addView(this.viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;

        storiesList = Lists.slist;

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
                String[] tesr = new String[]{"https://edgecdn.matrp.ru/matrp_mobile/images/tgpic.webp", "https://sun9-49.vkuserphoto.ru/s/v1/ig2/G71VCeaMT3sfjL8gfdwfEF0wyj65S2MCJXBFcCTPrWf8dtR25AgtNZokywZtDcsvYY-idOdJYNuw1Plt80cect9l.jpg?quality=95&as=32x14,48x22,72x32,108x49,160x72,240x108,360x162,480x216,540x243,640x288,720x324,1080x486,1280x576,1440x648,2400x1080&from=bu&cs=2400x0", "https://sun9-52.vkuserphoto.ru/s/v1/ig2/yvccRwYvATEm--pddNEOCm-pE0Wav8rUx5rtMCGC8y2bNSxJxXFL-X8DlOaDFKo3tQWXtf3NHrceS1dwPLuoP7wQ.jpg?quality=95&as=32x43,48x64,72x96,108x144,160x213,240x320,360x480,480x640,540x720,640x853,720x960,1080x1440,1280x1707,1440x1920,1920x2560&from=bu&cs=1920x0"};

                final ImageView visibleTarget = isFirstImageVisible ? news_image_two : news_image;
                final ImageView invisibleTarget = isFirstImageVisible ? news_image : news_image_two;

                invisibleTarget.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .start();

                Glide.with(visibleTarget.getContext())
                        .load(/*storiesList.get(currentStoryIndex).getImageUrl()*/tesr[currentStoryIndex])
                        .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {

                                visibleTarget.animate()
                                        .alpha(1.0f)
                                        .setDuration(300)
                                        .start();

                                isFirstImageVisible = !isFirstImageVisible;
                                return false;
                            }
                        })
                        .into(visibleTarget);

                handler.postDelayed(this, 5000);
            }
        };

        handler.post(carouselStoryRunnable);

        nick_name_layout = viewGroup.findViewById(R.id.nick_name_layout);
        nick_name_layout.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, nick_name_layout));
        nick_name_layout.setOnClickListener(v -> {
            // Переход на персонажа
        });

        news = viewGroup.findViewById(R.id.news);
        news.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, news));
        news.setOnClickListener(v -> {
            // Переход на новости.
        });

        btn_vk = viewGroup.findViewById(R.id.btn_vk);
        btn_vk.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, btn_vk));
        btn_vk.setOnClickListener(v -> {
            // Переход на новости.
        });

        btn_telegram = viewGroup.findViewById(R.id.btn_telegram);
        btn_telegram.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, btn_telegram));
        btn_telegram.setOnClickListener(v -> {
            // Переход на новости.
        });

        btn_shop = viewGroup.findViewById(R.id.btn_shop);
        btn_shop.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity,btn_shop));
        btn_shop.setOnClickListener(v -> {
            // Переход в донат
        });

        btn_settings = viewGroup.findViewById(R.id.btn_settings);
        btn_settings.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, btn_settings));
        btn_settings.setOnClickListener(v -> {
            // Переход в настройки
        });

        btn_support = viewGroup.findViewById(R.id.btn_support);
        btn_support.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, btn_support));
        btn_support.setOnClickListener(v -> {
            // Переход в поддержку
        });

        btn_balance = viewGroup.findViewById(R.id.btn_balance);
        btn_balance.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, btn_balance));
        btn_balance.setOnClickListener(v -> {
            // Переход в хз донат наверное
        });

        btn_donate = viewGroup.findViewById(R.id.btn_donate);
        btn_donate.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, btn_donate));
        btn_donate.setOnClickListener(v -> {
            // Переход в донат
        });

        gift_window = viewGroup.findViewById(R.id.gift_window);
        gift_window.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, gift_window));
        gift_window.setOnClickListener(v -> {
            // Диалог какой-то
        });

        frame_server = viewGroup.findViewById(R.id.frame_server);
        frame_server.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, frame_server));
        frame_server.setOnClickListener(v -> {
            // Переход в выбор сервера
        });

        btn_play = viewGroup.findViewById(R.id.btn_play);
        btn_play.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, btn_play));
        btn_play.setOnClickListener(v -> {
            // Проверки
            MainScreenActivity.nickName = "Piter_Parker";
            SAMP.getInstance().connectEdgar();
            hide();
        });

        ((TextView) viewGroup.findViewById(R.id.uidtext)).setText(SAMP.getInstance().mAuth.getUid());
        viewGroup.setLayoutParams(layoutParams);
        viewGroup.setVisibility(View.VISIBLE);
    }

    public void hide() {
        viewGroup.setVisibility(View.GONE);
    }
}
