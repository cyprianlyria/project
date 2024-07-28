package com.example.project;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://sandbox.safaricom.co.ke/";
    private static final String AUTHORIZATION_TOKEN = "Bearer QGFA8pmyFHYX6kq306YR4FVowvST";
    private static final String CALLBACK_URL = "https://mydomain.com/path";
    private MpesaApi mpesaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        EditText editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        Button buttonPay = findViewById(R.id.buttonPay);

        try {
            setupMpesaApi();
        } catch (Exception e) {
            Log.e("PaymentActivity", "Error setting up Mpesa API", e);
            Toast.makeText(this, "Error setting up payment service", Toast.LENGTH_SHORT).show();
        }

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String phoneNumber = editTextPhoneNumber.getText().toString().trim();




            }
        });
    }

    private void setupMpesaApi() throws Exception {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mpesaApi = retrofit.create(MpesaApi.class);
    }

    private void makePayment(String phoneNumber) throws Exception {
        try {
            String businessShortCode = "174379";
            String passkey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

            // Generate the password
            String password = generatePassword(businessShortCode, passkey, timestamp);

            // Ensure the phone number is in the correct format
            String formattedPhoneNumber = formatPhoneNumber(phoneNumber);

            STKPushRequest request = new STKPushRequest(
                    businessShortCode,
                    password,
                    timestamp,
                    "CustomerPayBillOnline",
                    "1", // Amount
                    formattedPhoneNumber,
                    businessShortCode,
                    CALLBACK_URL,
                    "CompanyXLTD",
                    "Payment of X"
            );

            Call<MpesaResponse> call = mpesaApi.stkPush(AUTHORIZATION_TOKEN, request);

            call.enqueue(new Callback<MpesaResponse>() {
                @Override
                public void onResponse(Call<MpesaResponse> call, Response<MpesaResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(PaymentActivity.this, response.body().getCustomerMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PaymentActivity.this, "Payment failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        try {
                            Log.e("PaymentActivity", "Error: " + (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MpesaResponse> call, Throwable t) {
                    Toast.makeText(PaymentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("PaymentActivity", "Failure: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("PaymentActivity", "Error making payment", e);
            throw e;
        }
    }

    // Helper method to format phone number
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "254" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }

    // Method to generate password
    private String generatePassword(String businessShortCode, String passkey, String timestamp) {
        String data = businessShortCode + passkey + timestamp;
        return Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
    }
}
