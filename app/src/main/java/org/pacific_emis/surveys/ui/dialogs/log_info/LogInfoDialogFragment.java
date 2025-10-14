package org.pacific_emis.surveys.ui.dialogs.log_info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.omegar.mvp.presenter.InjectPresenter;
import com.omegar.mvp.presenter.ProvidePresenter;
import org.pacific_emis.surveys.R;
import org.pacific_emis.surveys.core.data.model.SurveyLog;
import org.pacific_emis.surveys.core.preferences.entities.LogAction;
import org.pacific_emis.surveys.core.preferences.entities.SurveyType;
import org.pacific_emis.surveys.core.ui.screens.base.BaseDialogFragment;
import org.pacific_emis.surveys.core.utils.Constants;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Xena Bolshakova on 22.12.2023.
 * Copyright (c) 2023 Omega https://omega-r.com
 */

public class LogInfoDialogFragment extends BaseDialogFragment implements LogInfoView {

    private static final String KEY_LOG_INFO_DATA = "KEY_LOG_INFO_DATA";

    @InjectPresenter
    LogInfoPresenter presenter;

    @BindView(R.id.textview_tablet_id)
    TextView tabletIdTextView;

    @BindView(R.id.textview_school_id)
    TextView schoolIdTextView;

    @BindView(R.id.textview_period)
    TextView periodTextView;

    @BindView(R.id.textview_user)
    TextView userTextView;

    @BindView(R.id.textview_date_and_time)
    TextView dateAndTimeTextView;

    @BindView(R.id.textview_school_name)
    TextView schoolNameTextView;

    @BindView(R.id.textview_description)
    TextView descriptionTextView;

    @BindView(R.id.textview_type)
    TextView typeTextView;

    public static LogInfoDialogFragment create(SurveyLog surveyLog) {
        LogInfoDialogFragment dialog = new LogInfoDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_LOG_INFO_DATA, surveyLog);
        dialog.setArguments(args);
        return dialog;
    }

    @ProvidePresenter
    LogInfoPresenter providePresenter() {
        Bundle bundle = getArguments();
        if (bundle == null) throw new RuntimeException(Constants.Errors.WRONG_FRAGMENT_ARGS);
        SurveyLog data = (SurveyLog) bundle.getSerializable(KEY_LOG_INFO_DATA);
        if (data == null) throw new RuntimeException(Constants.Errors.WRONG_FRAGMENT_ARGS);
        return new LogInfoPresenter(data);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCancelable(false);
        return inflater.inflate(R.layout.dialog_details_log_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(v -> dismiss());
    }

    @OnClick(R.id.button_close_dialog)
    void onCloseButtonPressed() {
        dismiss();
    }

    private String getSurveyTypeText(SurveyType surveyType) {
        switch (surveyType) {
            case SCHOOL_ACCREDITATION:
                return getString(R.string.title_survey_type_school_accreditation);
            case WASH:
                return getString(R.string.title_survey_type_wash);
        }
        throw new IllegalStateException();
    }

    @Override
    public void setTabletId(String tabletId) {
        tabletIdTextView.setText(tabletId);
    }

    @Override
    public void setSchoolId(String schoolId) {
        schoolIdTextView.setText(schoolId);
    }

    @Override
    public void setSurveyPeriod(String surveyTag) {
        periodTextView.setText(surveyTag);
    }

    @Override
    public void setCreateUser(String createUser) {
        userTextView.setText(createUser);
    }

    @Override
    public void setSurveyDateAndTime(String surveyDateAndTime) {
        dateAndTimeTextView.setText(surveyDateAndTime);
    }

    @Override
    public void setSchoolName(String schoolName) {
        schoolNameTextView.setText(schoolName);
    }

    @Override
    public void setSurveyType(SurveyType surveyType) {
        typeTextView.setText(getSurveyTypeText(surveyType));
    }

    @Override
    public void setDescription(LogAction logAction) {
        logAction.getName().applyTo(descriptionTextView);
        descriptionTextView.setTextColor(logAction.getColor().getColorInt(getActivity()));
    }
}
