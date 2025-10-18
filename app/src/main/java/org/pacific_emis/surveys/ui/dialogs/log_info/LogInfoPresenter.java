package org.pacific_emis.surveys.ui.dialogs.log_info;

import com.omegar.mvp.InjectViewState;
import org.pacific_emis.surveys.app_support.MicronesiaApplication;
import org.pacific_emis.surveys.core.data.model.SurveyLog;
import org.pacific_emis.surveys.core.preferences.LocalSettings;
import org.pacific_emis.surveys.core.ui.screens.base.BasePresenter;

/**
 * Created by Xena Bolshakova on 22.12.2023.
 * Copyright (c) 2023 Omega https://omega-r.com
 */

@InjectViewState
public class LogInfoPresenter extends BasePresenter<LogInfoView> {

    private final LocalSettings localSettings = MicronesiaApplication.getInjection().getCoreComponent().getLocalSettings();

    public LogInfoPresenter(SurveyLog surveyLog) {
        getViewState().setTabletId(localSettings.getTabletId());
        getViewState().setSchoolId(surveyLog.getSchoolId());
        getViewState().setSurveyPeriod(surveyLog.getSurveyTag());
        getViewState().setCreateUser(surveyLog.getCreateUser());
        getViewState().setSurveyDateAndTime(surveyLog.getSurveyTime());
        getViewState().setSchoolName(surveyLog.getSchoolName());
        getViewState().setSurveyType(surveyLog.getSurveyType());
        getViewState().setDescription(surveyLog.getLogAction());
    }

}
