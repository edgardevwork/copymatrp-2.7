package ru.edgar.nlremake.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullHeightVideoView extends VideoView {
    public FullHeightVideoView(Context context) {
        super(context);
    }

    public FullHeightVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullHeightVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);

        // Adjust the aspect ratio
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();

        // Calculate the aspect ratio of the video
        int aspectRatioWidth = width;
        int aspectRatioHeight = height;

        if (aspectRatioWidth > 0 && aspectRatioHeight > 0) {
            if (childWidthSize < childHeightSize * aspectRatioWidth / aspectRatioHeight) {
                height = childWidthSize * aspectRatioHeight / aspectRatioWidth;
            } else {
                width = childHeightSize * aspectRatioWidth / aspectRatioHeight;
            }
        }

        setMeasuredDimension(width, height);
    }
}