package georg.steinbacher.ark_news;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by georg on 29.12.17.
 */

public class PreferencesActivity extends AppCompatActivity {
        private static final String TAG = "PreferencesActivity";

        public static final String PEER_ADDRESS_KEY = "settings_peer_address";

        private static Context mContext;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_preferences);

            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsPreferenceFragment()).commit();

            mContext = getApplicationContext();
        }

        public static class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
        {
            @Override
            public void onCreate(final Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);

                EditTextPreference costomPeer = (EditTextPreference) findPreference(PEER_ADDRESS_KEY);
                costomPeer.setText(MainActivity.DEFAULT_PEER);

                String currentPeer = getPreferenceManager().getSharedPreferences().getString(PEER_ADDRESS_KEY, MainActivity.DEFAULT_PEER);
                costomPeer.setSummary(currentPeer);

            }

            @Override
            public void onResume() {
                super.onResume();
                getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            }

            @Override
            public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
                getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }
        }
    }