package AIDL;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import win.intheworld.treenodedb.IRemoteAidlInterface;


public class RemoteService extends Service {

    private static final String TAG = "RemoteService";

    private Person mPerson;
    private String mUserName;
    private String mUserAge;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind Service success!");
        mPerson = new Person("Edward", "24");
        return new RemoteBinder();
    }

    class RemoteBinder extends IRemoteAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String getPersonUserName() throws RemoteException {
            mUserName = mPerson.getmUserName();
            Log.d(TAG, "Person mUserName = " + mUserName);
            return mUserName;
        }

        @Override
        public String getPersonUserAge() throws RemoteException {
            mUserAge = mPerson.getmUserAge();
            Log.d(TAG, "Person mUserAge = " + mUserAge);
            return mUserAge;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
