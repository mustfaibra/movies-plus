package com.curiousdev.moviesdiscover.Models;

//import android.content.Context;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;
//
//public class VolleyRequest {
//    public static VolleyRequest mInstance;
//    Context mcontext;
//    RequestQueue requestQueue;
//    private VolleyRequest(Context context){
//        this.mcontext=context;
//        requestQueue=getRequestQueue();
//    }
//    static synchronized VolleyRequest getInstance(Context context){
//        if (mInstance==null){
//            mInstance= new VolleyRequest(context);
//        }
//        return mInstance;
//    }
//    public RequestQueue getRequestQueue(){
//        if (requestQueue==null){
//            requestQueue=Volley.newRequestQueue(mcontext.getApplicationContext());
//        }
//        return requestQueue;
//    }
//    public <T> void addToRequestQueue(Request<T> request){
//        getRequestQueue().add(request);
//    }
//}
