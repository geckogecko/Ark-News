package georg.steinbacher.ark_news;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        vendorField.setText(formatLinkInString(currentItem.getVendorField()));
        vendorField.setMovementMethod(LinkMovementMethod.getInstance());
        vendorField.setClickable(true);

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

    // Pattern for recognizing a URL, based off RFC 3986
    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private SpannableString formatLinkInString(String string) {
        Matcher matcher = urlPattern.matcher(string);
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            // now you have the offsets of a URL match
            SpannableString spannableString = new SpannableString(string);
            spannableString.setSpan(new URLSpan(string.substring(matchStart, matchEnd)), matchStart,matchEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
        return new SpannableString(string);
    }
}
