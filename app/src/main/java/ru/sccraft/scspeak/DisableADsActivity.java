package ru.sccraft.scspeak;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisableADsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "DisableADsActivity";
    private BillingClient mBillingClient;
    Fe fe;
    Button buyButton;

    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private String интендификатор_товара = "ru.sccraft.scspeak.disableads";

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        return purchasesResult.getPurchasesList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable_ads);
        setTitle(getString(R.string.disableADs));
        setupActionBar();
        buyButton = findViewById(R.id.button_buy);
        fe = new Fe(this);

        mBillingClient = BillingClient.newBuilder(this).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                //сюда мы попадем когда будет осуществлена покупка
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    восстановить();
                }
            }
        }).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    //здесь мы можем запросить информацию о товарах и покупках
                    получить_информацию_о_товарах();

                    List<Purchase> purchasesList = queryPurchases(); //запрос о покупках

                    //если товар уже куплен, предоставить его пользователю
                    for (int i = 0; i < purchasesList.size(); i++) {
                        String purchaseId = purchasesList.get(i).getSku();
                        if(TextUtils.equals(интендификатор_товара, purchaseId)) {
                            восстановить();
                        }
                    }
                }
                else {
                    Log.w(LOG_TAG, "Ошибка In-App Billing №" + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
                    показать_диалог_ошибки(billingResult.getDebugMessage());
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                //сюда мы попадем если что-то пойдет не так
            }
        });
    }

    private void получить_информацию_о_товарах() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(интендификатор_товара);
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                        String цена = skuDetails.getPrice();
                        if (skuDetails.getSku().equals(интендификатор_товара)) {
                            String текст = buyButton.getText().toString();
                            текст = текст + " (" + цена + ")";
                            buyButton.setText(текст);
                        }
                    }
                }
            }
        });
    }

    private void показать_диалог_ошибки(String сообщение) {
        AlertDialog.Builder диалог = new AlertDialog.Builder(this);
        диалог.setTitle("ERROR")
                .setMessage(сообщение)
                .setCancelable(true)
                .setIcon(android.R.drawable.stat_notify_error)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        диалог.show();
    }

    public void купить(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(skuId))
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void восстановить() {
        //Зачем покупать, если можно с root файл создать... (или переименовать adid)
        fe.saveFile("scspeak-ads", "1");
        Log.i(LOG_TAG, "Реклама отключена!");
        Toast.makeText(getApplicationContext(), R.string.adsDisabled, Toast.LENGTH_SHORT).show();
        finish();
    }
    public void buy(View view) {
        купить(интендификатор_товара);
    }

    public void restore(View view) {
        восстановить();
    }
}