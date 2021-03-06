package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;

public interface SettingsView extends MvpView {
    @StateStrategyType(SkipStrategy.class)
    void setCheckedStoreKeyPairOption(boolean value);
    void setEnablePushOption(boolean value);
    void displayCurrentNotificationFacade(PushNotificationServiceFacade facade);
    void startProgress();
    void stopProgress();

    void showExitDialog();

    @StateStrategyType(SkipStrategy.class)
    void showTEENotSupportedDialog();

    @StateStrategyType(SkipStrategy.class)
    void showVerifyingDialog();

    @StateStrategyType(SkipStrategy.class)
    void hideVerifyingDialog();

    @StateStrategyType(SkipStrategy.class)
    void showMessage(String message);

    @StateStrategyType(SkipStrategy.class)
    void showMessage(int messageResource);

    @StateStrategyType(SkipStrategy.class)
    void showSelectServiceDialog(List<PushNotificationServiceFacade> facades, PushNotificationServiceFacade current);
}
