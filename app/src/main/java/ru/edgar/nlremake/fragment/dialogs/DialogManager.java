package ru.edgar.nlremake.fragment.dialogs;

import android.view.View;

public class DialogManager {

    private static DialogManager instance;

    AuthFragment authFragment;

    DialogFragment dialogFragment;

    public DialogManager() {
        instance = this;
        authFragment = new AuthFragment();
        dialogFragment = new DialogFragment();
    }

    public void showDialog(String name, String dname, String b1, String b2, View.OnClickListener click1, View.OnClickListener click2) {
        getDialogFragment().showDialog(name, dname, b1, b2, click1, click2);
    }

    public void hideDialog() {
        getDialogFragment().hideDialog();
    }

    public void showAuthDialog() {
        getAuthFragment().showAuthDialog();
    }

    public void hideAuthDialog() {
        getAuthFragment().hideAuthDialog();
    }


    public static DialogManager getDialogManager() {
        return instance;
    }

    private AuthFragment getAuthFragment() {
        return authFragment;
    }

    private DialogFragment getDialogFragment() {
        return dialogFragment;
    }
}
