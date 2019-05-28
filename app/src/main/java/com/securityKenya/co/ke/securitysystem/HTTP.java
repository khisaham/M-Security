package com.securityKenya.co.ke.securitysystem;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class HTTP {
    private static final String TAG = "HTTP";
    public static final String EP_RIDER_POSITION = "rider_position";
    public static final String EP_SEND_DATA = "rider_app_login";
    public static final String EP_REJECT_DELIVERY = "rider_app_reject";


    public static final int TUMAXPRESS_SOCKET_PORT = 8992;
    public static final int DEFAULT_TIMEOUT = 60000;
    public static final String TUMAXPRESS_HOST = "35.189.76.21";
    public static final String URI_IMG_STORE = "http://tumaxpress.com/parcel/doc/photo/";
    public static final int TUMAXPRESS_API_PORT = 8080;
    public static final String TUMAXPRESS_API_PROTOCOL = "http";

    public static final void sendRequest(Context context, String endpoint, final JSONObject request, final OnHTTPResponseListener onHTTPResponseListener) {
        String urlString = TUMAXPRESS_API_PROTOCOL + "://" + TUMAXPRESS_HOST + ":" + TUMAXPRESS_API_PORT + "/" + endpoint;
        Log.d(TAG, "Sending request to "+urlString);
        Log.d(TAG, "Request = "+request.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlString, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onHTTPResponseListener.onHTTPResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onHTTPResponseListener.onHTTPError(error);
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.e(TAG, "Could not send request because the device ran out of memory");
            onHTTPResponseListener.onHTTPError(new VolleyError("Could not send request because the device ran out of memory"));
        }
    }

    public static final ImageLoader getImageLoader(Context context) {
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        try {
            return new ImageLoader(Volley.newRequestQueue(context), imageCache);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method checks whether a connection to the server is available
     *
     * @param context   The context to use to check whether the connection is available
     *
     * @return  TRUE if able to connect before the timeout
     */
    public static void isConnectedToServer(Context context, final OnHTTPResponseListener onHTTPResponseListener) {
        String urlString = TUMAXPRESS_API_PROTOCOL + "://" + TUMAXPRESS_HOST + ":" + TUMAXPRESS_API_PORT + "/" + EP_SEND_DATA;
        Log.d(TAG, "Checking if can connect to server "+urlString);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlString, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onHTTPResponseListener.onHTTPResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onHTTPResponseListener.onHTTPError(error);
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.e(TAG, "Could not send request because the device ran out of memory");
            onHTTPResponseListener.onHTTPError(new VolleyError("Could not send request because the device ran out of memory"));
        }
    }

    /**
     * This method checks if device is connected to the internet (and not the tumaxpress server)
     *
     * @param context   The context from where this method is being called
     * @return  TRUE if connected to the internet
     */
    public static boolean isEnabled(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public static void buildEnableDialog(final Activity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage("Please enabled your internet inorder to get updates from tumaxpress")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        activity.startActivity(intent);
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    public interface OnHTTPResponseListener {
        void onHTTPResponse(JSONObject response);

        void onHTTPError(VolleyError volleyError);
    }

    private static class BitmapLruCache
            extends LruCache<String, Bitmap>
            implements ImageLoader.ImageCache {

        public BitmapLruCache() {
            this(getDefaultLruCacheSize());
        }

        public BitmapLruCache(int sizeInKiloBytes) {
            super(sizeInKiloBytes);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }

        public static int getDefaultLruCacheSize() {
            final int maxMemory =
                    (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;

            return cacheSize;
        }
    }
}
