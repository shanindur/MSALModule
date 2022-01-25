package com.msal_pugin;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;
import com.facebook.react.bridge.Callback;

import java.util.HashMap;

public class MSALModule extends ReactContextBaseJavaModule {
    private final static String[] SCOPES = {"api://dc0311ee-20aa-417b-bd46-69edd5559d45/impersonate"};
    /* Azure AD v2 Configs */
    final static String AUTHORITY = "https://login.microsoftonline.com/9d95cde8-c4ec-4b9c-8ee2-249d44b79acf";
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private static final String TAG = "MSALModule";
    Context context;

    MSALModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "MSALModule";
    }

    @ReactMethod
    public void initializeMSAL(Callback callback) {
        /// Initialization logic goes here
        PublicClientApplication.createSingleAccountPublicClientApplication(getReactApplicationContext(),
                R.raw.auth_config_single_account, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mSingleAccountApp = application;
                        Log.d(TAG, "Authentication failed: " + application.toString());
//                        callback.invoke("Success");
                        loadAccount(callback);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.d(TAG, "Authentication failed: " + exception.toString());
                        callback.invoke("Failed");
                    }
                });
    }

    @ReactMethod
    public void signSilently() {
        mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
    }

    @ReactMethod
    public void loadAccount(Callback callback) {
        if (mSingleAccountApp == null) {
            callback.invoke("Not found");
        }

        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
                if (activeAccount == null) {
                    callback.invoke("Not found");
                    return;
                }
                Log.d("account details", activeAccount.toString());
                callback.invoke(activeAccount.getIdToken());
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {

            }


            @Override
            public void onError(@NonNull MsalException exception) {
                callback.invoke("Not found");
            }
        });
    }

    @ReactMethod
    public void signInUser(Callback callBack) {
        mSingleAccountApp.signIn(getCurrentActivity(), null, SCOPES, getAuthInteractiveCallback(callBack));
    }

    private AuthenticationCallback getAuthInteractiveCallback(Callback callBack) {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "Token: " + authenticationResult.getAccessToken().toString());

                HashMap<String, String> map = new HashMap<>();
                map.put("token", authenticationResult.getAccessToken());
                Log.d("check", map.toString());
                callBack.invoke(map);

                /* Update UI */
//                updateUI(authenticationResult.getAccount());
//                /* call graph */
//                callGraphAPI(authenticationResult);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
//                displayError(exception);
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    private SilentAuthenticationCallback getAuthSilentCallback() {
        return new SilentAuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                Log.d(TAG, "Successfully authenticated" + authenticationResult);
            }

            @Override
            public void onError(MsalException exception) {
                Log.d(TAG, "Authentication failed: " + exception.toString());
            }
        };
    }
}
