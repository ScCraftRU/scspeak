package ru.sccraft.scspeak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import ru.sccraft.scspeak.util.IabHelper;
import ru.sccraft.scspeak.util.IabResult;
import ru.sccraft.scspeak.util.Inventory;
import ru.sccraft.scspeak.util.Purchase;

public class DisableADsActivity extends AppCompatActivity {

    IabHelper mHelper;
    boolean adsDisabled = false;
    private String TAG = "DisableADsActivity";
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }
            else if (purchase.getSku().equals("ru.sccraft.scspeak.disableads")) {
                adsDisabled = true;
                // consume the gas and update the UI
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                // handle error here
            }
            else {
                // does the user have the premium upgrade?
                adsDisabled = inventory.hasPurchase("ru.sccraft.scspeak.disableads");
                // update UI accordingly
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable_ads);
        setTitle(getString(R.string.disableADs));
        String base64EncodedPublicKey = "";

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    private void buy() {
        try {
            mHelper.launchPurchaseFlow(this, "ru.sccraft.scspeak.disableads", 10001, mPurchaseFinishedListener, "Язык: " + MainActivity.language);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    private void restore() {
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    public void buy(View view) {
        buy();
    }

    public void restore(View view) {
        restore();
    }
}