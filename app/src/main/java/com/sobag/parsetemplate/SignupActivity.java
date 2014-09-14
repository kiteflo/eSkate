package com.sobag.parsetemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.inject.Inject;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.fb.FacebookHandler;
import com.sobag.parsetemplate.services.SignupListener;
import com.sobag.parsetemplate.services.ParseSignupService;
import com.sobag.parsetemplate.util.FontUtility;
import com.sobag.parsetemplate.util.ResourceUtility;

import java.util.Arrays;
import java.util.List;

import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContextSingleton
public class SignupActivity extends CommonHeadlessActivity
        implements Validator.ValidationListener, SignupListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Validator validator;

    @Inject
    FontUtility fontUtility;
    @Inject
    ParseSignupService parseSignupService;

    // UI components...

    @InjectView(tag = "progressBar") ProgressBar progressBar;

    @InjectView(tag = "tv_title") TextView tvTitle;
    @InjectView(tag = "tv_fb_button_label") TextView tvFbButtonLabel;
    @InjectView(tag = "tv_signup_button_label") TextView tvSignupButtonLabel;

    @Required(order = 1, message = "msg_requiredField")
    @InjectView(tag = "et_username") EditText etUsername;

    @Required(order = 2, message = "msg_requiredField")
    @Email(order = 3, message = "msg_emailFormat")
    @InjectView(tag = "et_email") EditText etEmail;

    @Password(order = 4, message = "msg_requiredField")
    @TextRule(order = 5, minLength = 6, message = "msg_weakPassword")
    @InjectView(tag = "et_password") EditText etPassword;

    @ConfirmPassword(order = 6, message = "msg_passwordMismatch")
    @InjectView(tag = "et_passwordRepeat") EditText etPasswordRepeat;

    // ------------------------------------------------------------------------
    // default stuff
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email);

        // apply font...
        fontUtility.applyFontToComponent(tvTitle,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(etUsername,R.string.default_font,
                FontApplicableComponent.EDIT_TEXT);
        fontUtility.applyFontToComponent(etEmail,R.string.default_font,
                FontApplicableComponent.EDIT_TEXT);
        fontUtility.applyFontToComponent(etPassword,R.string.default_font,
                FontApplicableComponent.EDIT_TEXT);
        fontUtility.applyFontToComponent(etPasswordRepeat,R.string.default_font,
                FontApplicableComponent.EDIT_TEXT);
        fontUtility.applyFontToComponent(tvFbButtonLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvSignupButtonLabel,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);

        // init validator....
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    /**
     * User presses create button...
     * @param view
     */
    public void onCreateAccount(View view)
    {
        validator.validate();
    }

    public void onFacebookLogin(View view)
    {
        // display loading indicator...
        progressBar.setVisibility(View.VISIBLE);

        parseSignupService.loginOrSignupWithFacebook(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    // ------------------------------------------------------------------------
    // validation implementation
    // ------------------------------------------------------------------------

    /**
     * Validation passed...trigger server call...
     */
    public void onValidationSucceeded()
    {
        Ln.d("Validated successfully!");

        // indicate loading within button...
        tvSignupButtonLabel.setText(getString(R.string.but_register_loading));

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();

        parseSignupService.signupViaEmail(username,password,email,this);
    }

    public void onValidationFailed(View failedView, Rule<?> failedRule)
    {
        Ln.d("Validation failed!");

        // little hack - within a module we can not specify a message ID directly
        // within the annotation...so we define a string constant in the annotation
        // which finally here will be translated...
        String messageID = failedRule.getFailureMessage();
        String translatedMessage = getString(ResourceUtility.getId(messageID, R.string.class));

        if (failedView instanceof EditText)
        {
            failedView.requestFocus();
            ((EditText) failedView).setError(translatedMessage);
        }
        else
        {
            Toast.makeText(this, translatedMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------------------------------------------------------------
    // parse authentication listener...
    // ------------------------------------------------------------------------

    @Override
    public void handleSuccessfulSignup()
    {
        Ln.d("Signed up successfully!");

        // reset label...
        tvSignupButtonLabel.setText(getString(R.string.but_register));

        // display loading indicator...
        progressBar.setVisibility(View.GONE);

        Intent mainActivityIntent = new Intent(this,RidesActivity.class);
        startActivity(mainActivityIntent);

        finish();
    }


    @Override
    public void handleSignupError(Exception ex)
    {
        Ln.e(ex,"Authentication failed...");

        // reset label...
        tvSignupButtonLabel.setText(getString(R.string.but_register));

        // display loading indicator...
        progressBar.setVisibility(View.GONE);

        Intent errorIntent = new Intent(this,ErrorActivity.class);
        startActivity(errorIntent);

        finish();
    }
}
