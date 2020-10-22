package com.curiousdev.moviesdiscover.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.curiousdev.moviesdiscover.R;

import static com.curiousdev.moviesdiscover.Activities.FragmentsHost.hostContext;

public class AccountsFragment extends Fragment implements View.OnClickListener {

//    Widgets
    private TextView haveAccount,dontHaveAccount,forgetPassword;
    private Button signUp,logIn;
    private EditText logEmail,logPassword,signEmail,signUserName,signPassword;

//    layout
    private View accountView;
    private RelativeLayout loginLayout,signLayout;
//    layout Toggler variables
    private final int SHOW_LOGIN=0;
    private final int SHOW_SIGNUP=1;
    private int CURRENT_SHOWN=SHOW_LOGIN;
    //host activity context;
    private Context context=hostContext;
//    animations
    private Animation fadeIn,fadeOut;
    public static AccountsFragment accountsFragmentInstance;
    public static AccountsFragment getAccountsFragmentInstance(){
        if(accountsFragmentInstance==null){
            accountsFragmentInstance=new AccountsFragment();
        }
        return accountsFragmentInstance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (accountView==null){
            accountView=inflater.inflate(R.layout.fragment_accounts, container, false);
            //containers
            loginLayout=accountView.findViewById(R.id.login_layout);
            signLayout=accountView.findViewById(R.id.sign_layout);
            //editTexts
            signUserName=accountView.findViewById(R.id.user_name);
            signEmail=accountView.findViewById(R.id.new_user_email);
            signPassword=accountView.findViewById(R.id.new_user_password);
            logEmail=accountView.findViewById(R.id.login_email);
            logPassword=accountView.findViewById(R.id.login_password);
            //Textviews
            haveAccount=accountView.findViewById(R.id.have_account);
            dontHaveAccount=accountView.findViewById(R.id.dont_have_account);
            forgetPassword=accountView.findViewById(R.id.forget_password);
            //Buttons
            signUp=accountView.findViewById(R.id.sign_now);
            logIn=accountView.findViewById(R.id.login_now);
            //animation
            fadeIn= AnimationUtils.loadAnimation(context,R.anim.fade_in);
            fadeOut= AnimationUtils.loadAnimation(context,R.anim.fade_out);
            //set handler event
            haveAccount.setOnClickListener(this);
            dontHaveAccount.setOnClickListener(this);
            forgetPassword.setOnClickListener(this);
            signUp.setOnClickListener(this);
            logIn.setOnClickListener(this);
        }
        return accountView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.have_account:{
                layoutSwapper(SHOW_LOGIN);
                break;
            }
            case R.id.dont_have_account:{
                layoutSwapper(SHOW_SIGNUP);
                break;
            }
        }
    }

    private void layoutSwapper(int layout){
        if(layout==SHOW_LOGIN){
            loginLayout.setVisibility(View.VISIBLE);
            signLayout.setVisibility(View.GONE);
            loginLayout.startAnimation(fadeIn);
            signLayout.startAnimation(fadeOut);
        }
        else if(layout==SHOW_SIGNUP){
            signLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            signLayout.startAnimation(fadeIn);
            loginLayout.startAnimation(fadeOut);
        }

    }
}
