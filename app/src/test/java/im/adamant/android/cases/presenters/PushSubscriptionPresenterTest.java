package im.adamant.android.cases.presenters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.DisabledNotificationServiceFacade;
import im.adamant.android.interactors.push.FCMNotificationServiceFacade;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.ui.mvp_view.PushSubscriptionView;
import im.adamant.android.ui.presenters.PushSubscriptionPresenter;
import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class PushSubscriptionPresenterTest {
    private PushSubscriptionView view;
    private PushSubscriptionPresenter presenter;
    private SwitchPushNotificationServiceInteractor interactor;
    private CompositeDisposable compositeDisposable;
    private Map<SupportedPushNotificationFacadeType, PushNotificationServiceFacade> facades = new HashMap<>();
    private PushNotificationServiceFacade disabledFacade = mock(DisabledNotificationServiceFacade.class);
    private FCMNotificationServiceFacade fcmFacade = mock(FCMNotificationServiceFacade.class);

    @Before
    public void setUp() {
        view = mock(PushSubscriptionView.class);
        interactor = mock(SwitchPushNotificationServiceInteractor.class);
        compositeDisposable = new CompositeDisposable();
        presenter = new PushSubscriptionPresenter(interactor, compositeDisposable);
        facades.put(SupportedPushNotificationFacadeType.FCM, fcmFacade);
        facades.put(SupportedPushNotificationFacadeType.DISABLED, disabledFacade);
    }

    @Test
    public void testOnClickSetNewPushServiceFail() {
        when(disabledFacade.getFacadeType()).thenReturn(SupportedPushNotificationFacadeType.DISABLED);
        when(fcmFacade.getFacadeType()).thenReturn(SupportedPushNotificationFacadeType.FCM);

        when(interactor.getFacades()).thenReturn(facades);
        when(interactor.getCurrentFacade()).thenReturn(disabledFacade);
        when(interactor.changeNotificationFacade(SupportedPushNotificationFacadeType.FCM)).thenReturn(Completable.error(IOException::new));

        presenter.attachView(view);
        presenter.onClickSetNewPushService(fcmFacade);

        ArgumentCaptor<Boolean> enablePushArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(view, times(2)).setEnablePushServiceTypeOption(enablePushArgumentCaptor.capture());
        Assert.assertEquals(false, enablePushArgumentCaptor.getAllValues().get(0));
        Assert.assertEquals(true, enablePushArgumentCaptor.getAllValues().get(1));

        verify(view).startProgress();
        verify(view).stopProgress();
        verify(view).showMessage(any());
        verify(view, times(2)).displayCurrentNotificationFacade(disabledFacade);
    }
}
