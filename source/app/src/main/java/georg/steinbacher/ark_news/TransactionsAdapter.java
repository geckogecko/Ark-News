package georg.steinbacher.ark_news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        dateField.setText(getDateString(currentItem.getTimestamp()));

        if((currentItem.getAmount()) >= 100000000)
            vendorField.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        else
            vendorField.setTextColor(mContext.getResources().getColor(R.color.colorText));

        return view;
    }

    private static final long FIRST_BLOCK_TIMESTAMP = 1458586800;
    private String getDateString(long seconds) {
        Date d = new Date((seconds * 1000 + FIRST_BLOCK_TIMESTAMP * 1000));
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return df.format(d);
    }
}
