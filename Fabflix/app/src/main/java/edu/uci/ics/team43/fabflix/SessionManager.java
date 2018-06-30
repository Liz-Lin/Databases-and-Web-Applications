package edu.uci.ics.team43.fabflix;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;

public class SessionManager {
    private static SessionManager instance = null;
    private boolean isLogin=false;

    public boolean isUserLogin(Context context){
        // user has already login, and the session still maintains
        if (isLogin && CookieHandler.getDefault()!=null)return true;
        // user has not yet login or the cookie expired

        // send request to server to check if session still maintains.
        String url = context.getResources().getString(R.string.API_host)+ context.getResources().getString(R.string.API_is_user_login);
        final StringRequest loginRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jObject = new JSONObject(response);
                            SessionManager.getInstance().isLogin = jObject.getBoolean("is_user_login");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("security.error", error.toString());
                error.printStackTrace();
            }
        });
        final RequestQueue queue = NetworkManager.sharedManager(context).queue;
        queue.add(loginRequest);
        return instance.isLogin;
    }

    public static SessionManager getInstance() {
        if (instance==null) instance = new SessionManager();
        return instance;
    }

    public void setIsLoginTrue()
    {
        isLogin=true;
    }
}
