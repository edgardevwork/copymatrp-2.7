package ru.edgar.nlremake.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class DialogUI extends View {
    // Константы для цветов
    private static final int BACKGROUND_COLOR = 0xE60D1318;  // Оставляем темный фон
    private static final int DIALOG_BG_COLOR = 0x2D3032FF;   // Оставляем серый фон диалога
    private static final int TEXT_COLOR_WHITE = Color.WHITE; // Белый текст
    private static final int TEXT_COLOR_GRAY = 0xFFC1BCB7;   // Светло-серый текст
    private static final int BUTTON_ORAY_COLOR = 0xFF0077FF; // Меняем на синий #0077FF (полностью непрозрачный)
    private static final int BUTTON_GRAY_COLOR = 0xFF474440; // Оставляем серый
    private static final int HIGHLIGHT_COLOR = 0x3F0077FF;   // Меняем с белого на синий с прозрачностью

    // Paint объекты
    private Paint backgroundPaint;
    private Paint dialogBgPaint;
    private Paint dialogHighlightPaint;
    private Paint textPaint;
    private Paint buttonBluePaint;
    private Paint buttonGrayPaint;

    // Размеры и отступы
    private float density;
    private float sdpScale;

    // Тексты
    private String titleText;
    private String descText;
    private String buttonYesText;
    private String buttonNoText;
    private boolean showNoButton = true;

    // Прямоугольники для областей
    private RectF dialogRect = new RectF();
    private RectF buttonYesRect = new RectF();
    private RectF buttonNoRect = new RectF();

    // Колбэки для кнопок
    private Runnable onYesClickAction;
    private Runnable onNoClickAction;

    // Родительский ViewGroup для добавления
    private ViewGroup parentViewGroup;

    public interface OnButtonClickListener {
        void onYesClick();
        void onNoClick();
    }

    // Упрощенный конструктор с параметрами
    public DialogUI(Context context, String yesButtonText, String dialogTitle, String dialogDescription) {
        this(context, yesButtonText, null, dialogTitle, dialogDescription);
    }

    // Полный конструктор с возможностью указать текст для кнопки "Нет"
    public DialogUI(Context context, String yesButtonText, String noButtonText,
                    String dialogTitle, String dialogDescription) {
        super(context);
        this.buttonYesText = yesButtonText;
        this.buttonNoText = noButtonText;
        this.titleText = dialogTitle;
        this.descText = dialogDescription;
        this.showNoButton = (noButtonText != null && !noButtonText.isEmpty());
        init(context);
    }

    public DialogUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        density = getResources().getDisplayMetrics().density;
        sdpScale = density;

        if (titleText == null) titleText = "Доступна новая\nверсия клиента!";
        if (descText == null) descText = "Скачать обновление и продолжить играть";
        if (buttonYesText == null) buttonYesText = "Скачать обновление";
        if (buttonNoText == null) buttonNoText = "Отмена";

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(BACKGROUND_COLOR);
        backgroundPaint.setStyle(Paint.Style.FILL);

        dialogBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dialogBgPaint.setColor(DIALOG_BG_COLOR);
        dialogBgPaint.setStyle(Paint.Style.FILL);

        dialogHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dialogHighlightPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(TEXT_COLOR_WHITE);
        textPaint.setTypeface(Typeface.create("montserrat_bold", Typeface.BOLD));
        textPaint.setLetterSpacing(0.06f);

        buttonBluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonBluePaint.setColor(BUTTON_ORAY_COLOR);
        buttonBluePaint.setStyle(Paint.Style.FILL);

        buttonGrayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonGrayPaint.setColor(BUTTON_GRAY_COLOR);
        buttonGrayPaint.setStyle(Paint.Style.FILL);

        setupTouchListener();
        setAlpha(0f);
    }

    public void showIn(ViewGroup parent) {
        this.parentViewGroup = parent;
        parent.addView(this, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        animateVisibility(View.VISIBLE);
    }

    public void dismiss() {
        animateVisibility(View.GONE, new Runnable() {
            @Override
            public void run() {
                if (parentViewGroup != null) {
                    parentViewGroup.removeView(DialogUI.this);
                }
            }
        });
    }

    private void animateVisibility(int visibility) {
        animateVisibility(visibility, null);
    }

    private void animateVisibility(int visibility, final Runnable onEnd) {
        if (visibility == View.VISIBLE) {
            setVisibility(View.VISIBLE);
            animate().setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (onEnd != null) onEnd.run();
                        }
                    })
                    .alpha(1.0f)
                    .start();
        } else {
            animate().setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setVisibility(View.GONE);
                            if (onEnd != null) onEnd.run();
                        }
                    })
                    .alpha(0.0f)
                    .start();
        }
    }

    public void show() {
        if (getParent() != null) {
            animateVisibility(View.VISIBLE);
        }
    }

    public void setOnClickYesBtn(Runnable action) {
        this.onYesClickAction = action;
    }

    public void setOnClickNoBtn(Runnable action) {
        this.onNoClickAction = action;
    }

    private void setupTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isYesTouched = isYesButtonClicked(event.getX(), event.getY());
                boolean isNoTouched = showNoButton && isNoButtonClicked(event.getX(), event.getY());

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isYesTouched && onYesClickAction != null) {
                        onYesClickAction.run();
                        return true;
                    } else if (isNoTouched && onNoClickAction != null) {
                        onNoClickAction.run();
                        return true;
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        float scaleFactor = 1.4f;

        // Переменная для регулировки расстояния между текстом и кнопками
        // Чем меньше значение, тем ближе кнопки к тексту
        float textToButtonSpacing = 0.7f; // Можно менять от 0.3 до 1.5

        // Рассчитываем размеры текста для всех элементов
        textPaint.setTextSize(15 * sdpScale * scaleFactor);
        textPaint.setTypeface(Typeface.create("montserrat_bold", Typeface.BOLD));
        float titleTextSize = textPaint.getTextSize();

        textPaint.setTextSize(7 * sdpScale * scaleFactor);
        textPaint.setTypeface(Typeface.create("montserrat_regular", Typeface.NORMAL));
        float descTextSize = textPaint.getTextSize();

        textPaint.setTextSize(10 * sdpScale * scaleFactor);
        textPaint.setTypeface(Typeface.create("montserrat_bold", Typeface.BOLD));
        float buttonTextSize = textPaint.getTextSize();

        // Измеряем ширину текста на кнопках отдельно для каждой кнопки
        float yesTextWidth = textPaint.measureText(buttonYesText);
        float noTextWidth = showNoButton ? textPaint.measureText(buttonNoText) : 0;

        // Отступ между текстом и краем кнопки
        float textPadding = 16 * sdpScale * scaleFactor;

        // Рассчитываем ширину каждой кнопки отдельно на основе ее текста
        float yesButtonWidth = yesTextWidth + textPadding * 2;
        float minYesButtonWidth = 80 * sdpScale * scaleFactor;
        yesButtonWidth = Math.max(yesButtonWidth, minYesButtonWidth);

        float noButtonWidth = 0;
        float minNoButtonWidth = 40 * sdpScale * scaleFactor; // Минимальная ширина для кнопки No
        if (showNoButton) {
            noButtonWidth = noTextWidth + textPadding * 2;
            noButtonWidth = Math.max(noButtonWidth, minNoButtonWidth);
        }

        float buttonHeight = 28 * sdpScale * scaleFactor;
        float buttonCornerRadius = 8 * sdpScale * scaleFactor;
        float buttonSpacing = 5 * sdpScale * scaleFactor; // Расстояние между кнопками

        // Рассчитываем необходимую ширину диалога на основе контента
        float contentWidth = 0;
        float titleMaxWidth = 0;

        // Измеряем максимальную ширину заголовка
        textPaint.setTextSize(15 * sdpScale * scaleFactor);
        textPaint.setTypeface(Typeface.create("montserrat_bold", Typeface.BOLD));
        String[] titleLines = titleText.split("\n");
        for (String line : titleLines) {
            float lineWidth = textPaint.measureText(line);
            titleMaxWidth = Math.max(titleMaxWidth, lineWidth);
        }

        // Измеряем ширину описания
        textPaint.setTextSize(7 * sdpScale * scaleFactor);
        textPaint.setTypeface(Typeface.create("montserrat_regular", Typeface.NORMAL));
        float descWidth = textPaint.measureText(descText);

        // Максимальная ширина текстового контента
        float maxTextWidth = Math.max(titleMaxWidth, descWidth);

        // Ширина кнопок
        float buttonsWidth = 0;
        if (showNoButton) {
            contentWidth = maxTextWidth + 32 * sdpScale * scaleFactor; // Отступы слева и справа
            buttonsWidth = yesButtonWidth + noButtonWidth + buttonSpacing;
        } else {
            contentWidth = maxTextWidth + 32+64 * sdpScale * scaleFactor; // Отступы слева и справа
            buttonsWidth = yesButtonWidth;
        }
        buttonsWidth += 32 * sdpScale * scaleFactor; // Отступы слева и справа

        // Итоговая ширина диалога - максимум из ширины контента и ширины кнопок
        float requiredDialogWidth = Math.max(contentWidth, buttonsWidth);

        // Рассчитываем высоту диалога на основе контента
        float titleHeight = titleLines.length * (titleTextSize + (2 * sdpScale * scaleFactor) / 3);

        // Отступы теперь считаем относительно друг друга, а не от краев
        float titleToDescSpacing = (10 * sdpScale * scaleFactor) / 3; // Отступ между заголовком и описанием

        // ОТВЕТСТВЕННЫЙ ЗА СБЛИЖЕНИЕ - этот отступ регулирует расстояние между описанием и кнопками
        float descToButtonSpacing = (30 * sdpScale * scaleFactor) * textToButtonSpacing;

        // Отступы от краев диалога до контента (сделаем фиксированными)
        float verticalPadding = 10 * sdpScale * scaleFactor;

        // Общая высота контента (заголовок + описание + кнопки + отступы между ними)
        float contentHeight = titleHeight + titleToDescSpacing + descTextSize + descToButtonSpacing + buttonHeight;

        // Полная высота диалога = контент + отступы сверху и снизу
        float requiredDialogHeight = contentHeight + verticalPadding * 2;

        // Минимальная высота диалога
        float minDialogHeight = 110 * sdpScale * scaleFactor;
        requiredDialogHeight = Math.max(requiredDialogHeight, minDialogHeight);

        // Позиционирование диалога по центру экрана
        int dialogWidth = (int) requiredDialogWidth;
        int dialogHeight = (int) requiredDialogHeight;
        int dialogLeft = (getWidth() - dialogWidth) / 2;
        int dialogTop = ((getHeight() - dialogHeight) / 2) + 16;
        int dialogRight = dialogLeft + dialogWidth;
        int dialogBottom = dialogTop + dialogHeight + 16;

        float cornerRadius = 10 * sdpScale * scaleFactor;
        dialogRect.set(dialogLeft, dialogTop, dialogRight, dialogBottom);
        canvas.drawRoundRect(dialogRect, cornerRadius, cornerRadius, dialogBgPaint);

        LinearGradient highlightGradient = new LinearGradient(
                dialogLeft, dialogTop, dialogRight, dialogBottom,
                new int[]{HIGHLIGHT_COLOR, Color.TRANSPARENT},
                new float[]{0.4f, 1.0f},
                Shader.TileMode.CLAMP
        );
        dialogHighlightPaint.setShader(highlightGradient);
        canvas.drawRoundRect(dialogRect, cornerRadius, cornerRadius, dialogHighlightPaint);

        // Рисуем заголовок (начинаем от верхнего отступа)
        textPaint.setTextSize(15 * sdpScale * scaleFactor);
        textPaint.setColor(TEXT_COLOR_WHITE);
        textPaint.setTypeface(Typeface.create("montserrat_bold", Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.LEFT);

        float titleX = dialogLeft + 16 * sdpScale * scaleFactor;
        float currentY = dialogTop + verticalPadding + titleTextSize;

        for (String line : titleLines) {
            canvas.drawText(line, titleX, currentY, textPaint);
            currentY += titleTextSize + (2 * sdpScale * scaleFactor) / 3;
        }

        // Рисуем описание (после заголовка с отступом)
        textPaint.setTextSize(7 * sdpScale * scaleFactor);
        textPaint.setColor(TEXT_COLOR_GRAY);
        textPaint.setTypeface(Typeface.create("montserrat_regular", Typeface.NORMAL));

        // Возвращаем currentY к последней позиции заголовка и добавляем отступ
        currentY = dialogTop + verticalPadding + titleHeight + titleToDescSpacing + descTextSize;
        canvas.drawText(descText, titleX, currentY, textPaint);

        // Позиции кнопок (после описания с регулируемым отступом)
        float buttonY = dialogTop + verticalPadding + titleHeight + titleToDescSpacing + descTextSize + descToButtonSpacing;

        if (showNoButton) {
            // Центрируем кнопки по горизонтали
            float totalButtonsWidth = yesButtonWidth + noButtonWidth + buttonSpacing;
            float buttonsStartX = dialogLeft + (dialogWidth - totalButtonsWidth) / 2;

            // Рисуем синюю кнопку (Yes)
            buttonYesRect.set(buttonsStartX, buttonY, buttonsStartX + yesButtonWidth, buttonY + buttonHeight);
            canvas.drawRoundRect(buttonYesRect, buttonCornerRadius, buttonCornerRadius, buttonBluePaint);

            // Рисуем серую кнопку (No)
            buttonNoRect.set(buttonsStartX + yesButtonWidth + buttonSpacing, buttonY,
                    buttonsStartX + yesButtonWidth + buttonSpacing + noButtonWidth, buttonY + buttonHeight);
            canvas.drawRoundRect(buttonNoRect, buttonCornerRadius, buttonCornerRadius, buttonGrayPaint);

        } else {
            // Только одна кнопка (центрированная)
            float buttonsStartX = dialogLeft + 16 * sdpScale * scaleFactor;

            buttonYesRect.set(buttonsStartX, buttonY, buttonsStartX + yesButtonWidth, buttonY + buttonHeight);
            canvas.drawRoundRect(buttonYesRect, buttonCornerRadius, buttonCornerRadius, buttonBluePaint);
            buttonNoRect.setEmpty();
        }

        // Рисуем текст на кнопках
        textPaint.setTextSize(10 * sdpScale * scaleFactor);
        textPaint.setColor(TEXT_COLOR_WHITE);
        textPaint.setTypeface(Typeface.create("montserrat_bold", Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);

        float buttonTextY = buttonY + buttonHeight / 2 - (textPaint.descent() + textPaint.ascent()) / 2;

        canvas.drawText(buttonYesText, buttonYesRect.centerX(), buttonTextY, textPaint);

        if (showNoButton && !buttonNoRect.isEmpty()) {
            canvas.drawText(buttonNoText, buttonNoRect.centerX(), buttonTextY, textPaint);
        }
    }

    public boolean isYesButtonClicked(float x, float y) {
        return buttonYesRect.contains(x, y);
    }

    public boolean isNoButtonClicked(float x, float y) {
        return showNoButton && buttonNoRect.contains(x, y);
    }
}