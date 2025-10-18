package org.pacific_emis.surveys.ui.screens.splash;

import com.omegar.mvp.InjectViewState;

import org.pacific_emis.surveys.BuildConfig;
import org.pacific_emis.surveys.app_support.MicronesiaApplication;
import org.pacific_emis.surveys.core.ui.screens.base.BasePresenter;
import org.pacific_emis.surveys.domain.SettingsInteractor;
import org.pacific_emis.surveys.remote_settings.model.RemoteSettings;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class SplashPresenter extends BasePresenter<SplashView> {

    private final SettingsInteractor interactor = MicronesiaApplication.getInjection()
            .getAppComponent()
            .getSettingsInteractor();

    private final RemoteSettings remoteSettings = MicronesiaApplication.getInjection()
            .getRemoteSettingsComponent()
            .getRemoteSettings();

    SplashPresenter() {
        getViewState().requestAppPermissions();
    }

    public void onPermissionsGranted() {
        navigateToNextScreen();
    }

    private void navigateToNextScreen() {
        if (!interactor.isMasterPasswordSaved()) {
            getViewState().navigateToMasterPassword();
        } else if (interactor.getAppRegion() != BuildConfig.APP_REGION) {
            updateRemoteSettings();
        } else {
            getViewState().navigateToMenu();
        }
    }

    private void updateRemoteSettings() {
        interactor.setAppRegion(BuildConfig.APP_REGION);
        addDisposable(
                remoteSettings.forceFetch()
                        .flatMapCompletable(b -> interactor.loadDataFromAssets())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(d -> getViewState().showWaiting())
                        .subscribe(() -> {
                            remoteSettings.init(null);
                            getViewState().hideWaiting();
                            getViewState().navigateToMenu();
                        }, this::handleError));

    }

}
