package org.pacific_emis.surveys.ui.screens.password;

import com.omega_r.libs.omegatypes.Text;
import com.omegar.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.omegar.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.omegar.mvp.viewstate.strategy.StateStrategyType;

import org.pacific_emis.surveys.core.ui.screens.base.BaseView;

public interface PasswordView extends BaseView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setConfirmPasswordErrorVisible(boolean visible);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setEnterPasswordErrorVisible(boolean visible);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void navigateToRegion();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setConformButtonEnabled(boolean enabled);
}
