package georg.steinbacher.ark_news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import georg.steinbacher.ark_news.ark.Transaction;

/**
 * Created by georg on 28.12.17.
 */

public class TransactionsAdapter extends ArrayAdapter<Transaction>{
    private static final String TAG = "TransactionsAdapter";

    private Context mContext;
    private List<Transaction> mItems;

    public TransactionsAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> objects) {
        super(context, resource, objects);

        mContext = context;
        mItems = objects;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.main_listview_row, null);
        }

        Transaction currentItem = mItems.get(position);

        TextView vendorField = view.findViewById(R.id.row_vendorField);
        TextView dateField = view.findViewById(R.id.row_date);

        vendorField.setText(currentItem.getVendorField());
        dateField.setText(Integer.toString(currentItem.getTimestamp()));

        return view;
    }
}
