package ru.edgar.nlremake.fragment.dialogs;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.slider.LabelFormatter;

import org.checkerframework.checker.units.qual.A;

import javax.annotation.Nullable;

import ru.edgar.nlremake.other.LauncherUiComponent;

public class DialogManager implements LauncherUiComponent {

    private AuthFragment authFragment;
    private AuthEmailFragment authEmailFragment;
    private AccountDialogFragment accountDialogFragment;
    private PromoDialogFragment promoDialogFragment;
    private CreateChasterFragment createChasterFragment;
    private DialogFragment dialogFragment;

    @Override
    public void init(Activity activity) {
        authFragment = new AuthFragment(activity);
        authEmailFragment = new AuthEmailFragment(activity);
        accountDialogFragment = new AccountDialogFragment(activity);
        promoDialogFragment = new PromoDialogFragment(activity);
        createChasterFragment = new CreateChasterFragment(activity);
        dialogFragment = new DialogFragment(activity);
    }

    public void changingButtonPriority(boolean isPriority) {
        getDialogFragment().changingButtonPriority(isPriority);
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
        getAuthEmailFragment().hideAuthEmailDialog();
    }

    public void showAccountDialog() {
        getAccountDialogFragment().showAccountDialog();
    }

    public void hideAccountDialog() {
        getAccountDialogFragment().hideAccountDialog();
    }

    public void showPromoDialog() {
        getPromoDialogFragment().showPromoDialog();
    }

    public void hidePromoDialog() {
        getPromoDialogFragment().hidePromoDialog();
    }

    public void showCreateChesterDialog() {
        getCreateChasterFragment().showCreateChasterDialog();
    }

    public void hideCreateChesterDialog(boolean isShowServers) {
        getCreateChasterFragment().hideCreateChasterDialog(isShowServers);
    }

    private AuthFragment getAuthFragment() {
        return authFragment;
    }

    private AuthEmailFragment getAuthEmailFragment() {
        return authEmailFragment;
    }

    private AccountDialogFragment getAccountDialogFragment() {
        return accountDialogFragment;
    }

    private PromoDialogFragment getPromoDialogFragment() {
        return promoDialogFragment;
    }

    private CreateChasterFragment getCreateChasterFragment() {
        return createChasterFragment;
    }

    private DialogFragment getDialogFragment() {
        return dialogFragment;
    }

    public boolean getIsChecked() {
        return getDialogFragment().checkBox.isChecked();
    }
}
