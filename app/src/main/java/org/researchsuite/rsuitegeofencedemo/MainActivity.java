package org.researchsuite.rsuitegeofencedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton mLogInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mLogInButton = (AppCompatButton) findViewById(R.id.log_in_button);
        this.mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInButtonClicked(view);
            }
        });
        this.updateUI();
    }

    private void updateUI() {
        this.mLogInButton.setText(R.string.log_in_button_text);
    }

    public void logInButtonClicked(View view) {
//        showLogInStep();
        Toast.makeText(this,
                "Log In Clicked",
                Toast.LENGTH_SHORT).show();
    }

//    //pass code MUST be set when we launch this
//    private void showLogInStep() {
//        CTFLogInStep logInStep = new CTFLogInStep(
//                YADLOnboardingActivity.LOG_IN_STEP_IDENTIFIER,
//                "Sign In",
//                "Please enter your Ohmage-OMH credentials to sign in.",
//                CTFOhmageLogInStepLayout.class
//        );
////        logInStep.setForgotPasswordButtonTitle("Skip Log In");
////        logInStep.setOptional(false);
//
//        OrderedTask task = new OrderedTask(YADLOnboardingActivity.LOG_IN_TASK_IDENTIFIER, logInStep);
//        startActivityForResult(ViewTaskActivity.newIntent(this, task),
//                REQUEST_CODE_SIGN_IN);
//    }
}
