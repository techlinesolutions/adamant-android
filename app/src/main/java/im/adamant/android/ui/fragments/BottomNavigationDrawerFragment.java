package im.adamant.android.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.ui.presenters.MainPresenter;

//TODO: Parrent class must be the BaseBottomFragment
public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {
    @Inject
    AdamantApiWrapper api;

    MainPresenter mainPresenter;

    @Inject
    Avatar avatar;

    @Inject
    AccountInteractor accountInteractor;

    @BindView(R.id.fragment_bottom_navigation_iv_avatar)
    ImageView avatarView;

    @BindView(R.id.fragment_bottom_navigation_tv_address)
    TextView addressView;

    @BindView(R.id.fragment_bottom_navigation_tv_balance)
    TextView balanceView;

    @BindView(R.id.fragment_bottom_navigation_nv_menu)
    NavigationView navigationView;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    public void setMainPresenter(MainPresenter mainPresenter) {
        this.mainPresenter = mainPresenter;
    }

    private Unbinder unbinder;

    //TODO: Think about whether there is a presenter and a refactor code
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (api.isAuthorized()){
            avatar.build(
                        api.getKeyPair().getPublicKeyString().toLowerCase(),
                        (int) getResources().getDimension(R.dimen.fragment_bottom_navigation_avatar_size)
                ).subscribe(bitmap -> {
                    avatarView.setImageBitmap(bitmap);
            });

            addressView.setText(api.getAccount().getAddress());

            accountInteractor
                    .getAdamantBalance()
                    .subscribe(balance -> {
                        balanceView.setText(balance.toString() + " " + getString(R.string.adm_currency_abbr));
                    });
        }

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.navigation_chats: {
                    mainPresenter.onSelectedChatsScreen();
                }
                break;
                case R.id.navigation_wallet: {
                    mainPresenter.onSelectedWalletScreen();
                }
                break;
                case R.id.navigation_settings: {
                    mainPresenter.onSelectedSettingsScreen();
                }
                break;
            }

            dismiss();
            return true;
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
