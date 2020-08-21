package com.quiriletelese.troppadvisorproject.cognito;

import android.content.Context;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class CognitoSettings {
    private CognitoUserPool cognitoUserPool;
    private CognitoUserAttributes cognitoUserAttributes;
    private Context context;
    SignUpHandler signUpCallback = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            Toast.makeText(context, "Link verifica inviato a " + cognitoUserCodeDeliveryDetails.getDestination(), Toast.LENGTH_LONG)
                    .show();
            if (userConfirmed)
                Toast.makeText(context, "Errore: l'utente era già stato confermato", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Exception exception) {
            Toast.makeText(context, "Errore durante la registrazione: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    // Callback handler for confirmSignUp API
    GenericHandler confirmationCallback = new GenericHandler() {
        @Override
        public void onSuccess() {
            Toast.makeText(context, "Account confermato", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Exception exception) {
            // User confirmation failed. Check exception for the cause.
        }
    };
    private String userPassword;
    // Callback handler for the sign-in process
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }

        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            if (!userSession.isValid())
                Toast.makeText(context, "Rifai il login", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Login valido", Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, "Sign in success", Toast.LENGTH_LONG).show();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, userPassword, null);
            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);
            // Allow the sign-in to continue
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            // Multi-factor authentication is required; get the verification code from user
            //multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
            //multiFactorAuthenticationContinuation.continueTask();
        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-in failed, check exception for the cause
            Toast.makeText(context, "Sign in Failure " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    public CognitoSettings(Context context) {
        this.context = context;
        String poolID = "us-east-1_Ta8vx4mFy";
        String clientID = "3lr4t6rq94k63vno3ceahd3ije";
        String clientSecret = "1m104g5k7g5pbhc9qsuedrukj1a5hgeil6ipni3rl7pfec2q3ikn";
        Regions awsRegion = Regions.US_EAST_1;
        cognitoUserPool = new CognitoUserPool(context, poolID, clientID, clientSecret, awsRegion);
        cognitoUserAttributes = new CognitoUserAttributes();
    }

    public void signUpInBackground(String username, String password) {
        cognitoUserPool.signUpInBackground(username, password, this.cognitoUserAttributes, null, signUpCallback);
    }

    public void confirmUser(String userId, String code) {
        CognitoUser cognitoUser = cognitoUserPool.getUser(userId);
        cognitoUser.confirmSignUpInBackground(code, false, confirmationCallback);
    }

    public void addAttribute(String key, String value) {
        cognitoUserAttributes.addAttribute(key, value);
    }

    public void userLogin(String userId, String password) {
        CognitoUser cognitoUser = cognitoUserPool.getUser(userId);
        userPassword = password;
        cognitoUser.getSessionInBackground(authenticationHandler);
    }

    public void tokenIsValid() {
        CognitoUser cognitoUser = cognitoUserPool.getCurrentUser();
        cognitoUser.getSessionInBackground(authenticationHandler);
    }

    public CognitoUserPool getCognitoUserPool() {
        return cognitoUserPool;
    }
}
