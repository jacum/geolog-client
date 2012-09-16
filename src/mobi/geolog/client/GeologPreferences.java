package mobi.geolog.client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;

/**
 * @author timur
 */
public class GeologPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String FREQUENCY_PREF = "frequencyPref";
    public static final String HOSTNAME_PREF = "hostNamePref";
    public static final String PORT_PREF = "portPref";
    public static final String IMEI_PREF = "imeiPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
            updateSummary(getPreferenceScreen().getPreference(i));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference pref = findPreference(key);
        updateSummary(pref);
    }

    private void updateSummary(Preference pref) {
        if (pref instanceof PreferenceCategory){
            PreferenceCategory pCat = (PreferenceCategory)pref;
            for(int i=0;i<pCat.getPreferenceCount();i++){
                updateSummary(pCat.getPreference(i));
            }

        } else if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        } else if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) pref;
            pref.setSummary(editTextPref.getText());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }
    /*
        // Get the custom preference
        Preference customPref = (Preference) findPreference("customPref");
        customPref
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                        Toast.makeText(getBaseContext(),
                                "The custom preference has been clicked",
                                Toast.LENGTH_LONG).show();
                        SharedPreferences customSharedPreference = getSharedPreferences(
                                "myCustomSharedPrefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = customSharedPreference
                                .edit();
                        editor.putString("myCustomPref",
                                "The preference has been clicked");
                        editor.commit();
                        return true;
                    }

                });
*/


}
