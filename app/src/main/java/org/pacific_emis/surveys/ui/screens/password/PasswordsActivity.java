package org.pacific_emis.surveys.ui.screens.password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.omegar.mvp.presenter.InjectPresenter;

import org.pacific_emis.surveys.R;
import org.pacific_emis.surveys.core.ui.screens.base.BaseActivity;
import org.pacific_emis.surveys.core.ui.views.BottomNavigatorView;
import org.pacific_emis.surveys.core.utils.TextWatcherAdapter;

public class PasswordsActivity extends BaseActivity implements PasswordView, BottomNavigatorView.Listener {

    private static final String EXTRA_IS_SETTINGS = "EXTRA_IS_SETTINGS";

    @InjectPresenter
    PasswordPresenter presenter;

    private TextView errorEnterPasswordTextView;
    private TextView errorConfirmPasswordTextView;
    private Button confirmButton;
    private boolean isInSettingsContext;

    private final TextWatcher newPassTextWatcher = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            presenter.onNewPasswordInput(s.toString());
        }
    };

    private final TextWatcher confirmPassTextWatcher = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            presenter.onConfirmPasswordInput(s.toString());
        }
    };

    @NonNull
    public static Intent createIntent(Context context, boolean isSettings) {
        return new Intent(context, PasswordsActivity.class).putExtra(EXTRA_IS_SETTINGS, isSettings);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isInSettingsContext = getIntent().getBooleanExtra(EXTRA_IS_SETTINGS, false);
        super.onCreate(savedInstanceState);
        setTitle(R.string.label_passwords);
        bindViews();
    }

    private void bindViews() {
        setTextWatcher(R.id.textinputedittext_new_pass, newPassTextWatcher);
        setTextWatcher(R.id.textinputedittext_confirm_pass, confirmPassTextWatcher);
        errorEnterPasswordTextView = findViewById(R.id.textview_short_password);
        errorConfirmPasswordTextView = findViewById(R.id.textview_incorrect);

        BottomNavigatorView navigatorView = findViewById(R.id.bottomnavigatorview);

        if (navigatorView != null) {
            navigatorView.setListener(this);
        }

        confirmButton = findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(v -> presenter.onConfirmPressed());
    }

    private void setTextWatcher(@IdRes int idRes, TextWatcher watcher) {
        ((EditText) findViewById(idRes)).addTextChangedListener(watcher);
    }

    @Override
    protected int getContentView() {
        return isInSettingsContext ? R.layout.activity_password_settings : R.layout.activity_password_wizard;
    }

    @Override
    public void setConfirmPasswordErrorVisible(boolean visible) {
        errorConfirmPasswordTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEnterPasswordErrorVisible(boolean visible) {
        errorEnterPasswordTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void navigateToRegion() {
        if (isInSettingsContext) {
            finish();
        }
    }

    @Override
    public void setConformButtonEnabled(boolean enabled) {
        confirmButton.setEnabled(enabled);
    }

    @Override
    public void onPrevPressed() {
        // nothing
    }

    @Override
    public void onNextPressed() {
        presenter.onConfirmPressed();
    }
}
