package georg.steinbacher.ark_news;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import georg.steinbacher.ark_news.ark.Transaction;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String GET_TRANSACTIONS_URL = "https://api.arkcoin.net/api/transactions?recipientId=AZHXnQAYajd3XkxwwiL6jnLjtDHjtAATtR&offset=0";

    private Context mContext;
    private ArrayList<Transaction> mTransactionsList = new ArrayList<>();

    private JsonObjectRequest mGetTransactionsRequest = new JsonObjectRequest
            (Request.Method.GET, GET_TRANSACTIONS_URL, null, new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            ListView listView = (ListView) findViewById(R.id.main_listview);


            try {
                JSONArray transactions = response.getJSONArray("transactions");

                for(int i=0; i<transactions.length(); i++) {
                    if(transactions.getJSONObject(i).has("vendorField"))
                        mTransactionsList.add(new Transaction(transactions.getJSONObject(i)));
                }

                //sort
                Collections.sort(mTransactionsList, new Comparator<Transaction>() {
                    public int compare(Transaction t1, Transaction t2){
                        if(t1.getId().equals(t2.getId())) {
                            return 0;
                        } else if (t1.getTimestamp() > t2.getTimestamp()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

                TransactionsAdapter adapter = new TransactionsAdapter(mContext, R.layout.main_listview_row, mTransactionsList);
                listView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            // TODO Auto-generated method stub

        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(mGetTransactionsRequest);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
