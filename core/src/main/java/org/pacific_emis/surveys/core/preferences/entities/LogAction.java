package org.pacific_emis.surveys.core.preferences.entities;

import com.omega_r.libs.omegatypes.Color;
import com.omega_r.libs.omegatypes.Text;
import org.pacific_emis.surveys.core.R;

public enum LogAction {
    CREATED(Text.from(R.string.log_action_created), Color.fromResource(R.color.green)),
    EDITED(Text.from(R.string.log_action_edited), Color.fromResource(R.color.yellow_a700)),
    DELETED(Text.from(R.string.log_action_deleted), Color.fromResource(R.color.red_400)),
    FETCH(Text.from(R.string.log_action_fetch), Color.fromResource(R.color.blue_600)),
    MERGED_TO(Text.from(R.string.log_action_merged_to), Color.fromResource(R.color.orange)),
    MERGED_FROM(Text.from(R.string.log_action_merged_from), Color.fromResource(R.color.orange));

    private final Text name;

    private final Color color;

    public static LogAction getOrDefault(String value) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return LogAction.DELETED;
        }
    }

    LogAction(Text name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Text getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
