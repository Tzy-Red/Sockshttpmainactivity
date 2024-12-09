package com.slipkprojects.sockshttp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.slipkprojects.sockshttp.activities.BaseActivity;
import com.slipkprojects.sockshttp.activities.ConfigGeralActivity;
import com.slipkprojects.sockshttp.adapter.LogsAdapter;
import com.slipkprojects.sockshttp.util.AESCrypt;
import com.slipkprojects.sockshttp.util.ConfigUpdate;
import com.slipkprojects.sockshttp.util.ConfigUtil;
import com.slipkprojects.sockshttp.util.GoogleFeedbackUtils;
import com.slipkprojects.sockshttp.util.NethPogi;
import com.slipkprojects.sockshttp.util.PayloadAdapter;
import com.slipkprojects.sockshttp.util.PayloadModel;
import com.slipkprojects.sockshttp.util.ServerAdapter;
import com.slipkprojects.sockshttp.util.ServerModel;
import com.slipkprojects.sockshttp.util.Utils;
import com.slipkprojects.sockshttp.util.VPNUtils;
import com.slipkprojects.ultrasshservice.LaunchVpn;
import com.slipkprojects.ultrasshservice.SocksHttpService;
import com.slipkprojects.ultrasshservice.config.ConfigParser;
import com.slipkprojects.ultrasshservice.config.Settings;
import com.slipkprojects.ultrasshservice.logger.ConnectionStatus;
import com.slipkprojects.ultrasshservice.logger.SkStatus;
import com.slipkprojects.ultrasshservice.tunnel.TunnelManagerHelper;
import com.slipkprojects.ultrasshservice.tunnel.TunnelUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.net237.vpn.R;


public class SocksHttpMainActivity extends BaseActivity implements SkStatus.StateListener {

    private ArrayList<ServerModel> serverList;
    private ArrayList<PayloadModel> payList;

    private Button mButtonSet;

    private SharedPreferences prefs;

    private MaterialTextView bytesIn;

    private MaterialTextView bytesOut;

	private Window window;
	public class NethPH extends PagerAdapter
    {

        @Override
        public int getCount()
        {
            // TODO: Implement this method
            return 2;
        }

        @Override
        public boolean isViewFromObject(View p1, Object p2)
        {
            // TODO: Implement this method
            return p1 == p2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            int[] ids = new int[]{R.id.tab1, R.id.tab2};
            int id = 0;
            id = ids[position];
            // TODO: Implement this method
            return findViewById(id);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            // TODO: Implement this method
            return titles.get(position);
        }

        private List<String> titles;
        public NethPH(List<String> str)
        {
            titles = str;
        }
	}
    private static final String UPDATE_VIEWS = "MainUpdate";
	public static final String OPEN_LOGS = "com.slipkprojects.sockshttp:openLogs";
	private Settings mConfig;
	private Toolbar toolbar_main;
	private Handler mHandler;
	private MaterialTextView status;
	private FloatingActionButton deleteLogs;
	private RecyclerView logList;
    private LogsAdapter mLogAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ConfigUtil config;
    private Button buttonnethie;
    
    private AppCompatSpinner serverSpinner;
    private AppCompatSpinner payloadSpinner;
    private ViewPager vp;
    private TabLayout tabs;
	private InterstitialAd interstitialAd;
	private AdView adsBannerView;
	private RewardedAd rewardedAd;
	private MaterialAlertDialogBuilder builer;
	private AlertDialog alert;
	private MaterialTextView ok;
	private MaterialTextView cancel;
	private boolean mClaim;
	private boolean isLoading;
	
    public void loadInterstitialAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
			this,
			getString(R.string.jmgc_intersid),
			adRequest,
			new InterstitialAdLoadCallback() {
				@Override
				public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
					// The mInterstitialAd reference will be null until
					// an ad is loaded.
					SocksHttpMainActivity.this.interstitialAd = interstitialAd;
					// Log.i(TAG, "onAdLoaded");
					// Toast.makeText(MyActivity.this, "onAdLoaded()",
					// Toast.LENGTH_SHORT).show();
					interstitialAd.setFullScreenContentCallback(
						new FullScreenContentCallback() {
							@Override
							public void onAdDismissedFullScreenContent() {
								// Called when fullscreen content is dismissed.
								// Make sure to set your reference to null so you don't
								// show it a second time.
								SocksHttpMainActivity.this.interstitialAd = null;
								// Log.d("TAG", "The ad was dismissed.");
							}

							@Override
							public void onAdFailedToShowFullScreenContent(AdError adError) {
								// Called when fullscreen content failed to show.
								// Make sure to set your reference to null so you don't
								// show it a second time.
								SocksHttpMainActivity.this.interstitialAd = null;
								// Log.d("TAG", "The ad failed to show.");
							}

							@Override
							public void onAdShowedFullScreenContent() {
								// Called when fullscreen content is shown.
								// Log.d("TAG", "The ad was shown.");
							}
						});
				}

				@Override
				public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
					// Handle the error
					// Log.i(TAG, loadAdError.getMessage());
					interstitialAd = null;

					// String error = String.format("domain: %s, code: %d, message: %s",
					// loadAdError.getDomain(), loadAdError.getCode(),
					// loadAdError.getMessage());
					// Toast.makeText(MyActivity.this, "onAdFailedToLoad() with error: " +
					// error, Toast.LENGTH_SHORT).show();
				}
			});
    }

    private void adsPopUp() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            startGame();
        }
    }

    private void startGame() {
        if (interstitialAd == null) {
            loadInterstitialAds();
        }
    }
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long mTimeLeftBtn;
    private long saved_ads_time;
    private boolean mConnected;
    private CountDownTimer mBtnCountDown;
	private boolean mTimerEnabled;
    private MaterialTextView mMaterialTextViewCountDown;
    
    
	@Override
    protected void onCreate(@Nullable Bundle $)
    {
        super.onCreate($);
		mHandler = new Handler();
		mConfig = new Settings(this);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));    
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
	    prefs = mConfig.getPrefsPrivate();
		boolean showFirstTime = prefs.getBoolean("connect_first_time", true);
		int lastVersion = prefs.getInt("last_version", 0);
		loadInterstitialAds();
		if (showFirstTime)
        {
            SharedPreferences.Editor pEdit = prefs.edit();
            pEdit.putBoolean("connect_first_time", false);
            pEdit.apply();
			Settings.setDefaultConfig(this);
        }

		try {
			int idAtual = ConfigParser.getBuildId(this);
			if (lastVersion < idAtual) {
				SharedPreferences.Editor pEdit = prefs.edit();
				pEdit.putInt("last_version", idAtual);
				pEdit.apply();
				if (!showFirstTime) {
					if (lastVersion <= 12) {
						Settings.setDefaultConfig(this);
						Settings.clearSettings(this);
					}
				}
			}
		} catch(IOException e) {}
		if (getIntent().getStringExtra("ReMod").equals(String.valueOf(View.GONE)) || getIntent().getStringExtra("ReMod").equals("")){
			Utils.exitAll(SocksHttpMainActivity.this);
		}
		doLayout();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_VIEWS);
		filter.addAction(OPEN_LOGS);
		LocalBroadcastManager.getInstance(this)
		.registerReceiver(mActivityReceiver, filter);
		doUpdateLayout();
 //       doTabs();
	}
	private void doLayout() {
		setContentView(R.layout.activity_main_drawer);
		toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar_main);
        config = new ConfigUtil(this);
		mClaim = false;
	    window = SocksHttpMainActivity.this.getWindow();
        MaterialTextView configv = (MaterialTextView) findViewById(R.id.configv);
		configv.setText("Config Version: " + config.getVersion());
		MaterialTextView updatet = (MaterialTextView) findViewById(R.id.update);
		updatet.setText(!mConfig.getPrefsPrivate().getString("UpdateTime", "").equals("") ? mConfig.getPrefsPrivate().getString("UpdateTime","") : "----/--/--");
		getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar_main, R.string.open, R.string.cancel);
        toggle.syncState();
		loadInterstitialAds();
		adsBannerView = (AdView) findViewById(R.id.adBannerMainView);
        if (TunnelUtils.isNetworkOnline(SocksHttpMainActivity.this)) {
            adsBannerView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (adsBannerView != null) {
                            adsBannerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            adsBannerView.loadAd(new AdRequest.Builder()
                                 .build());
		}
        mMaterialTextViewCountDown = (MaterialTextView)findViewById(R.id.gags);
        mButtonSet = (Button) findViewById(R.id.btnAddTime);
        mButtonSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SkStatus.isTunnelActive()){
                        loadz();
                    }else{
                        VPNUtils.NethToast(SocksHttpMainActivity.this, R.drawable.wrong, "Connect VPN First!");
                        }
                }
			});
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mLogAdapter = new LogsAdapter(layoutManager,this);
		deleteLogs = (FloatingActionButton)findViewById(R.id.clearLog);
		logList = (RecyclerView) findViewById(R.id.recyclerDrawerView);
		logList.setAdapter(mLogAdapter);
		logList.setLayoutManager(layoutManager);
		mLogAdapter.scrollToLastPosition();
		deleteLogs.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					mLogAdapter.clearLog();
					deleteLogs.startAnimation(AnimationUtils.loadAnimation(SocksHttpMainActivity.this, R.anim.grow));
					VPNUtils.NethToast(SocksHttpMainActivity.this, R.drawable.check, "Logs is cleared");
				//	SkStatus.logInfo("<font color='green'>Logs Deleted By your Finger!</font>");
					
				}


			});
        updateConfig(true);
		status = (MaterialTextView) findViewById(R.id.monsour_stats);
        bytesIn = (MaterialTextView) findViewById(R.id.download);
        bytesOut = (MaterialTextView) findViewById(R.id.upload);
        buttonnethie = (Button) findViewById(R.id.nethiestart);
        buttonnethie.setOnClickListener(new Button.OnClickListener(){
        @Override
        public void onClick(View ugh){
            startOrStopTunnel(SocksHttpMainActivity.this);
        }
        });
        new Timer().schedule(new TimerTask(){@Override public void run() {new Handler(Looper.getMainLooper()).post(new Runnable() {@Override public void run() {
        getData();}});}}, 0,1000);
        serverList = new ArrayList<ServerModel>();
        serverSpinner = (AppCompatSpinner) findViewById(R.id.serverSpinner);
		payloadSpinner = (AppCompatSpinner) findViewById(R.id.payloadSpinner);
        try {
            JSONArray array = config.getServersArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                SharedPreferences prefs = mConfig.getPrefsPrivate();
                SharedPreferences.Editor edit = prefs.edit();
                String flag = jsonObject.getString("FLAG");
                String sname = jsonObject.getString("Name");
				String sHost = jsonObject.getString("ServerIP");
				String sPort = jsonObject.getString("ServerPort");
                String sinfo = jsonObject.getString("sInfo");
                ServerModel model = new ServerModel();
                model.setServerName(sname);
                model.setServerInfo(sinfo);
				model.setServerHost(sHost);
				model.setServerPort(sPort);
                model.setServerflag(flag);
                serverList.add(model);
                edit.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        payList = new ArrayList<PayloadModel>();
        try {
            JSONArray array = config.getNetworksArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                SharedPreferences prefs = mConfig.getPrefsPrivate();
                SharedPreferences.Editor edit = prefs.edit();
                String sname = jsonObject.getString("Name");
                String sinfo = jsonObject.getString("pInfo");
                PayloadModel model = new PayloadModel();
                model.setServerName(sname);
                model.setServerInfo(sinfo);
                payList.add(model);
                edit.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerAdapter Serveradapter = new ServerAdapter(this, this, serverList, mConfig.getPrefsPrivate().getInt("Selected1",0));
        PayloadAdapter Payadapter = new PayloadAdapter(this, payList, mConfig.getPrefsPrivate().getInt("Selected2",0));
        if (config.getJSONConfig() != null){
		serverSpinner.setAdapter(Serveradapter);
        payloadSpinner.setAdapter(Payadapter);
		} else {
			VPNUtils.NethToast(this, R.drawable.wrong, "No Server & Payload Detected.");
		}
        serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                   mConfig.getPrefsPrivate().edit().putInt("Selected1", p3).apply();
				   adsPopUp();
                }
                @Override
                public void onNothingSelected(AdapterView<?> p1) {
                    Toast.makeText(getApplicationContext(), "Nothing selected!",0).show();
                }
        });
        payloadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                    mConfig.getPrefsPrivate().edit().putInt("Selected2", p3).apply();
					adsPopUp();
                }
                @Override
                public void onNothingSelected(AdapterView<?> p1) {
                    Toast.makeText(getApplicationContext(), "Nothing selected!",0).show();
                }
            });
        serverSpinner.setSelection(mConfig.getPrefsPrivate().getInt("Selected1",0));
        payloadSpinner.setSelection(mConfig.getPrefsPrivate().getInt("Selected2",0));
		try {
			JSONArray jsonArray = config.getServersArray();
			for (int i = 0; i < jsonArray.length(); i++) {
				MaterialTextView serverCount = (MaterialTextView) findViewById(R.id.serverCount);
				serverCount.setText("Server(s): " + jsonArray.length());
			}
		} catch (Exception err){
			err.printStackTrace();
			VPNUtils.NethToast(SocksHttpMainActivity.this, R.drawable.wrong, err.getMessage());
		}
		try {
			JSONArray jsonArray2 = config.getNetworksArray();
			for (int i = 0; i < jsonArray2.length(); i++) {
				MaterialTextView payCount = (MaterialTextView) findViewById(R.id.payCount);
				payCount.setText("Payload(s): " + jsonArray2.length());
			}
		} catch (Exception err){
			err.printStackTrace();
			VPNUtils.NethToast(SocksHttpMainActivity.this, R.drawable.wrong, err.getMessage());
		}
	    vp = (ViewPager)findViewById(R.id.viewpager);
        tabs = (TabLayout)findViewById(R.id.tablayout);
        vp.setAdapter(new NethPH(Arrays.asList(new String[]{"HOME", "STATUS"})));
        vp.setOffscreenPageLimit(2);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setupWithViewPager(vp);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

				@Override
				public void onPageScrolled(int p1, float p2, int p3) {
				}

				@Override
				public void onPageSelected(int p1) {
					switch (p1){
						case 0:
							deleteLogs.setVisibility(View.GONE);
							break;
					    case 1:
							new Handler().postDelayed(new Runnable(){
								@Override
								public void run(){
							deleteLogs.setVisibility(View.VISIBLE);
							deleteLogs.startAnimation(AnimationUtils.loadAnimation(SocksHttpMainActivity.this, R.anim.grow));
							}
							}, 100);
							break;
					}
				}

				@Override
				public void onPageScrollStateChanged(int p1) {
				}
				
			
		});
        final NavigationView drawerNavigationView = (NavigationView) findViewById(R.id.drawerNavigationView);
        drawerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

                @Override
                public boolean onNavigationItemSelected(MenuItem p1) {
                    switch (p1.getItemId()) {
                        case R.id.updConf:
                        updateConfig(false);
                        break;
                        case R.id.Huntzf:
                        ipHuntz();
                        break;
                        case R.id.Bell:
                            View inflate = LayoutInflater.from(SocksHttpMainActivity.this).inflate(R.layout.notification, null);
                            MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(SocksHttpMainActivity.this); 
                            builer.setView(inflate); 
                            MaterialTextView title = inflate.findViewById(R.id.notiftext1);
                            MaterialTextView ms = inflate.findViewById(R.id.confimsg);
                            MaterialTextView ok = inflate.findViewById(R.id.appButton1);
                            MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
                            title.setText("Release Notes!");
                            ms.setText(config.geNote());
                            ok.setText("Ok,Close");
                            cancel.setText(".");
                            cancel.setVisibility(View.GONE);
                            final AlertDialog alert = builer.create(); 
                            alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
                            alert.getWindow().setGravity(Gravity.CENTER); 
                            ok.setOnClickListener(new View.OnClickListener() { 
                                    @Override
                                    public void onClick(View p1){
                                        alert.dismiss();
                                    }
                                });

                            cancel.setOnClickListener(new View.OnClickListener(){

                                    @Override
                                    public void onClick(View p1) {

                                        alert.dismiss();
                                    }




                                });
                            alert.show();
                        break;
						case R.id.ann:
							View inflate1 = LayoutInflater.from(SocksHttpMainActivity.this).inflate(R.layout.notification, null);
                            MaterialAlertDialogBuilder builer1 = new MaterialAlertDialogBuilder(SocksHttpMainActivity.this); 
                            builer1.setView(inflate1); 
                            MaterialTextView title1 = inflate1.findViewById(R.id.notiftext1);
                            MaterialTextView ms1 = inflate1.findViewById(R.id.confimsg);
                            MaterialTextView ok1 = inflate1.findViewById(R.id.appButton1);
                            MaterialTextView cancel1 = inflate1.findViewById(R.id.appButton2);
                            title1.setText("Announcements!");
                            ms1.setText(config.getAnn());
                            ok1.setText("Ok,Close");
                            cancel1.setText(".");
                            cancel1.setVisibility(View.GONE);
                            final AlertDialog alert1 = builer1.create(); 
                            alert1.setCanceledOnTouchOutside(false);
							alert1.getWindow().getAttributes().windowAnimations = R.style.Neth01;
                            alert1.getWindow().setGravity(Gravity.CENTER); 
                            ok1.setOnClickListener(new View.OnClickListener() { 
                                    @Override
                                    public void onClick(View p1){
                                        alert1.dismiss();
                                    }
                                });

                            cancel1.setOnClickListener(new View.OnClickListener(){

                                    @Override
                                    public void onClick(View p1) {

                                        alert1.dismiss();
                                    }




                                });
                            alert1.show();
							break;
                  /*      case R.id.claimV:
                        claimz();
                        break;*/
                        case R.id.clearz:
                        clerd();
                        break;
                        case R.id.hardware:
                            MaterialAlertDialogBuilder mBuilder = new MaterialAlertDialogBuilder(SocksHttpMainActivity.this);
                            mBuilder.setTitle("Hardware ID");
                            mBuilder.setMessage(VPNUtils.getHWID());
                            mBuilder.setCancelable(false);
                            mBuilder.setPositiveButton("COPY", new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface mDialogInterface, int mInt)
                                    {
                                        ((ClipboardManager)getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("HWID", VPNUtils.getHWID()));
                                    }
                                });
                            mBuilder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface mDialog, int mInt)
                                    {
                                        mDialog.cancel();
                                    }
                                });
                            mBuilder.show();
                            break;

                        case R.id.miPhoneConfg:
                            if (Build.VERSION.SDK_INT >= 30){
                                    try {
                                        Intent in = new Intent(Intent.ACTION_MAIN);
                                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        in.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
                                        SocksHttpMainActivity.this.startActivity(in);
                                    } catch (Exception e){
                                        Toast.makeText(SocksHttpMainActivity.this, R.string.error_no_supported, Toast.LENGTH_SHORT)
                                            .show();   }
                            } else {
                                try {
                                    Intent in = new Intent(Intent.ACTION_MAIN);
                                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    in.setClassName("com.android.settings", "com.android.settings.RadioInfo");
                                    SocksHttpMainActivity.this.startActivity(in);
                                } catch(Exception e) {
                                    Toast.makeText(SocksHttpMainActivity.this, R.string.error_no_supported, Toast.LENGTH_SHORT)
                                        .show();   }
                            }
                            break;
                        case R.id.miAvaliarPlaystore:
                            String url = "https://www.github.com/tzyred";
                            Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            SocksHttpMainActivity.this.startActivity(Intent.createChooser(intent3, SocksHttpMainActivity.this.getText(R.string.open_with)));
                            break;

                        case R.id.miSendFeedback:
                            if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                try {
                                    GoogleFeedbackUtils.bindFeedback(SocksHttpMainActivity.this);
                                } catch (Exception e) {
                                    Toast.makeText(SocksHttpMainActivity.this, "Not available on your device", Toast.LENGTH_SHORT)
                                        .show();
                                    SkStatus.logDebug("Error: " + e.getMessage());
                                }
                            }
                            else {
                                Intent email = new Intent(Intent.ACTION_SEND);  
                                email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"tzyreddy@gmail.com"});
                                email.putExtra(Intent.EXTRA_SUBJECT, "Source Code: NET237-VPN- " + SocksHttpMainActivity.this.getString(R.string.feedback));
                                //email.putExtra(Intent.EXTRA_TEXT, "");  

                                //need this to prompts email client only  
                                email.setType("message/rfc822");  

                                SocksHttpMainActivity.this.startActivity(Intent.createChooser(email, "Choose Gmail:"));
                            }
                            break;

                            /** case R.id.miAbout:
                             if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                             drawerLayout.closeDrawers();
                             }
                             Intent aboutIntent = new Intent(mActivity, AboutActivity.class);
                             mActivity.startActivity(aboutIntent);
                             break;**/
                    }
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                      drawerLayout.closeDrawers();
                    }
					adsPopUp();
                    return true;
                        }
        });
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener(){
                private boolean bolbol = true;
                @Override
                public void onDrawerSlide(View p1, float p2) {
                 if (bolbol){
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(500);
                p1.startAnimation(fadeIn);
                bolbol = false;
                }     
				}
                @Override
                public void onDrawerOpened(View p1) {
                }
                @Override
                public void onDrawerClosed(View p1) {
                 bolbol = true;
                }
                @Override
                public void onDrawerStateChanged(int p1) {
                }
            });
	}
    private String render_bandwidth(double bw) {
        String postfix;
        float div;
        Object[] objArr;
        float bwf = (float) bw;
        if (bwf >= 1.0E12f) {
            postfix = "TB";
            div = 1.0995116E12f;
        } else if (bwf >= 1.0E9f) {
            postfix = "GB";
            div = 1.0737418E9f;
        } else if (bwf >= 1000000.0f) {
            postfix = "MB";
            div = 1048576.0f;
        } else if (bwf >= 1000.0f) {
            postfix = "KB";
            div = 1024.0f;
        } else {
            objArr = new Object[1];
            objArr[0] = Float.valueOf(bwf);
            return String.format("%.0f", objArr);
        }
        objArr = new Object[2];
        objArr[0] = Float.valueOf(bwf / div);
        objArr[1] = postfix;
        return String.format("%.2f %s", objArr);
    }
    private void getData() {
        boolean isRunning = SkStatus.isTunnelActive();
        long mUpload, mDownload, saved_Send ,saved_Down/*,up, down*/;
        String saved_date, tDate;
        List<Long> allData;
        allData = NethPogi.findData();
        mDownload = allData.get(0);
        mUpload = allData.get(1);
        NethPogi.damn(mDownload, mUpload);
        //down = mDownload;
        //up = mUpload;
        SharedPreferences myData = mConfig.getPrefsPrivate();
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        tDate = sdf.format(ca.getTime());
        saved_date = myData.getString("today_date", "empty");
        SharedPreferences.Editor editor = myData.edit();
        if (saved_date.equals(tDate)) {
            saved_Send = myData.getLong("UP_DATA", 0);
            saved_Down = myData.getLong("DOWN_DATA", 0);
            editor.putLong("UP_DATA", mUpload + saved_Send);
            editor.putLong("DOWN_DATA", mDownload + saved_Down);
            editor.apply();
        } else {
            editor.clear();
            editor.putString("today_date", tDate);
            editor.apply();
        }
        if(isRunning){
            bytesOut.setText(render_bandwidth(myData.getLong("UP_DATA", 0)));
            bytesIn.setText(render_bandwidth(myData.getLong("DOWN_DATA", 0)));
        }else{
            myData.edit().putLong("UP_DATA", 0).apply();
            myData.edit().putLong("DOWN_DATA", 0).apply();
        }
    }
	
	private void doUpdateLayout() {
        if (config.time()){
        } else {
            mButtonSet.setText("Disabled");
            mButtonSet.setEnabled(false);
        }
        setStarterButton(buttonnethie, this);
	}
	public void doRestart() {
        finish();
        startActivity(new Intent(this,LauncherActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
	}
	/**
	 * Tunnel SSH
	 */
	public void startOrStopTunnel(Activity activity) {
		if (SkStatus.isTunnelActive()) {
			TunnelManagerHelper.stopSocksHttp(activity);
		}
		else {
			// oculta teclado se vísivel, tá com bug, tela verde
			//Utils.hideKeyboard(activity);
            if (mConfig.getPrivString(Settings.SERVIDOR_KEY).isEmpty() || mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY).isEmpty()) {
                SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                                           ConnectionStatus.LEVEL_NOTCONNECTED);
			}

			/*if (config.getPrefsPrivate()
				.getBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false)) {
				if (inputPwUser.getText().toString().isEmpty() || 
					inputPwPass.getText().toString().isEmpty()) {
					Toast.makeText(this, R.string.error_userpass_empty, Toast.LENGTH_SHORT)
						.show();
					return;
				}
			}*/
            _pos_(serverSpinner.getSelectedItemPosition(),payloadSpinner.getSelectedItemPosition());
			Intent intent = new Intent(activity, LaunchVpn.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			activity.startActivity(intent);
		}
	}
    String getName(String str){
      return mConfig.getPrivString(str);  
    }
    private synchronized void _pos_(int pos, int pos1) {
        try {
            SharedPreferences prefs = mConfig.getPrefsPrivate();
            SharedPreferences.Editor edit = prefs.edit();
            prefs.getBoolean(Settings.CUSTOM_PAYLOAD_KEY, true);
            edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, !true);
			String ssh_name = config.getServersArray().getJSONObject(pos).getString("Name");
            String ssh_server = config.getServersArray().getJSONObject(pos).getString("ServerIP");
            String remote_proxy = config.getServersArray().getJSONObject(pos).getString("ProxyIP");
            String proxy_port = config.getServersArray().getJSONObject(pos).getString("ProxyPort");
            String user = config.getServersArray().getJSONObject(pos).getString("ServerUser");
            String pass = config.getServersArray().getJSONObject(pos).getString("ServerPass");
            String chvKey = config.getServersArray().getJSONObject(pos).getString("SlowDnskey");
            String nvKey = config.getServersArray().getJSONObject(pos).getString("SlowDnshost");
            String cfz = config.getServersArray().getJSONObject(pos).getString("CloudfrontIP");
            String proxycum = config.getNetworksArray().getJSONObject(pos1).getString("CustomProxy");
            String proxyporn = config.getNetworksArray().getJSONObject(pos1).getString("CustomProxyPort");
            boolean CustomProxyBold = config.getNetworksArray().getJSONObject(pos1).getBoolean("isCustom");
            boolean directModeType = config.getNetworksArray().getJSONObject(pos1).getBoolean("isSSL");
            boolean sshssltype =  config.getNetworksArray().getJSONObject(pos1).getBoolean("isSslPayRp");
            boolean slowdnstype = config.getNetworksArray().getJSONObject(pos1).getBoolean("SlowDns");
            edit.putString(Settings.SERVIDOR_KEY, ssh_server);
			edit.putString("ServerName", ssh_name);
            if (CustomProxyBold == true){
                edit.putString(Settings.PROXY_IP_KEY, VPNUtils.IsangTangangNagDecrypt(proxycum).replace("[HOST]", config.getServersArray().getJSONObject(pos).getString("ServerIP")));
                edit.putString(Settings.PROXY_PORTA_KEY, proxyporn);          
            }
            else {
                edit.putString(Settings.PROXY_IP_KEY, remote_proxy);
                edit.putString(Settings.PROXY_PORTA_KEY, proxy_port);
            }
            if (directModeType) {
                String ssl_port = config.getServersArray().getJSONObject(pos).getString("SSLPort");
                edit.putString(Settings.SERVIDOR_PORTA_KEY, ssl_port);
            } else if (sshssltype) {
                String ssl_port1 = config.getServersArray().getJSONObject(pos).getString("SSLPort");
                edit.putString(Settings.SERVIDOR_PORTA_KEY, ssl_port1);
            } else if (slowdnstype) {
                edit.putString(Settings.SERVIDOR_KEY, "127.0.0.1");
                edit.putString(Settings.SERVIDOR_PORTA_KEY, "2222");
            } else {
                String ssh_port = config.getServersArray().getJSONObject(pos).getString("ServerPort");
                edit.putString(Settings.SERVIDOR_PORTA_KEY, ssh_port);
            }
            edit.apply();
            edit.putString(Settings.USUARIO_KEY, VPNUtils.IsangTangangNagDecrypt(user));
            edit.putString(Settings.SENHA_KEY, VPNUtils.IsangTangangNagDecrypt(pass));
            if (cfz.isEmpty()){
            } else {
                edit.putString("CloudfrontIP", cfz);
            }
            if (chvKey.isEmpty()){
            } else {
                edit.putString(Settings.CHAVE_KEY, chvKey);
            }
            if (nvKey.isEmpty()){
            } else {
                edit.putString(Settings.NAMESERVER_KEY, nvKey);
            }
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Server Error: " + e.getMessage(), 0).show();
            //SocksHttpApp.toast(getApplicationContext(), R.color.red, e.getMessage());
        }

        try {
            SharedPreferences prefs = mConfig.getPrefsPrivate();
            SharedPreferences.Editor edit = prefs.edit();
            prefs.getBoolean(Settings.CUSTOM_PAYLOAD_KEY, true);
            edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, !true);
            boolean directModeType = config.getNetworksArray().getJSONObject(pos1).getBoolean("isSSL");
            boolean sshssltype =  config.getNetworksArray().getJSONObject(pos1).getBoolean("isSslPayRp");
            boolean slowdnstype = config.getNetworksArray().getJSONObject(pos1).getBoolean("SlowDns");
			boolean FuckingCloudfront = config.getNetworksArray().getJSONObject(pos1).getBoolean("isCf");
            if (directModeType) {
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSL_TLS).apply();
                String sni = config.getNetworksArray().getJSONObject(pos1).getString("SNI");
                if (FuckingCloudfront){
                    edit.putString(Settings.CUSTOM_SNI, prefs.getString("CloudfrontIP", ""));
                } else {
                    edit.putString(Settings.CUSTOM_SNI, sni);
                }
                edit.apply();
            } else if (sshssltype) {
                String payload = VPNUtils.IsangTangangNagDecrypt(config.getNetworksArray().getJSONObject(pos1).getString("Payload"));
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_PAY_SSL).apply();
                String snissl = config.getNetworksArray().getJSONObject(pos1).getString("SNI");
                if (FuckingCloudfront){
                    edit.putString(Settings.CUSTOM_SNI, prefs.getString("CloudfrontIP", ""));
                } else {
                    edit.putString(Settings.CUSTOM_SNI, snissl);
                }
                if (FuckingCloudfront){
                    edit.putString(Settings.CUSTOM_PAYLOAD_KEY, VPNUtils.cloudfront_payload(prefs));
                } else {
                    edit.putString(Settings.CUSTOM_PAYLOAD_KEY, payload.replace("[HOST]", getName(Settings.SERVIDOR_KEY)));
                }
                edit.apply();
            }else if (slowdnstype){
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SLOWDNS).apply();
                String dnsKey = config.getNetworksArray().getJSONObject(pos1).getString("dnsKey");
				if (Settings.CUSTOM_PAYLOAD_KEY == null){
					edit.putString(Settings.CUSTOM_PAYLOAD_KEY, "[]").apply();
				}
				edit.putString(Settings.DNS_KEY, dnsKey);
                edit.apply();
            } else {
                String payload = VPNUtils.IsangTangangNagDecrypt(config.getNetworksArray().getJSONObject(pos1).getString("Payload"));
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_PROXY).apply();
                if (FuckingCloudfront){
                    edit.putString(Settings.CUSTOM_PAYLOAD_KEY, VPNUtils.cloudfront_payload(prefs));
                } else {
                    edit.putString(Settings.CUSTOM_PAYLOAD_KEY, payload.replace("[HOST]", getName(Settings.SERVIDOR_KEY)));
                }
                edit.apply();
            }
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Payload Error: " + e.getMessage(), 0).show();
            //SocksHttpApp.toast(getApplicationContext(), R.color.red, e.getMessage());
        }
    }
    private void updateConfig(final boolean isOnCreate) {
        new ConfigUpdate(this, new ConfigUpdate.OnUpdateListener() {
                @Override
                public void onUpdateListener(String result) {
                    try {
                        if (!result.contains("Error on getting data")) {
                            String json_data = AESCrypt.decrypt(config.PASSWORD, result);
                            if (isNewVersion(json_data)) {
                              newUpdateDialog(result);
                            } else {
                                if (!isOnCreate) {
                                   noUpdateDialog();
                                }
                            }
                        } else if(result.contains("Error on getting data") && !isOnCreate){
                           errorUpdateDialog(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start(isOnCreate);

    }

    private boolean isNewVersion(String result) {
        try {
            String current = config.getVersion();
            String update = new JSONObject(result).getString("Version");
            return config.versionCompare(update, current);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
	}
    private void newUpdateDialog(final String result) throws JSONException, GeneralSecurityException{
        String json_data = AESCrypt.decrypt(config.PASSWORD, result);
        String notes = new JSONObject(json_data).getString("ReleaseNotes");
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("New Update Available");
        ms.setText(notes);
        ok.setText("Restart");
        cancel.setText("Dismiss");
        cancel.setVisibility(View.VISIBLE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
         
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    try
                    {
                        File file = new File(getFilesDir(), "Config.json");
                        OutputStream out = new FileOutputStream(file);
                        out.write(result.getBytes());
                        out.flush();
                        out.close();
						mConfig.getPrefsPrivate().edit()
						.putString("UpdateTime", VPNUtils.getTime01(SocksHttpMainActivity.this)).apply();
                        doRestart();
                        alert.dismiss();
                        
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }




            });
        alert.show();
    }
    private void noUpdateDialog() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("No Update");
        ms.setText("Latest config version is already installed");
        ok.setText("Ok,Close");
        cancel.setText(".");
        cancel.setVisibility(View.GONE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
         
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    alert.dismiss();
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }




            });
        alert.show();
    }
   private void clerd() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("Clear Data");
		ms.setText("You sure want to clear data? This cannot be undone!");
		ok.setText("Clear");
		cancel.setText("Cancel");
		cancel.setVisibility(View.VISIBLE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    		try {
						// clearing app data
						String packageName = getApplicationContext().getPackageName();
						Runtime runtime = Runtime.getRuntime();
						runtime.exec("pm clear "+packageName);
						Toast.makeText(getApplicationContext(), "Done! Reopen the app!", 0).show();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), e.getMessage(),0).show();
					}
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }




            });
        alert.show();
    }
    
    private void errorUpdateDialog(String error) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("Error");
        ms.setText(error.replace(VPNUtils.ConfigUrl, "**********"));
        ok.setText("Ok,Close");
        cancel.setText(".");
        cancel.setVisibility(View.GONE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
         
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    alert.dismiss();
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }




            });
        alert.show();
	}
    private void ipHuntz() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification, null);
        MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(this); 
        builer.setView(inflate); 
        MaterialTextView title = inflate.findViewById(R.id.notiftext1);
        final MaterialTextView ms = inflate.findViewById(R.id.confimsg);
        final MaterialTextView ok = inflate.findViewById(R.id.appButton1);
        MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
        title.setText("IP Hunter");
        ms.setText("Check IP! - For GTM No Load");
        ok.setText("Hunt");
        cancel.setText("Close");
        cancel.setVisibility(View.VISIBLE);
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
       
        alert.getWindow().setGravity(Gravity.CENTER); 
        ok.setOnClickListener(new View.OnClickListener() { 
                @Override
                public void onClick(View p1){
                    ms.setText("Checking...");
                    ok.setText("Checking");
                    ok.setEnabled(false);
                    ipHuntez(ms,ok,"✓ Success! Magic IP","× Fail! Airplane Mode","× Something Went Wrong.");
                }
            });

        cancel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    alert.dismiss();
                }

            });
        alert.show();
	}
    private void ipHuntez(final MaterialTextView ms, final MaterialTextView ok
    , final String magic, final String fail, final String something){
           new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    try {
                        int l = 0;
                        URL whatismyip = new URL("http://noloadbalance.globe.com.ph");
                        try{        
                            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("104.16.213.74", 80));
                            HttpURLConnection connection = (HttpURLConnection) whatismyip.openConnection(proxy);
                            connection.setRequestMethod("GET");
                            connection.connect();
                            connection.getContentLength();
                            connection.setConnectTimeout(3000);
                            InputStream in = connection.getInputStream();
                            byte[] buffer = new byte[4096];
                            int countBytesRead;
                            while((countBytesRead = in.read(buffer)) != -1) {
                                l += countBytesRead;
                            }
                            in.markSupported();
                            if (l == 333){
                                ms.setText(magic);
                                SkStatus.logInfo(magic);
                                ok.setText("Check Again");
                                ok.setEnabled(true);
                                return;
                            }
                            if (connection.getResponseCode() == 200){
                                ms.setText(magic);
                                SkStatus.logInfo(magic);
                                ok.setText("Check Again");
                                ok.setEnabled(true);
                                return;
                            }
                            in.close();
                            ms.setText(fail);
                            SkStatus.logInfo(fail);
                            ok.setText("Check Again");
                            ok.setEnabled(true);
                        } catch (IOException e) {
                            ok.setText("Check Again");
                            ok.setEnabled(true);
                            ms.setText(something);
                            SkStatus.logInfo(something);
                        }
                    }catch (MalformedURLException e) {}}
            }, 1000);
    }
	public void setStarterButton(Button starterButton, Activity activity) {
		String state = SkStatus.getLastState();
		boolean isRunning = SkStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;
			
			SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();

			if (ConfigParser.isValidadeExpirou(prefsPrivate
					.getLong(Settings.CONFIG_VALIDADE_KEY, 0))) {
				resId = R.string.expired;
				starterButton.setEnabled(false);

				if (isRunning) {
					startOrStopTunnel(activity);
				}
			}
			else if (prefsPrivate.getBoolean(Settings.BLOQUEAR_ROOT_KEY, false) &&
					ConfigParser.isDeviceRooted(activity)) {
			   resId = R.string.blocked;
			   starterButton.setEnabled(false);
			   Toast.makeText(activity, R.string.error_root_detected, Toast.LENGTH_SHORT)
					.show();
			   if (isRunning) {
				   startOrStopTunnel(activity);
			   }
			}
			else if (SkStatus.SSH_INICIANDO.equals(state)) {
				resId = R.string.stop;
                vp.setCurrentItem(1);
				adsPopUp();
				starterButton.setEnabled(false);
			}
			else if (SkStatus.SSH_PARANDO.equals(state)) {
				resId = R.string.state_stopping;
				adsPopUp();
				starterButton.setEnabled(false);
			}
			else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);
			}
			starterButton.setText(resId);
		}
	}
	
	
	@Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
   }
	@Override
	public void updateState(final String state, String msg, int localizedResId, final ConnectionStatus level, Intent intent)
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				doUpdateLayout();
				if(SkStatus.isTunnelActive()){		
					if(level.equals(ConnectionStatus.LEVEL_CONNECTED)) {      
						status.setText(R.string.state_connected);
                       if (config.time()){
                           start();
                       } else {
                           mButtonSet.setText("Disabled");
                           mButtonSet.setEnabled(false);
                       }
					}
					if(level.equals(ConnectionStatus.LEVEL_NOTCONNECTED)){
						status.setText(R.string.state_disconnected);
					}
					if(level.equals(ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED)){
						status.setText( R.string.state_auth);
					}
					if(level.equals(ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET)){
						status.setText(R.string.state_connecting);
					}
					if(level.equals(ConnectionStatus.UNKNOWN_LEVEL)){
						status.setText(R.string.state_disconnected);
                        if (config.time()){
                            stop();
                        } else {
                            mButtonSet.setText("Disabled");
                            mButtonSet.setEnabled(false);
                        }
					}
					/*if(level.equals(ConnectionStatus.LEVEL_RECONNECTANDO)){
					status.setText(R.string.state_reconnecting);}*/
				}
				if(level.equals(ConnectionStatus.LEVEL_NONETWORK)){
					status.setText(R.string.state_nonetwork);
				}
				if(level.equals(ConnectionStatus.LEVEL_AUTH_FAILED)){
					status.setText(R.string.state_auth_failed);
				}

			}
		});
		
		switch (state) {
			case SkStatus.SSH_CONECTADO:
				// carrega ads banner
				if (adsBannerView != null && TunnelUtils.isNetworkOnline(SocksHttpMainActivity.this)) {
					adsBannerView.setAdListener(new AdListener() {
						@Override
						public void onAdLoaded() {
							if (adsBannerView != null && !isFinishing()) {
								adsBannerView.setVisibility(View.VISIBLE);
							}
						}
					});
					adsBannerView.postDelayed(new Runnable() {
						@Override
						public void run() {
							// carrega ads interestitial
							// ads banner
							if (adsBannerView != null && !isFinishing()) {
								adsBannerView.loadAd(new AdRequest.Builder()
									.build());
							}
						}
					}, 5000);
				}
			break;
		}
	}


	/**
	 * Recebe locais Broadcast
	 */

	private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;

            if (action.equals(UPDATE_VIEWS) && !isFinishing()) {
				doUpdateLayout();
			
				}
			}
        
    };


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.miSettings:
                Intent intentSettings = new Intent(this, ConfigGeralActivity.class);
                intentSettings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentSettings);
                break;
           case R.id.offlz:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 550);
               break;
		   case R.id.clipimport:
				View inflate = LayoutInflater.from(SocksHttpMainActivity.this).inflate(R.layout.notification, null);
				MaterialAlertDialogBuilder builer = new MaterialAlertDialogBuilder(SocksHttpMainActivity.this); 
				builer.setView(inflate); 
				MaterialTextView title = inflate.findViewById(R.id.notiftext1);
				MaterialTextView ms = inflate.findViewById(R.id.confimsg);
				MaterialTextView ok = inflate.findViewById(R.id.appButton1);
				MaterialTextView cancel = inflate.findViewById(R.id.appButton2);
				title.setText("Import From ClipBoard");
				ms.setText("Do not forget to copy the config then click OK to confirm. Else if you copied wrong config, it will fail to open the app.");
				ok.setText("OK");
				cancel.setText("Cancel");
				cancel.setVisibility(View.VISIBLE);
				final AlertDialog alert = builer.create(); 
				alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
				 
				alert.getWindow().setGravity(Gravity.CENTER); 
				ok.setOnClickListener(new View.OnClickListener() { 
						@Override
						public void onClick(View p1){
							try {
								String b = ((android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getText().toString();
								File file = new File(getFilesDir(), "Config.json");
								OutputStream out = new FileOutputStream(file);
								out.write(b.getBytes());
								out.flush();
								out.close();
								mConfig.getPrefsPrivate().edit()
									.putString("UpdateTime", VPNUtils.getTime01(SocksHttpMainActivity.this)).apply();
								doRestart();
							} catch (IOException e) {
								e.printStackTrace();
								VPNUtils.NethToast(SocksHttpMainActivity.this, R.drawable.wrong, e.getMessage());
							}
							alert.dismiss();
						}
					});

				cancel.setOnClickListener(new View.OnClickListener(){

						@Override
						public void onClick(View p1) {
							alert.dismiss();
						}
					});
				alert.show();
				break;
        }
        adsPopUp();
        return false;
    }
	@Override
	public void onBackPressed() {
		showExitDialog();
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 550)
        {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    String intentData = importer(uri);
                    //String cipter = AESCrypt.decrypt(ConfigUtil.PASSWORD, intentData);
                    File file = new File(getFilesDir(), "Config.json");
                    OutputStream out = new FileOutputStream(file);
                    out.write(intentData.getBytes());
                    out.flush();
                    out.close();
					mConfig.getPrefsPrivate().edit()
						.putString("UpdateTime", VPNUtils.getTime01(SocksHttpMainActivity.this)).apply();
                    doRestart();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String importer(Uri uri)
    {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return builder.toString();
	} 

	public void loadz(){
		loadRewardedAd();
		View inflate = LayoutInflater.from(SocksHttpMainActivity.this).inflate(R.layout.notification, null);
	    builer = new MaterialAlertDialogBuilder(SocksHttpMainActivity.this); 
		builer.setView(inflate); 
		MaterialTextView title = inflate.findViewById(R.id.notiftext1);
		MaterialTextView ms = inflate.findViewById(R.id.confimsg);
		ok = (MaterialTextView) inflate.findViewById(R.id.appButton1);
		cancel = (MaterialTextView) inflate.findViewById(R.id.appButton2);
		title.setText("Requesting Ads");
		ms.setText("Please wait while loading ads...");
		ok.setText("Claim Reward");
		cancel.setText("Cancel");
		cancel.setVisibility(View.VISIBLE);
		if (!mClaim){
			ok.setVisibility(View.GONE);
		}
	    alert = builer.create(); 
		alert.setCanceledOnTouchOutside(false);
		alert.getWindow().getAttributes().windowAnimations = R.style.Neth01;
		alert.getWindow().setGravity(Gravity.CENTER); 
		ok.setOnClickListener(new View.OnClickListener() { 
				@Override
				public void onClick(View p1){
					showRewardedVideo();
                    alert.dismiss();
					mClaim = false;
				}
			});
		alert.show();
		cancel.setOnClickListener(new View.OnClickListener() { 
				@Override
				public void onClick(View p1){
					alert.dismiss();
				}
			});
		alert.show();
	}

    // Rewarded
    private void showRewardedVideo() {
        if (rewardedAd == null) {
            return;
        }

        rewardedAd.setFullScreenContentCallback(
			new FullScreenContentCallback() {
				@Override
				public void onAdShowedFullScreenContent() {
					// Called when ad is shown.
				}

				@Override
				public void onAdFailedToShowFullScreenContent(AdError adError) {
					// Called when ad fails to show.
					// Don't forget to set the ad reference to null so you
					// don't show the ad a second time.
					rewardedAd = null;
				}

				@Override
				public void onAdDismissedFullScreenContent() {
					// Called when ad is dismissed.
					// Don't forget to set the ad reference to null so you
					// don't show the ad a second time.
					rewardedAd = null;
					//btnTimer(false);
				}
			});
        rewardedAd.show(
			SocksHttpMainActivity.this,
			new OnUserEarnedRewardListener() {
				@Override
				public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
					addTime();
					VPNUtils.NethToast(SocksHttpMainActivity.this, R.drawable.check, "2 hours added to your time!");
					btnTimer();
				}
			});
    }

    private void loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
				this,
				getString(R.string.jmgc_rewardedid),
				adRequest,
				new RewardedAdLoadCallback() {
					@Override
					public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
						// Handle the error.
						Log.d(SocksHttpMainActivity.class.getSimpleName(), loadAdError.getMessage());
						rewardedAd = null;
						SocksHttpMainActivity.this.isLoading = false;
					}

					@Override
					public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
						SocksHttpMainActivity.this.rewardedAd = rewardedAd;
						SocksHttpMainActivity.this.isLoading = false;
						//	SocksHttpApp.toast(SocksHttpMainActivity.this, R.color.green, "Rewarded Ads Loaded");
						ok.setVisibility(View.VISIBLE);
						mClaim = true;
					}
				});
        }
    }
    // End Of Rewarded
	
	@Override
    public void onResume() {
        super.onResume();
		SkStatus.addStateListener(this);
		if (adsBannerView != null) {
			adsBannerView.resume();
		}
        if (!mTimerEnabled){
            resumeTime(); // resume time
		}
    }

	@Override
	protected void onPause()
	{
		super.onPause();
		SkStatus.removeStateListener(this);
		//rewardedAd.pause(this);
		if (adsBannerView != null) {
			adsBannerView.pause();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//mDrawer.onDestroy();
		LocalBroadcastManager.getInstance(this)
			.unregisterReceiver(mActivityReceiver);	
		if (adsBannerView != null) {
			adsBannerView.destroy();
		}
	}       
	public static void updateMainViews(Context context) {
		Intent updateView = new Intent(UPDATE_VIEWS);
		LocalBroadcastManager.getInstance(context)
			.sendBroadcast(updateView);
	}
	private void setTime(long milliseconds) {
        saved_ads_time = mTimeLeftInMillis + milliseconds;
        mTimeLeftInMillis = saved_ads_time;
        updateCountDownText();

    }
    private void saveTime(){
        SharedPreferences.Editor time_edit = prefs.edit();
        time_edit.putLong("SAVED_TIME", mTimeLeftInMillis);
        time_edit.commit();
    }

    private void resumeTime(){
        long saved_time = prefs.getLong("SAVED_TIME", 0);
        setTime(saved_time);
        // Use this code to continue time if app close accidentally while connected
        /**
         String state = SkStatus.getLastState();

         if (SkStatus.SSH_CONECTADO.equals(state)) {

         if (!mTimerRunning){
         startTimer();
         mConnected = true;
         }
         }**/
        mTimerEnabled = true;
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                saveTime();
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                pauseTimer();
                saved_ads_time = 0;
                // Code for auto stop vpn (sockshtttp)         
                Intent stopVPN = new Intent(SocksHttpService.TUNNEL_SSH_STOP_SERVICE);
                LocalBroadcastManager.getInstance(SocksHttpMainActivity.this)
                    .sendBroadcast(stopVPN);
                Toast.makeText(SocksHttpMainActivity.this, "Time expired! Click Add + Time to renew access!", Toast.LENGTH_LONG).show();

            }
        }.start();
        mTimerRunning = true;
    }

    private void btnTimer() {

        mBtnCountDown = new CountDownTimer(16000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftBtn = millisUntilFinished;
                mButtonSet.setEnabled(false);
                updateBtnText();
            }
            @Override
            public void onFinish() {
                mButtonSet.setEnabled(true);
                mButtonSet.setText("ADD + TIME");
            }

        }.start();

    }

    private void updateBtnText() {
        int seconds = (int) (mTimeLeftBtn / 1000) % 60;
        String timeLeftFormatted;
        if (seconds > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                                              "%02d", seconds);

            mButtonSet.setText(timeLeftFormatted + " SECS");

        }
    }
    private void updateCountDownText(){
        long hours = TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis);
        long hoursMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis - hoursMillis);
        long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis - hoursMillis - minutesMillis);
        String resultString = hours + ":" + minutes + ":" + seconds;
        mMaterialTextViewCountDown.setText(resultString);
    }
    private void start(){

        if (saved_ads_time == 0){
        setTime(2*3600*1000);
        }
        if (!mTimerRunning){
            startTimer();
        }
        mConnected = true;
    }


    private void stop(){
        if (mTimerRunning){
            pauseTimer();
        }
        mConnected = false;
    }

    private void addTime(){
        long time = 2*3600*1000;
        setTime(time);
        if (mTimerRunning){
            pauseTimer();
        }
        startTimer();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;

	}
	public void showExitDialog() {
		AlertDialog dialog = new MaterialAlertDialogBuilder(this).
			create();
		dialog.setTitle(getString(R.string.attention));
		dialog.setMessage(getString(R.string.alert_exit));

		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.
				string.exit),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Utils.exitAll(SocksHttpMainActivity.this);
				}
			}
		);

		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.
				string.minimize),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// minimiza app
					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(startMain);
				}
			}
		);

		dialog.show();
	}
	
}