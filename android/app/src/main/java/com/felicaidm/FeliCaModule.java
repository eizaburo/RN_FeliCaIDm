package com.felicaidm;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Formatter;
import java.util.Locale;

import javax.annotation.Nullable;

public class FeliCaModule extends ReactContextBaseJavaModule {

    NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this.getReactApplicationContext());

    public FeliCaModule(ReactApplicationContext reactContext){
        super(reactContext);
    }

    @Override
    //RN側でFeliCaで呼び出せる
    public String getName(){
        return "FeliCa";
    }

    @ReactMethod
    //Polling開始（ReaderMode利用）
    public void startPolling(Callback callback){
        nfcAdapter.enableReaderMode(this.getCurrentActivity(),new MyReaderCallback(this.getReactApplicationContext()),NfcAdapter.FLAG_READER_NFC_F,null);
        callback.invoke("start polling...");
    }

    @ReactMethod
    //Polling停止
    public void stopPolling(Callback callback){
        nfcAdapter.disableReaderMode(this.getCurrentActivity());
        callback.invoke("stop polling...");

    }

    //ReaderModeの引数にわたすCallbackをinner classで実装
    //Callback内でEventを発火させる
    private class MyReaderCallback implements NfcAdapter.ReaderCallback{

        ReactApplicationContext context;

        MyReaderCallback(ReactApplicationContext reactContext){
            context = reactContext;
        }

        @Override
        public void onTagDiscovered(Tag tag){

            //IDm取得(tag.getId()でsystem0のIDmが取れる。複数systemがある場合は注意）
            String idmString = bytesToHexString(tag.getId());
            Log.d("Hoge","IDm=" + idmString);

            //必要に応じて取得したtag使ってNfcF生成してtransceive()でいろいろすればよい

            //渡すパラメータ定義(emitの際 WritableMapである必要があるため）
            WritableMap params = Arguments.createMap();
            params.putString("idm", idmString);
            //sendEvent
            sendEvent(context,"onTagDiscovered",params);

        }
    }

    //SendEvent（ここでは1つしかEventが無いが、普通は複数あるので関数化しておく）
    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params){
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName,params);
    }

    //bytes列を16進数文字列に変換
    public static String bytesToHexString(byte[] bytes) {

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString().toUpperCase();
    }
}
