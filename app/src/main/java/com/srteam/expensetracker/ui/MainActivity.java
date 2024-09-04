package com.srteam.expensetracker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;

import com.google.android.material.appbar.AppBarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.srteam.expensetracker.IncomeActivity;
import com.srteam.expensetracker.IncomeFragment;
import com.srteam.expensetracker.R;
import com.srteam.expensetracker.entities.Expense;
import com.srteam.expensetracker.interfaces.IDateMode;
import com.srteam.expensetracker.interfaces.IMainActivityListener;
import com.srteam.expensetracker.signinActivity;
import com.srteam.expensetracker.ui.ExpenseCalculator.ExCalculatorActivity;
import com.srteam.expensetracker.ui.categories.CategoriesFragment;
import com.srteam.expensetracker.ui.expenses.ExpensesContainerFragment;
import com.srteam.expensetracker.ui.help.HelpActivity;
import com.srteam.expensetracker.ui.history.HistoryFragment;
import com.srteam.expensetracker.ui.reminders.ReminderFragment;
import com.srteam.expensetracker.ui.settings.SettingsActivity;
import com.srteam.expensetracker.ui.statistics.StatisticsFragment;
import com.srteam.expensetracker.utils.AppUtility;
import com.srteam.expensetracker.utils.DateUtils;
import com.srteam.expensetracker.utils.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IMainActivityListener {

    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_TABS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {
    }

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_TABS = 1;
    public static final String NAVIGATION_POSITION = "navigation_position";

    private int mCurrentMode = NAVIGATION_MODE_STANDARD;
    private int idSelectedNavigationItem;

    private DrawerLayout mainDrawerLayout;
    private NavigationView mainNavigationView;
    private Toolbar mToolbar;
    private TabLayout mainTabLayout;
    private FloatingActionButton mFloatingActionButton;

    // Expenses Summary related views
    private LinearLayout llExpensesSummary;
    private TextView tvDate;
    private TextView tvDescription;
    private TextView tv_username;
    private TextView tvTotal;
    public static final String EMPTY_STRING = "";
//    private InterstitialAd mInterstitialAd;
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setUpDrawer();
        setUpToolbar();
        if (savedInstanceState != null) {
            int menuItemId = savedInstanceState.getInt(NAVIGATION_POSITION);
            mainNavigationView.setCheckedItem(menuItemId);
            mainNavigationView.getMenu().performIdentifierAction(menuItemId, 0);
        } else {
            mainNavigationView.getMenu().performIdentifierAction(R.id.nav_expenses, 0);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String usernamedemo = sharedPreferences.getString("username", "");
        tv_username.setText("Welcome, " + usernamedemo);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }

    // Handle permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do something
            } else {
                // Permission denied, show an explanation
            }
        }
    }

    @NavigationMode
    public int getNavigationMode() {
        return mCurrentMode;
    }

    private void initUI() {
        mainDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mainTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mainNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_main);
        llExpensesSummary = (LinearLayout) findViewById(R.id.ll_expense_container);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tv_username = (TextView) findViewById(R.id.tv_username);

        showBannerInterstitialAd();
    }

    private void setUpDrawer() {
        mainNavigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onAddLodded();
            adShow();
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_help) {
            onAddLodded();
            adShow();
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        } else if (id == R.id.action_cal) {
            onAddLodded();
            adShow();
            startActivity(new Intent(this, ExCalculatorActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NAVIGATION_POSITION, idSelectedNavigationItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mainDrawerLayout.closeDrawers();
        switchFragment(menuItem.getItemId());
        return false;
    }

    @Override
    public void setTabs(List<String> tabList, final TabLayout.OnTabSelectedListener onTabSelectedListener) {
        mainTabLayout.removeAllTabs();
        mainTabLayout.setVisibility(View.VISIBLE);
        mainTabLayout.setOnTabSelectedListener(onTabSelectedListener);
        for (String tab : tabList) {
            mainTabLayout.addTab(mainTabLayout.newTab().setText(tab).setTag(tab));
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setMode(@NavigationMode int mode) {
        mFloatingActionButton.setVisibility(View.GONE);
        llExpensesSummary.setVisibility(View.GONE);
        mCurrentMode = mode;
        switch (mode) {
            case NAVIGATION_MODE_STANDARD:
                setNavigationModeStandard();
                break;
            case NAVIGATION_MODE_TABS:
                setNavigationModeTabs();
                break;
        }
    }

    @Override
    public void setExpensesSummary(@IDateMode int dateMode) {
        float total = Expense.getTotalExpensesByDateMode(dateMode);
        tvTotal.setText(Util.getFormattedCurrency(total));
        String date;
        switch (dateMode) {
            case IDateMode.MODE_TODAY:
                date = Util.formatDateToString(DateUtils.getToday(), Util.getCurrentDateFormat());
                break;
            case IDateMode.MODE_WEEK:
                date = Util.formatDateToString(DateUtils.getFirstDateOfCurrentWeek(), Util.getCurrentDateFormat()).concat(" - ").concat(Util.formatDateToString(DateUtils.getRealLastDateOfCurrentWeek(), Util.getCurrentDateFormat()));
                break;
            case IDateMode.MODE_MONTH:
                date = Util.formatDateToString(DateUtils.getFirstDateOfCurrentMonth(), Util.getCurrentDateFormat()).concat(" - ").concat(Util.formatDateToString(DateUtils.getRealLastDateOfCurrentMonth(), Util.getCurrentDateFormat()));
                break;
            default:
                date = "";
                break;
        }
        tvDate.setText(date);
    }

    @Override
    public void setFAB(@DrawableRes int drawableId, View.OnClickListener onClickListener) {
        mFloatingActionButton.setImageDrawable(getResources().getDrawable(drawableId));
        mFloatingActionButton.setOnClickListener(onClickListener);
        mFloatingActionButton.show();
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void setPager(ViewPager vp, final TabLayout.ViewPagerOnTabSelectedListener viewPagerOnTabSelectedListener) {
        mainTabLayout.setupWithViewPager(vp);
        mainTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                @IDateMode int dateMode;
                switch (tab.getPosition()) {
                    case 0:
                        dateMode = IDateMode.MODE_TODAY;
                        break;
                    case 1:
                        dateMode = IDateMode.MODE_WEEK;
                        break;
                    case 2:
                        dateMode = IDateMode.MODE_MONTH;
                        break;
                    default:
                        dateMode = IDateMode.MODE_TODAY;
                }
                setExpensesSummary(dateMode);
                viewPagerOnTabSelectedListener.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPagerOnTabSelectedListener.onTabUnselected(tab);
            }
        });
        setExpensesSummary(IDateMode.MODE_TODAY);
    }

    public ActionMode setActionMode(final ActionMode.Callback actionModeCallback) {
        return mToolbar.startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return actionModeCallback.onCreateActionMode(mode, menu);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return actionModeCallback.onPrepareActionMode(mode, menu);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return actionModeCallback.onActionItemClicked(mode, item);
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                actionModeCallback.onDestroyActionMode(mode);
            }
        });
    }

    private void setNavigationModeTabs() {
        mainTabLayout.setVisibility(View.VISIBLE);
        llExpensesSummary.setVisibility(View.VISIBLE);
    }

    private void setNavigationModeStandard() {
        CoordinatorLayout coordinator = (CoordinatorLayout) findViewById(R.id.main_coordinator);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null && appbar != null) {
            int[] consumed = new int[2];
            behavior.onNestedPreScroll(coordinator, appbar, null, 0, -1000, consumed);
        }
        mainTabLayout.setVisibility(View.GONE);
    }

    private void switchFragment(int menuItemId) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        switch (menuItemId) {
            case R.id.nav_expenses:
                if (!(currentFragment instanceof ExpensesContainerFragment))
                    replaceFragment(ExpensesContainerFragment.newInstance(), false);
                break;
            case R.id.nav_income:
                startActivity(new Intent(MainActivity.this, IncomeActivity.class));
                break;
            case R.id.nav_categories:
                if (!(currentFragment instanceof CategoriesFragment))
                    replaceFragment(CategoriesFragment.newInstance(), false);
                break;
            case R.id.nav_statistics:
                if (!(currentFragment instanceof StatisticsFragment))
                    replaceFragment(StatisticsFragment.newInstance(), false);
                break;

            case R.id.nav_history:
                if (!(currentFragment instanceof HistoryFragment))
                    replaceFragment(HistoryFragment.newInstance(), false);
                break;
            case R.id.nav_cal:
                startActivity(new Intent(MainActivity.this, ExCalculatorActivity.class));
                break;
            case R.id.nav_share:
                AppUtility.shareApp(this);
                break;
            case R.id.nav_rate:
                AppUtility.rateThisApp(this);
                break;
            case R.id.nav_About:
                showAboutsDialog();
                break;

            case R.id.logout:
                logout();
                break;
        }
    }


    public void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, signinActivity.class);
        startActivity(intent);
        finish();
    }

    private void showBannerInterstitialAd() {
        /*InterstitialAd*/
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(this.getString(R.string.interstitial_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//        });

    }

    private void adShow() {
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void onAddLodded() {
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        } else {
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        }
    }

    private void showAboutsDialog() {
        final Dialog d = new Dialog(this, android.R.style.Theme_Translucent);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.popup_about);
        d.setCancelable(false);

        final RelativeLayout finish = d.findViewById(R.id.finish);
        final ImageView imvBack = d.findViewById(R.id.imvBack);
        final TextView appVName = d.findViewById(R.id.appVersionName);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        String appVersionName = getAppVersionName(this);
        if (!appVersionName.isEmpty() || appVersionName.equals("")) {
            appVName.setText(getString(R.string.version) + " " + appVersionName);
        } else {
            appVName.setVisibility(View.GONE);
        }

        d.show();
    }

    public String getAppVersionName(Context context) {
        String versionName = EMPTY_STRING;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return versionName;
    }


    @Override
    public void onBackPressed() {
        new OoOAlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setTitleColor(R.color.primary)
                .setAnimation(Animation.ZOOM)
                .setMessage("Are You Sure,want exit From App?")
                .setMessageColor(R.color.primary)
                .setPositiveButtonColor(R.color.primary)
                .setPositiveButtonTextColor(R.color.white)
                .setNegativeButtonTextColor(R.color.white)
                .setNegativeButtonColor(R.color.primary)
                .setPositiveButton("Yes", new OnClickListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .build();

    }

}
