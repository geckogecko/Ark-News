package georg.steinbacher.ark_news.ark;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by georg on 28.12.17.
 */

public class Transaction {
    private static final String TAG = "Transaction";

    private String mId;
    private long mBlockID;
    private int mTimestamp;
    private int mAmount;
    private int mFee;
    private String mVendorField;
    private String mSenderId;
    private String mRecipientId;

    public Transaction(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.getString("id");
        mBlockID = jsonObject.getLong("blockid");
        mTimestamp = jsonObject.getInt("timestamp");
        mAmount = jsonObject.getInt("amount");
        mFee = jsonObject.getInt("fee");
        mVendorField = jsonObject.getString("vendorField");
        mSenderId = jsonObject.getString("senderId");
        mRecipientId = jsonObject.getString("recipientId");
    }

    public String getId() {
        return mId;
    }

    public long getBlockId() {
        return mBlockID;
    }

    public int getTimestamp() {
        return mTimestamp;
    }

    public int getAmount() {
        return mAmount;
    }

    public String getVendorField() {
        return mVendorField;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public String getRecipientId() {
        return mRecipientId;
    }


}


