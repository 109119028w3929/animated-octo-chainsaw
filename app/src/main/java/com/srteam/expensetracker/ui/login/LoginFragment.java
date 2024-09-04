package com.srteam.expensetracker.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.srteam.expensetracker.ExpenseTrackerApp;
import com.srteam.expensetracker.R;
import com.srteam.expensetracker.adapters.WelcomePagerAdapter;
import com.srteam.expensetracker.custom.CrossfadePageTransformer;
import com.srteam.expensetracker.ui.BaseFragment;
import com.srteam.expensetracker.ui.MainActivity;
import com.viewpagerindicator.CirclePageIndicator;

public class LoginFragment extends BaseFragment implements View.OnClickListener  {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private static final int RC_SIGN_IN = 0;

    private ViewPager vpWelcome;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        vpWelcome = (ViewPager)rootView.findViewById(R.id.vp_welcome);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WelcomePagerAdapter welcomePagerAdapter = new WelcomePagerAdapter(getChildFragmentManager());
        vpWelcome.setAdapter(welcomePagerAdapter);
        vpWelcome.setPageTransformer(true, new CrossfadePageTransformer());
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)getView().findViewById(R.id.cpi_welcome);
        circlePageIndicator.setViewPager(vpWelcome);

//        getView().findViewById(R.id.sign_in_button).setOnClickListener(this);
        getView().findViewById(R.id.sign_in).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
//        mGoogleApiClient.disconnect();
    }

//    @Override
//    public void onConnected(Bundle bundle) {
//        Log.d(TAG, "onConnected:" + bundle);
//        mShouldResolve = false;
//
//        // Show the signed-in UI
//        showSignedInUI();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in) {
            showSignedInUI();
        }
    }




    private void showSignedInUI() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.already_accepted_user_key), true);
        editor.apply();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
