package ru.edgar.nlremake.other;

import android.app.Activity;

public interface LauncherUiComponent {
    void init(Activity activity);
    default void show() {
        /* EDGAR 3.0 */
    };
    default void hide() {
        /* EDGAR 3.0 */
    };
}