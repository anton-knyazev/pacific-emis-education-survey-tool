package org.pacific_emis.surveys.remote_settings.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import org.pacific_emis.surveys.core.preferences.LocalSettings;
import org.pacific_emis.surveys.core.preferences.entities.OperatingMode;
import org.pacific_emis.surveys.core.utils.VoidArgFunction;
import org.pacific_emis.surveys.core.utils.VoidFunction;
import org.pacific_emis.surveys.remote_settings.BuildConfig;
import org.pacific_emis.surveys.remote_storage.data.storage.RemoteStorage;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.SingleSubject;

public class RemoteSettings {
    private static final String TAG = RemoteSettings.class.getName();
    private static final long TIMEOUT_FETCH_SEC = 5;
    private static final String KEY_MASTER_PASSWORD = "master_password";
    private static final String KEY_LOGO_URL = "logo_url";
    private static final String KEY_EXPORT_TO_EXCEL = "can_export_to_excel";
    private static final String KEY_APP_TITLE = "app_title";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_SERVICE_ACCOUNT_TEST = "service_account_key_test";
    private static final String KEY_SERVICE_ACCOUNT_PROD = "service_account_key_prod";
    private static final String KEY_OPERATING_MODE = "operating_mode";
    private static final String KEY_EMIS_URL = "emis_url";
    private static final String KEY_EMIS_USER = "emis_user";
    private static final String KEY_EMIS_PASSWORD = "emis_password";
    private static final String KEY_DELETING_CLOUDFILE_MODE = "deleting_cloudfile_also_deletes_on_tablet";
    private static final String KEY_SERVICE_ACCOUNT_TRAIN = "service_account_key_train";

    private static final String KEY_SHARED_FOLDER_ID_PROD = "cloudfiles_shared_drive_id_prod";
    private static final String KEY_SHARED_FOLDER_ID_TRAIN = "cloudfiles_shared_drive_id_train";
    private static final String KEY_SHARED_FOLDER_ID_TEST = "cloudfiles_shared_drive_id_test";

    private final LocalSettings localSettings;
    private final RemoteStorage remoteStorage;
    private final Executor executor = Executors.newCachedThreadPool();
    private final Gson gson = new Gson();
    private Disposable fetchDisposable;


    public RemoteSettings(Context appContext, LocalSettings localSettings, RemoteStorage remoteStorage) {
        this.localSettings = localSettings;
        this.remoteStorage = remoteStorage;
    }

    private FirebaseRemoteConfig getRemoteConfig() {
        return FirebaseRemoteConfig.getInstance();
    }

    public void init(@Nullable Runnable onSuccess) {
        init(BuildConfig.INTERVAL_FETCH_SEC, onSuccess);
    }

    private void init(long fetchInterval, @Nullable Runnable onSuccess) {

        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(TIMEOUT_FETCH_SEC)
                .setMinimumFetchIntervalInSeconds(fetchInterval)
                .build();
        getRemoteConfig().setConfigSettingsAsync(settings)
                .addOnSuccessListener(executor, v -> {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                })
                .addOnFailureListener(executor, e -> Log.e(TAG, "setConfigSettingsAsync failed", e));

    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void fetchAsync(int currentVersionCode) {
        SingleSubject<Boolean> subject = SingleSubject.create();
        int oldCurrentVersionCode = localSettings.getCurrentVersionCode();

        fetch(subject, oldCurrentVersionCode != currentVersionCode);

        subject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    if (success) localSettings.setCurrentVersionCode(currentVersionCode);
                }, throwable -> {
                    Log.e("TAG", "Fetch failed", throwable);
                });
    }

    public Single<Boolean> forceFetch() {
        SingleSubject<Boolean> subject = SingleSubject.create();
        init(0, () -> fetch(subject, true));
        return subject;
    }

    private void fetch(@Nullable SingleSubject<Boolean> subject, boolean forcedByUser) {
        final VoidFunction<Boolean> notifyCompleted = isSuccess -> {
            if (subject != null) {
                subject.onSuccess(isSuccess);
            }
        };
        getRemoteConfig().fetchAndActivate()
                .addOnSuccessListener(executor, areUpdated -> {
                    if (areUpdated || forcedByUser) {
                        parseRemoteSettings(forcedByUser);
                        notifyCompleted.apply(true);
                    } else {
                        Log.i(TAG, "Fetch from local storage");
                        notifyCompleted.apply(false);
                    }
                })
                .addOnFailureListener(executor, e -> {
                    Log.e(TAG, "Fetch failed", e);
                    notifyCompleted.apply(false);
                });
    }

    private void parseRemoteSettings(boolean forcedByUser) {
        parseForceableString(
                KEY_MASTER_PASSWORD,
                forcedByUser,
                localSettings::setMasterPassword,
                localSettings::isMasterPasswordSaved
        );
        parseForceableString(KEY_LOGO_URL, forcedByUser, localSettings::setLogoPath, localSettings::isLogoSaved);
        parseForceableBoolean(
                KEY_EXPORT_TO_EXCEL,
                forcedByUser,
                localSettings::setExportToExcelEnabled,
                localSettings::isExportToExcelEnabled
        );
        parseForceableString(KEY_APP_TITLE, forcedByUser, localSettings::setAppName, localSettings::isAppNameSaved);
        parseForceableString(KEY_CONTACT, forcedByUser, localSettings::setContactName, localSettings::isContactNameSaved);
        parseForceableString(KEY_SERVICE_ACCOUNT_TEST, forcedByUser, cert64 -> {
            localSettings.setServiceAccountKeyDev(decodeBase64(cert64));
            remoteStorage.refreshCredentials();
        }, () -> localSettings.getServiceAccountKeyDev() == null);
        parseForceableString(KEY_SERVICE_ACCOUNT_PROD, forcedByUser, cert64 -> {
            localSettings.setServiceAccountKeyProd(decodeBase64(cert64));
            remoteStorage.refreshCredentials();
        }, () -> localSettings.getServiceAccountKeyProd() == null);
        parseForceableString(KEY_OPERATING_MODE, forcedByUser, value -> {
            OperatingMode operatingMode = OperatingMode.createFromSerializedName(value);
            localSettings.setOperatingMode(operatingMode);
            remoteStorage.refreshCredentials();
        }, localSettings::isOperatingModeSaved);
        parseForceableString(KEY_EMIS_URL, forcedByUser, localSettings::setEmisApiUrl, localSettings::isEmisApiUrlSaved);
        parseForceableString(KEY_EMIS_USER, forcedByUser, localSettings::setEmisUser, localSettings::isEmisUserSaved);
        parseForceableString(KEY_EMIS_PASSWORD, forcedByUser, localSettings::setEmisPassword, localSettings::isEmisPasswordSaved);
        parseForceableBoolean(KEY_DELETING_CLOUDFILE_MODE, forcedByUser, localSettings::setDeletingCloudFileModeEnabled, localSettings::isDeletingCloudFileModeEnabled);
        parseForceableString(KEY_SERVICE_ACCOUNT_TRAIN, forcedByUser, cert64 -> {
            localSettings.setServiceAccountKeyTrain(decodeBase64(cert64));
            remoteStorage.refreshCredentials();
        }, () -> localSettings.getServiceAccountKeyTrain() == null);

        parseForceableString(KEY_SHARED_FOLDER_ID_PROD, forcedByUser, folderId -> {
            localSettings.setGoogleDriveSharedFolderId(OperatingMode.PROD, folderId);
            remoteStorage.refreshCredentials();
        }, () -> localSettings.getGoogleDriveSharedFolderId(OperatingMode.PROD) == null);

        parseForceableString(KEY_SHARED_FOLDER_ID_TRAIN, forcedByUser, folderId -> {
            localSettings.setGoogleDriveSharedFolderId(OperatingMode.TRAIN, folderId);
            remoteStorage.refreshCredentials();
        }, () -> localSettings.getGoogleDriveSharedFolderId(OperatingMode.TRAIN) == null);

        parseForceableString(KEY_SHARED_FOLDER_ID_TEST, forcedByUser, folderId -> {
            localSettings.setGoogleDriveSharedFolderId(OperatingMode.TEST, folderId);
            remoteStorage.refreshCredentials();
        }, () -> localSettings.getGoogleDriveSharedFolderId(OperatingMode.TEST) == null);

    }

    private String decodeBase64(String decodedString) {
        return new String(Base64.decode(decodedString, Base64.DEFAULT));
    }

    private void parseForceableBoolean(String key,
                                       boolean forcedByUser,
                                       VoidFunction<Boolean> setFunction,
                                       VoidArgFunction<Boolean> existenceCheckFunction) {
        String value = getRemoteConfig().getString(key);
        if (!TextUtils.isEmpty(value)) {
            ForceableBoolean remoteValue = gson.fromJson(value, ForceableBoolean.class);

            if (remoteValue == null) {
                return;
            }

            if (forcedByUser || remoteValue.isForce() || !existenceCheckFunction.apply()) {
                setFunction.apply(remoteValue.getValue());
            }
        }
    }

    private void parseForceableString(String key,
                                      boolean forcedByUser,
                                      VoidFunction<String> setFunction,
                                      VoidArgFunction<Boolean> existenceCheckFunction) {
        String keys = getRemoteConfig().getAll().keySet().toString();
        String value = getRemoteConfig().getString(key);
        if (!TextUtils.isEmpty(value)) {
            ForceableString remoteValue = gson.fromJson(value, ForceableString.class);

            if (remoteValue == null) {
                return;
            }

            if (forcedByUser && remoteValue.isForce() || !existenceCheckFunction.apply()) {
                setFunction.apply(remoteValue.getValue());
            }
        }
    }
}
