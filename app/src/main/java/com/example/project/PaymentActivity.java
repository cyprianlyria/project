package com.example.project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.model.AccessToken;
import com.example.project.model.STKPush;
import com.example.project.databinding.ActivityPaymentBinding;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PaymentActivity";
    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;
    private ActivityPaymentBinding binding;

    // Constants (should be stored securely in a production app)
    private static final String BUSINESS_SHORT_CODE = "174379";
    private static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    private static final String CALLBACK_URL = "https://your-actual-callback-url.com/path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mProgressDialog = new ProgressDialog(this);
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true); // Set True to enable logging, false to disable.

        binding.btnPay.setOnClickListener(this);

        getAccessToken();
    }

    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to get access token: " + t.getMessage());
                Toast.makeText(PaymentActivity.this, "Failed to get access token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnPay) {
            String phoneNumber = binding.etPhone.getText().toString();
            String amount = binding.etAmount.getText().toString();
            if (phoneNumber.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "Please enter both phone number and amount", Toast.LENGTH_SHORT).show();
                return;
            }
            performSTKPush(phoneNumber, amount);
        }
    }

    public void performSTKPush(String phoneNumber, String amount) {
        mProgressDialog.setMessage("Processing your request");
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        String timestamp = Utils.getTimestamp();
        String toEncode = BUSINESS_SHORT_CODE + PASSKEY + timestamp;

        byte[] byteArray = toEncode.getBytes(StandardCharsets.UTF_8);

        String encodedPassword;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodedPassword = Base64.getEncoder().encodeToString(byteArray);
        } else {
            encodedPassword = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
        }

        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                encodedPassword,
                timestamp,
                "CustomerPayBillOnline",
                Integer.parseInt(amount),
                Utils.sanitizePhoneNumber(phoneNumber),
                BUSINESS_SHORT_CODE,
                Utils.sanitizePhoneNumber(phoneNumber),
                CALLBACK_URL,
                "Luxury Network company powered by Starlink",
                "Payment of X"
        );

        mApiClient.setGetAccessToken(false);

        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Payment request submitted successfully. " + response.body());
                        Toast.makeText(PaymentActivity.this, "Payment request sent successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Payment request failed. Response: " + response.errorBody().string());
                        Toast.makeText(PaymentActivity.this, "Payment request failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing payment response", e);
                    Toast.makeText(PaymentActivity.this, "Error processing payment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Log.e(TAG, "Payment request failed", t);
                Toast.makeText(PaymentActivity.this, "Payment request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}