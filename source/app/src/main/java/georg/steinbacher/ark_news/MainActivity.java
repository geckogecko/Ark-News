package georg.steinbacher.ark_news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import georg.steinbacher.ark_news.ark.Transaction;
import georg.steinbacher.ark_news.requests.RequestQueueSingleton;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "MainActivity";

    public static final String DEFAULT_PEER = "https://explorer.ark.io:8443/api/";
    public static final String DEFAULT_ADDRESS = "AZHXnQAYajd3XkxwwiL6jnLjtDHjtAATtR";

    public static final String DEFAULT_TESTNET_PEER = "https://dexplorer.ark.io:8443/api/";
    public static final String DEFAULT_TESTNET_ADDRESS = "DLRj8juWwPBapTeNakxQGbrJcWib7gFhkN";

    private static final String GET_TRANSACTIONS_URL = "transactions?recipientId=";

    private static final int SETTINGS_RESULT_CODE = 1;

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Transaction> mTransactionsList = new ArrayList<>();
    private int mTransactionsCount = 0;
    private int mCurrentOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mContext = this;

        //start pulling all transactions
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();

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
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivityForResult(i, SETTINGS_RESULT_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SETTINGS_RESULT_CODE){
            //start pulling all transactions
            mSwipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        mTransactionsList.clear();
        mTransactionsCount = 0;
        mCurrentOffset = 0;
        pullTransactions();
    }

    private void addTransactions(JSONArray response) throws JSONException{
        for(int i=0; i<response.length(); i++) {
            if(response.getJSONObject(i).has("vendorField"))
                mTransactionsList.add(new Transaction(response.getJSONObject(i)));
        }
    }

    private void pullTransactions() {
        JsonObjectRequest getTransactionsRequest = new JsonObjectRequest
                (Request.Method.GET, getPeerURLFromPref() + GET_TRANSACTIONS_URL + getAddressFromPref() + "&offset=" + mCurrentOffset, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray transactions = response.getJSONArray("transactions");
                            addTransactions(transactions);

                            if(response.has("count")) {
                                mTransactionsCount = response.getInt("count");
                            } else {
                                mTransactionsCount = -1;
                            }
                            mCurrentOffset+=50;

                            if(mTransactionsCount > mCurrentOffset || (mTransactionsCount == -1 && transactions.length() >= 50)) {
                                pullTransactions();
                            } else {
                                //show the results
                                ListView listView = (ListView) findViewById(R.id.main_listview);

                                sortByTimestamp(mTransactionsList);
                                TransactionsAdapter adapter = new TransactionsAdapter(mContext, R.layout.main_listview_row, mTransactionsList);
                                listView.setAdapter(adapter);

                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: " + error.toString());
                        Toast.makeText(mContext, getString(R.string.error_cannot_reach_peer), Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(getTransactionsRequest);
    }

    private void sortByTimestamp(ArrayList<Transaction> transactions) {
        //sort
        Collections.sort(transactions, new Comparator<Transaction>() {
            public int compare(Transaction t1, Transaction t2){
                if(t1.getId().equals(t2.getId())) {
                    return 0;
                } else if (t1.getTimestamp() < t2.getTimestamp()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    private String getPeerURLFromPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(PreferencesActivity.PEER_ADDRESS_KEY, DEFAULT_PEER);
    }

    private String getAddressFromPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(PreferencesActivity.WALLET_KEY, DEFAULT_ADDRESS);
    }
}
