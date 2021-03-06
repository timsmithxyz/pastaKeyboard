package com.lenny.timtom.pastakeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tim on 2/28/16.
 */
public class PastaKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean caps = false;

    List<String> data = new ArrayList<String>();
    String emoji;

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);

        String url = "http://45.79.176.133/api.php";


        StringRequest pastaReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String tmp = response.substring("Array ( [0] => ".length(),response.length() - 2);
                        String[] tmp2 = tmp.split("] => ");
                        for (int i = 0; i < tmp2.length; i++) {
                            if (i == 0) {
                                data.add(StringEscapeUtils.unescapeHtml(tmp2[i].substring(" => ".length(), tmp2[i].length() - 3)));
                            } else {
                                data.add(StringEscapeUtils.unescapeHtml(tmp2[i].substring(0, tmp2[i].length() - 3)));
                            }
                        }
                        for (String s : data) {
                            Log.w("WARBLGARBL", s);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("HTTP Req", error.toString());
            }
        }
        );

        int socketTimeout = 100000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        pastaReq.setRetryPolicy(policy);

        Volley.newRequestQueue(this).add(pastaReq);

        return kv;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                //ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_LANGUAGE_SWITCH));

            default:
                char code = (char) primaryCode;
                if(code < 6 && code >= 0){
                    ic.commitText(data.get(code-1), 0);
                }

                else if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        ic.commitText(text, 0);
        ic.endBatchEdit();
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }
}