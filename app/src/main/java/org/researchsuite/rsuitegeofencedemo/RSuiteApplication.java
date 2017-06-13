package org.researchsuite.rsuitegeofencedemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.backbone.storage.file.UnencryptedProvider;
import org.researchstack.skin.DataResponse;

import edu.cornell.tech.foundry.ohmageomhbackend.ORBEOhmageResultBackEnd;
import edu.cornell.tech.foundry.ohmageomhsdk.OhmageOMHManager;
import edu.cornell.tech.foundry.researchsuiteresultprocessor.RSRPResultsProcessor;
import rx.Single;
import rx.SingleSubscriber;

/**
 * Created by jameskizer on 6/13/17.
 */

public class RSuiteApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.initializeSingletons(this);
    }

    public void initializeSingletons(Context context) {

        //TODO: Change to pin encrypted
        UnencryptedProvider encryptionProvider = new UnencryptedProvider();

        AppDatabase dbAccess = createAppDatabaseImplementation(context);
        dbAccess.setEncryptionKey(encryptionProvider.getEncrypter().getDbKey());

        RSuiteFileAccess fileAccess = createFileAccessImplementation(context);
        fileAccess.setEncrypter(encryptionProvider.getEncrypter());

        //storageAccess
        //make unencrypted for now!!
        StorageAccess.getInstance().init(
                null,
                new UnencryptedProvider(),
                fileAccess,
                createAppDatabaseImplementation(context)
        );

        //config ohmage manager singleton
        //requires OhmageOMHSDKCredentialStore
        //TODO:
        OhmageOMHManager.config(
                context,
                getString(R.string.omh_base_url),
                getString(R.string.omh_client_id),
                getString(R.string.omh_client_secret),
                fileAccess,
                getString(R.string.ohmage_queue_directory)
        );

//        YADLResourcePathManager resourcePathManager = new YADLResourcePathManager();
//        ResourcePathManager.init(resourcePathManager);
//        //config task builder singleton
//        //task builder requires ResourceManager, ImpulsivityAppStateManager
//        YADLTaskBuilderManager.init(context, resourcePathManager, fileAccess);
//
//        //config results processor singleton
//        //requires RSRPBackend
//        YADLResultsProcessorManager.init(ORBEOhmageResultBackEnd.getInstance());
//        RSRPResultsProcessor resultsProcessor = new RSRPResultsProcessor(ORBEOhmageResultBackEnd.getInstance());

    }

    public void resetSingletons(Context context) {

        //remove passcode
        //delete all files
        //delete encrypted db

        this.initializeSingletons(context);

    }


//    protected PinCodeConfig getPinCodeConfig(Context context)
//    {
//        return new PinCodeConfig();
//    }
//
//    protected EncryptionProvider getEncryptionProvider(Context context)
//    {
//        return new AesProvider();
//    }


    protected RSuiteFileAccess createFileAccessImplementation(Context context)
    {
        String pathName = "/RSuite";
        return new RSuiteFileAccess(pathName);
    }

    protected AppDatabase createAppDatabaseImplementation(Context context) {
        SQLiteDatabase.loadLibs(context);

        return new SqlCipherDatabaseHelper(
                context,
                SqlCipherDatabaseHelper.DEFAULT_NAME,
                null,
                SqlCipherDatabaseHelper.DEFAULT_VERSION,
                new UpdatablePassphraseProvider()
        );
    }

    public Single<DataResponse> signOut(Context context) {

        return Single.create(new Single.OnSubscribe<DataResponse>() {
            @Override
            public void call(final SingleSubscriber<? super DataResponse> singleSubscriber) {
                OhmageOMHManager.getInstance().signOut(new OhmageOMHManager.Completion() {
                    @Override
                    public void onCompletion(Exception e) {

                        RSuiteFileAccess.getInstance().clearFileAccess(RSuiteApplication.this);

//                        YADLFullCompletedDate.clearFullAssessmentCompletedDate(YADLApplication.this);
//
//                        //clear notification, too!!
//                        NotificationTime notificationTime = new NotificationTime(YADLApplication.this);
//                        notificationTime.setNotificationTime(null, null);

                        if (e != null) {
                            singleSubscriber.onError(e);
                        }
                        else {
                            singleSubscriber.onSuccess(new DataResponse(true, "success"));
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void attachBaseContext(Context base)
    {
        // This is needed for android versions < 5.0 or you can extend MultiDexApplication
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
