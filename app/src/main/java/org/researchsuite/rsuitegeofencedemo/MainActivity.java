package org.researchsuite.rsuitegeofencedemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.skin.DataResponse;

import java.util.Date;

import edu.cornell.tech.foundry.ohmageomhsdk.OhmageOMHManager;
import edu.cornell.tech.foundry.ohmageomhsdkrs.CTFLogInStep;
import edu.cornell.tech.foundry.ohmageomhsdkrs.CTFLogInStepLayout;
import edu.cornell.tech.foundry.ohmageomhsdkrs.CTFOhmageLogInStepLayout;
import rx.SingleSubscriber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_CODE_SIGN_IN  = 31473;

    public static final String LOG_IN_STEP_IDENTIFIER = "login step identifier";
    public static final String LOG_IN_TASK_IDENTIFIER = "login task identifier";

    private AppCompatButton mLogInButton;


    private RSuiteGeofenceManager.PendingGeofenceTask mPendingGeofenceTask = RSuiteGeofenceManager.PendingGeofenceTask.NONE;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

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
        if(requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

            Boolean isLoggedIn = (Boolean) result.getStepResult(MainActivity.LOG_IN_STEP_IDENTIFIER)
                    .getResultForIdentifier(CTFLogInStepLayout.LoggedInResultIdentifier);

            if (isLoggedIn) {
                startMonitoringGeofences();
            }

            this.updateUI();

        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void signOut() {
        LogExt.d(getClass(), "Signing Out");

        stopMonitoringGeofences();

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

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
    }

    private void startMonitoringGeofences() {
        if (!checkPermissions()) {
            mPendingGeofenceTask = RSuiteGeofenceManager.PendingGeofenceTask.ADD;
            requestPermissions();
        } else {
            RSuiteGeofenceManager.getInstance().startMonitoringGeofences(this);
        }
    }

    private void stopMonitoringGeofences() {
        if (!checkPermissions()) {
            mPendingGeofenceTask = RSuiteGeofenceManager.PendingGeofenceTask.REMOVE;
            requestPermissions();
        } else {
            RSuiteGeofenceManager.getInstance().stopMonitoringGeofences(this);
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == RSuiteGeofenceManager.PendingGeofenceTask.ADD) {
            RSuiteGeofenceManager.getInstance().startMonitoringGeofences(this);
        } else if (mPendingGeofenceTask == RSuiteGeofenceManager.PendingGeofenceTask.REMOVE) {
            RSuiteGeofenceManager.getInstance().stopMonitoringGeofences(this);
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        Log.i(TAG, "Requesting permission");
        // Request permission. It's possible this can be auto answered if device policy
        // sets the permission in a given state or the user denied the permission
        // previously and checked "Never ask again".
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                performPendingGeofenceTask();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = RSuiteGeofenceManager.PendingGeofenceTask.NONE;
            }
        }
    }
}
