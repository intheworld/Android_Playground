package AIDL.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import win.intheworld.treenodedb.IRemoteAidlInterface;
import win.intheworld.treenodedb.R;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String BIND_SERVICE_ACTION = "android.intent.action.RESPOND_AIDL_MESSAGE";

    private IRemoteAidlInterface iRemoteAidlInterface;

    private Button mConnectButton;
    private Button mAcquireButton;

    private String mUserName;
    private String mUserAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        initView();
    }

    private void initView() {
        mConnectButton = (Button) this.findViewById(R.id.btn_connect);
        mAcquireButton = (Button) this.findViewById(R.id.btn_acquire_info);

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindService();
            }
        });

        mAcquireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != iRemoteAidlInterface) {
                    try {
                        mUserName = iRemoteAidlInterface.getPersonUserName();
                        mUserAge = iRemoteAidlInterface.getPersonUserAge();
                        Toast.makeText(getApplicationContext(), "mUserName = " + mUserName + ",mUserAge = " + mUserAge, Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iRemoteAidlInterface = IRemoteAidlInterface.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iRemoteAidlInterface = null;
        }
    };

    private void bindService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(BIND_SERVICE_ACTION);
        Intent exIntent = new Intent(achieveExplicitFromImplicitIntent(this, serviceIntent));
        bindService(exIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService");
    }

    private void unBindService() {
        unbindService(serviceConnection);
        Log.d(TAG, "unbindService");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }

    public Intent achieveExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        Log.d(TAG,"packageName = " + packageName);
        Log.d(TAG,"className = " + className);
        ComponentName component = new ComponentName(packageName, className);

        Intent explicitIntent = new Intent(implicitIntent);

        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
