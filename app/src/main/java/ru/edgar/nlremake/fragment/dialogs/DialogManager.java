package ru.edgar.nlremake.fragment.dialogs;

import android.view.View;
import android.widget.CheckBox;

import javax.annotation.Nullable;

public class DialogManager {

    private static DialogManager instance;
    private AuthFragment authFragment;

    private AuthEmailFragment authEmailFragment;
    private DialogFragment dialogFragment;


    public DialogManager() {
        instance = this;
        authFragment = new AuthFragment();
        authEmailFragment = new AuthEmailFragment();
        dialogFragment = new DialogFragment();
    }

    public void showDialog(String name, String dname, String b1, String b2, View.OnClickListener click1, View.OnClickListener click2) {
        getDialogFragment().showDialog(name, dname, b1, b2, click1, click2);
    }

    public void showErrorDialog(String name, String dname, String b1, View.OnClickListener click1, boolean isCheckBox, String dCheckBox) {
        getDialogFragment().showErrorDialog(name, dname, b1, click1, true, dCheckBox);
    }

    public void showDialogCheckBoxes(String name, String dname, String b1, View.OnClickListener click1, CheckBox[] checkBoxes) {
        getDialogFragment().showDialogCheckBoxes(name, dname, b1, click1, checkBoxes);
    }

    public void hideDialog() {
        getDialogFragment().hideDialog();
    }

    public void showAuthDialog(boolean isOnce) {
        getAuthFragment().showAuthDialog(isOnce);
    }

    public void hideAuthDialog() {
        getAuthFragment().hideAuthDialog();
    }

    public void showAuthEmailDialog() {
        getAuthEmailFragment().showAuthEmailDialog();
    }

    public void hideAuthEmailDialog() {
        getAuthEmailFragment().hideDialog();
    }


    public static DialogManager getDialogManager() {
        return instance;
    }

    private AuthFragment getAuthFragment() {
        return authFragment;
    }

    private AuthEmailFragment getAuthEmailFragment() {
        return authEmailFragment;
    }

    private DialogFragment getDialogFragment() {
        return dialogFragment;
    }

    public boolean getIsChecked() {
        return getDialogFragment().checkBox.isChecked();
    }
}
