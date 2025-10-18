package org.pacific_emis.surveys.ui.dialogs.log_info;

import com.omegar.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.omegar.mvp.viewstate.strategy.StateStrategyType;

import org.pacific_emis.surveys.core.preferences.entities.LogAction;
import org.pacific_emis.surveys.core.preferences.entities.SurveyType;
import org.pacific_emis.surveys.core.ui.screens.base.BaseView;

/**
 * Created by Xena Bolshakova on 22.12.2023.
 * Copyright (c) 2023 Omega https://omega-r.com
 */

public interface LogInfoView extends BaseView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setTabletId(String tabletId);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setSchoolId(String schoolId);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setSurveyPeriod(String surveyCreated);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setCreateUser(String createUser);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setSurveyDateAndTime(String surveyDateAndTime);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setSchoolName(String schoolName);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setSurveyType(SurveyType surveyType);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setDescription(LogAction logAction);

}
