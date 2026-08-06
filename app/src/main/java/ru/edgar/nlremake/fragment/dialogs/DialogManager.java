package ru.edgar.nlremake.fragment.dialogs;

import android.view.View;

import javax.annotation.Nullable;

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

    public void showErrorDialog(String name, String dname, String b1, View.OnClickListener click1, boolean isCheckBox, String dCheckBox) {
        getDialogFragment().showErrorDialog(name, dname, b1, click1, true, dCheckBox);
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

    public boolean getIsChecked() {
        return getDialogFragment().checkBox.isChecked();
    }
}
