package org.pacific_emis.surveys.ui.screens.password;

import com.omega_r.libs.omegatypes.Text;
import com.omegar.mvp.InjectViewState;

import org.pacific_emis.surveys.R;
import org.pacific_emis.surveys.app_support.MicronesiaApplication;
import org.pacific_emis.surveys.core.BuildConfig;
import org.pacific_emis.surveys.core.ui.screens.base.BasePresenter;
import org.pacific_emis.surveys.domain.SettingsInteractor;

@InjectViewState
public class PasswordPresenter extends BasePresenter<PasswordView> {

    private final SettingsInteractor settingsInteractor = MicronesiaApplication.getInjection().getAppComponent().getSettingsInteractor();
    private String password = null;
    private String confirm = null;

    public void onNewPasswordInput(String input) {
        password = null;
        if (input.length() >= BuildConfig.MIN_PASSWORD_LENGTH) {
            if (confirm != null && !input.equals(confirm)) showConfirmPasswordError();
            password = input;
            getViewState().setEnterPasswordErrorVisible(false);
        } else {
            showEnterPasswordError();
        }
    }

    public void onConfirmPasswordInput(String input) {
        confirm = input;
        if (password != null) {
            if (password.equals(input)) {
                getViewState().setConformButtonEnabled(true);
                getViewState().setConfirmPasswordErrorVisible(false);
            } else {
                showConfirmPasswordError();
            }
        }
    }

    public void onConfirmPressed() {
        settingsInteractor.setMasterPassword(password);
        getViewState().navigateToRegion();
    }
    
    private void showEnterPasswordError() {
        getViewState().setEnterPasswordErrorVisible(true);
        getViewState().setConfirmPasswordErrorVisible(false);
        getViewState().setConformButtonEnabled(false);
    }

    private void showConfirmPasswordError() {
        getViewState().setConfirmPasswordErrorVisible(true);
        getViewState().setConformButtonEnabled(false);
    }
}
