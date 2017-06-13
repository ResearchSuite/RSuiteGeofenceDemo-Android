package org.researchsuite.rsuitegeofencedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.skin.DataResponse;

import edu.cornell.tech.foundry.ohmageomhsdk.OhmageOMHManager;
import edu.cornell.tech.foundry.ohmageomhsdkrs.CTFLogInStep;
import edu.cornell.tech.foundry.ohmageomhsdkrs.CTFOhmageLogInStepLayout;
import rx.SingleSubscriber;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SIGN_IN  = 31473;

    public static final String LOG_IN_STEP_IDENTIFIER = "login step identifier";
    public static final String LOG_IN_TASK_IDENTIFIER = "login task identifier";

    private AppCompatButton mLogInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mLogInButton = (AppCompatButton) findViewById(R.id.log_in_button);
        this.updateUI();
    }

    private void updateUI() {
        if(OhmageOMHManager.getInstance().isSignedIn()) {
            this.mLogInButton.setText(R.string.log_out_button_text);
            this.mLogInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signOut();
                }
            });
        }
        else {
            this.mLogInButton.setText(R.string.log_in_button_text);
            this.mLogInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLogInStep();
                }
            });
        }

    }

    private void showLogInStep() {
        CTFLogInStep logInStep = new CTFLogInStep(
                MainActivity.LOG_IN_STEP_IDENTIFIER,
                "Sign In",
                "Please enter your Ohmage-OMH credentials to sign in.",
                CTFOhmageLogInStepLayout.class
        );

        OrderedTask task = new OrderedTask(MainActivity.LOG_IN_TASK_IDENTIFIER, logInStep);
        startActivityForResult(ViewTaskActivity.newIntent(this, task),
                REQUEST_CODE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        this.updateUI();
    }

    public void signOut() {
        LogExt.d(getClass(), "Signing Out");

        RSuiteApplication app = (RSuiteApplication)getApplication();
        app.signOut(this).subscribe(new SingleSubscriber<DataResponse>() {
            @Override
            public void onSuccess(DataResponse value) {
                updateUI();
            }

            @Override
            public void onError(Throwable error) {
                updateUI();
            }
        });

    }
}
