package com.sobag.parsetemplate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.sobag.parsetemplate.enums.FontApplicableComponent;
import com.sobag.parsetemplate.services.LoginListener;
import com.sobag.parsetemplate.services.ParseLoginService;
import com.sobag.parsetemplate.util.FontUtility;
import com.sobag.parsetemplate.util.ResourceUtility;

import javax.annotation.Nullable;

import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

@ContextSingleton
public class LoginActivity extends CommonHeadlessActivity
    implements LoginListener, Validator.ValidationListener
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Validator validator;

    @Inject
    ParseLoginService parseLoginService;

    @Inject
    FontUtility fontUtility;

    // UI components...

    @Nullable
    @InjectView(tag = "progressBar")
    ProgressBar progressBar;

    @InjectView(tag = "tv_title")
    TextView tvTitle;

    @InjectView(tag = "tv_or")
    TextView tvOr;

    @Required(order = 1, message = "msg_requiredField")
    @InjectView(tag = "et_username")
    EditText etUsername;

    @Required(order = 2, message = "msg_requiredField")
    @InjectView(tag = "et_password")
    EditText etPassword;

    @InjectView(tag = "tv_fb_button_label")
    TextView tvFbButtonLabel;

    @InjectView(tag = "tv_login_button_label")
    TextView tvLoginButtonLabel;

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // apply font...
        fontUtility.applyFontToComponent(tvTitle,R.string.button_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvFbButtonLabel,R.string.button_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(tvOr,R.string.default_font,
                FontApplicableComponent.TEXT_VIEW);
        fontUtility.applyFontToComponent(etUsername,R.string.button_font,
                FontApplicableComponent.EDIT_TEXT);
        fontUtility.applyFontToComponent(etPassword,R.string.button_font,
                FontApplicableComponent.EDIT_TEXT);
        fontUtility.applyFontToComponent(tvLoginButtonLabel,R.string.button_font,
                FontApplicableComponent.TEXT_VIEW);

        // init validator....
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    public void onFacebookLogin(View view)
    {
        // display loading indicator...
        progressBar.setVisibility(View.VISIBLE);

        parseLoginService.loginOrSignupWithFacebook(this);
    }

    public void onLogin(View view)
    {
        // apply text to button...
        validator.validate();
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
        tvLoginButtonLabel.setText(getString(R.string.but_login_loading));

        parseLoginService.login(this, etUsername.getText().toString(),
                etPassword.getText().toString());
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
    // parse authentication implementation
    // ------------------------------------------------------------------------

    @Override
    public void handleSuccessfulLogin()
    {
        Ln.d("Logged in successfully!");

        // display default label...
        tvLoginButtonLabel.setText(getString(R.string.but_login));

        // display loading indicator...
        progressBar.setVisibility(View.GONE);

        Intent mainActivityIntent = new Intent(this,RidesActivity.class);
        startActivity(mainActivityIntent);

        finish();
    }

    @Override
    public void handleLoginError(Exception ex)
    {
        Ln.e(ex);

        tvLoginButtonLabel.setText(getString(R.string.but_login));

        // display error...
        Toast.makeText(this,getString(R.string.msg_loginfailed),Toast.LENGTH_LONG).show();
    }
}
