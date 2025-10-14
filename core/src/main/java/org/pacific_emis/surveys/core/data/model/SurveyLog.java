package org.pacific_emis.surveys.core.data.model;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.pacific_emis.surveys.core.preferences.entities.AppRegion;
import org.pacific_emis.surveys.core.preferences.entities.LogAction;
import org.pacific_emis.surveys.core.preferences.entities.SurveyType;
import java.io.Serializable;

public interface SurveyLog extends Serializable {

    @NonNull
    SurveyType getSurveyType();

    @NotNull
    String getCreateUser();

    @NonNull
    String getSchoolName();

    @NotNull
    String getSchoolId();

    @NotNull
    String getSurveyTag();

    @NotNull
    String getSurveyTime();

    @NotNull
    String getSurveyCreated();

    LogAction getLogAction();

    @NotNull
    AppRegion getAppRegion();

}
