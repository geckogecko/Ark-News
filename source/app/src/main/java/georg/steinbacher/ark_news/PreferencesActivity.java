package georg.steinbacher.ark_news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Array;

/**
 * Created by georg on 29.12.17.
 */

public class PreferencesActivity extends AppCompatActivity {
        private static final String TAG = "PreferencesActivity";

        public static final String PEER_ADDRESS_KEY = "settings_peer_address";
        public static final String NETWORK_KEY = "settings_network";
        public static final String WALLET_KEY = "settings_wallet";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_preferences);

            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsPreferenceFragment()).commit();
        }

        public static class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
        {
            @Override
            public void onCreate(final Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);

                EditTextPreference costomPeer = (EditTextPreference) findPreference(PEER_ADDRESS_KEY);
                updatePeerSummary();
                costomPeer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        EditTextPreference costomPeer = (EditTextPreference) findPreference(PEER_ADDRESS_KEY);
                        costomPeer.setSummary(newValue.toString());
                        return true;
                    }
                });

                EditTextPreference wallet = (EditTextPreference) findPreference(WALLET_KEY);
                updateWalletSummary();
                wallet.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        EditTextPreference wallet = (EditTextPreference) findPreference(WALLET_KEY);
                        wallet.setSummary(newValue.toString());
                        return true;
                    }
                });

                ListPreference networkPref = (ListPreference) findPreference(NETWORK_KEY);
                String currentNetwork = getPreferenceManager().getSharedPreferences().getString(NETWORK_KEY, "MainNet");
                networkPref.setSummary(currentNetwork);
                networkPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        ListPreference networkPref = (ListPreference) findPreference(NETWORK_KEY);
                        networkPref.setSummary(newValue.toString());

                        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
                        SharedPreferences.Editor editor = prefs.edit();

                        if(newValue.toString().equals("TestNet")) {
                            editor.putString(PEER_ADDRESS_KEY, MainActivity.DEFAULT_TESTNET_PEER);
                            editor.putString(WALLET_KEY, MainActivity.DEFAULT_TESTNET_ADDRESS);
                            editor.commit();
                        } else if(newValue.toString().equals("MainNet")) {
                            editor.putString(PEER_ADDRESS_KEY, MainActivity.DEFAULT_PEER);
                            editor.putString(WALLET_KEY, MainActivity.DEFAULT_ADDRESS);
                            editor.commit();
                        }

                        updatePeerSummary();
                        updateWalletSummary();
                        return true;
                    }
                });

            }

            @Override
            public void onResume() {
                super.onResume();
                getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            }

            @Override
            public void onPause() {
                super.onPause();
                getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            }


            @Override
            public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
                Log.i(TAG, "onPreferenceTreeClick: ");
                getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }

            private void updatePeerSummary() {
                EditTextPreference costomPeer = (EditTextPreference) findPreference(PEER_ADDRESS_KEY);
                String currentPeer = getPreferenceManager().getSharedPreferences().getString(PEER_ADDRESS_KEY, MainActivity.DEFAULT_PEER);
                costomPeer.setText(currentPeer);
                costomPeer.setSummary(currentPeer);
            }

            private void updateWalletSummary() {
                EditTextPreference wallet = (EditTextPreference) findPreference(WALLET_KEY);
                String currentWallet = getPreferenceManager().getSharedPreferences().getString(WALLET_KEY, MainActivity.DEFAULT_ADDRESS);
                wallet.setText(currentWallet);
                wallet.setSummary(currentWallet);
            }
        }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(Activity.RESULT_OK, data);
        super.onBackPressed();
    }
}