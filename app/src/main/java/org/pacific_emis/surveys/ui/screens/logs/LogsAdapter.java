package org.pacific_emis.surveys.ui.screens.logs;

import android.view.ViewGroup;
import android.widget.TextView;
import org.pacific_emis.surveys.R;
import org.pacific_emis.surveys.core.data.model.SurveyLog;
import org.pacific_emis.surveys.core.preferences.entities.SurveyType;
import org.pacific_emis.surveys.core.ui.screens.base.BaseAdapter;

import butterknife.BindView;

public class LogsAdapter extends BaseAdapter<SurveyLog> {

    private OnLogItemClickListener itemClickListener;

    public LogsAdapter(OnItemClickListener<SurveyLog> clickListener, OnLogItemClickListener itemClickListener) {
        super(clickListener);
        this.itemClickListener = itemClickListener;
    }

    @Override
    protected LogsViewHolder provideViewHolder(ViewGroup parent) {
        return new LogsViewHolder(parent);
    }

    class LogsViewHolder extends ViewHolder {

        @BindView(R.id.textview_edit_date)
        TextView editedDateTextView;

        @BindView(R.id.textview_name_school)
        TextView schoolNameTextView;

        @BindView(R.id.textview_period)
        TextView periodTextView;

        @BindView(R.id.textview_survey_log_type)
        TextView surveyLogTypeTextView;

        @BindView(R.id.textview_log_action)
        TextView logActionTextView;

        LogsViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_logs);
        }

        @Override
        public void onBind(SurveyLog surveyLog) {
            editedDateTextView.setText(surveyLog.getSurveyTime());
            schoolNameTextView.setText(surveyLog.getSchoolName());
            periodTextView.setText(surveyLog.getSurveyTag());
            surveyLogTypeTextView.setText(getSurveyTypeText(surveyLog.getSurveyType()));
            logActionTextView.setTextColor(surveyLog.getLogAction().getColor().getColorInt(getContext()));
            surveyLog.getLogAction().getName().applyTo(logActionTextView);
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

    }

    public interface OnLogItemClickListener {
        void onLogItemClick(SurveyLog surveyLog);
    }

}
