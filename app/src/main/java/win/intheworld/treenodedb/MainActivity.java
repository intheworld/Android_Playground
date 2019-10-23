package win.intheworld.treenodedb;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import AIDL.client.ClientActivity;
import animation_tutorial.flow_layout.FlowLayoutActivity;
import win.intheworld.treenodedb.data.Bean;
import win.intheworld.treenodedb.data.Plate;
import win.intheworld.treenodedb.data.Node;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.interpolator_anim);
        final TextView textView = (TextView) findViewById(R.id.tv_anim);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.startAnimation(animation);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //testNavigateTreeFromLeaf();
        //testInsertTree();
        findViewById(R.id.btn_clear_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btn_add_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "test insert plate");
            }
        });

        findViewById(R.id.btn_test_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        testOrdinaryJsonApi();
                        testGzipJsonApi();
                        testGzipGsonStreamApi();
                        Bean.test();
                    }
                }.start();
            }
        });

        findViewById(R.id.btn_play_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miuiMusicLocalIntent();
            }
        });

        findViewById(R.id.btn_play_fav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miuiMusicFavIntent();
            }
        });

        findViewById(R.id.tv_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FlowLayoutActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addForegroundParams(Intent intent) {
        int targetSdkVersion = getTargetSdkVersion(this, "com.miui.player");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && targetSdkVersion >= Build.VERSION_CODES.O) {
            intent.putExtra("is_started_by_foreground", true);
        }
    }

    public static int getTargetSdkVersion(Context context, String packageName) {
        int version = 0;
        try {
            if (!TextUtils.isEmpty(packageName)) {
                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
                if (applicationInfo != null) {
                    version = applicationInfo.targetSdkVersion;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            version = -1;
        }
        return version;
    }

    private void testOrdinaryJsonApi() {
        long startTime = System.currentTimeMillis();
        try {
            StringBuilder sb = new StringBuilder();
            JsonParser jsonParser = new JsonParser();
            InputStream in = getAssets().open("mobileApp.entities");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                JsonObject itemJS = jsonParser.parse(line).getAsJsonObject();
                String slot = itemJS.get("slot").getAsString();
                if (!"name".equals(slot)) {
                    sb.append(line).append("\n");
                }
            }
            new ByteArrayInputStream(sb.toString().getBytes());
        } catch (IOException e) {
            Log.e(TAG, "error", e);
        }
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "ordinary time = " + (endTime - startTime));
    }

    private void testGzipJsonApi() {
        long startTime = System.currentTimeMillis();
        try {
            StringBuilder sb = new StringBuilder();
            //JsonParser jsonParser = new JsonParser();
            InputStream in = new GZIPInputStream(getAssets().open("mobileApp.entities.v2"));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                JSONObject itemJS = new JSONObject(line);
                String slot = itemJS.optString("slot");
                if (!"name".equals(slot)) {
                    sb.append(line).append("\n");
                }
            }
            new ByteArrayInputStream(sb.toString().getBytes());
            Log.d(TAG, "length = " + sb.toString().getBytes().length);
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "Gzip time = " + (endTime - startTime));
    }

    private void testGzipGsonStreamApi() {
        long startTime = System.currentTimeMillis();
        try {
            StringBuilder sb = new StringBuilder();
            InputStream in = new GZIPInputStream(getAssets().open("mobileApp.entities.v2"));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                JsonReader jsonReader = new JsonReader(new StringReader(line));
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String field = jsonReader.nextName();
                    if ("slot".equals(field)) {
                        if (!"name".equals(jsonReader.nextString())) {
                            sb.append(line).append("\n");
                        }
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            }
            Log.d(TAG, "length = " + sb.toString().getBytes().length);
            new ByteArrayInputStream(sb.toString().getBytes());
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "Gzip stream time = " + (endTime - startTime));
    }

    private Node createTreeInMemory() {
        Node root = new Node();
        root.setName("root");

        Node child1 = new Node();
        child1.setName("child1");
        root.getSubNodes().add(child1);

        Node child2 = new Node();
        child2.setName("child2");
        root.getSubNodes().add(child2);

        Node child1Child = new Node();
        child1Child.setName("child1child");
        child1.getSubNodes().add(child1Child);
        return root;
    }

    public void miuiMusicLocalIntent() {
        Intent localIntent= new Intent();
        localIntent.setPackage("com.miui.player");
        localIntent.setAction("com.miui.player.play_music");
        Uri uri = new Uri.Builder()
                .scheme("miui-music")
                .authority("play_music")
                .path("local")
                .appendQueryParameter("miref", "com.miui.voiceassist")//用于区分来源，使用包名
                .build();
        localIntent.setData(uri);
        localIntent.putExtra("is_started_by_foreground", true);
        startCompatibleServiceSafely(localIntent);
        Log.d(TAG, "local intent = " + localIntent.toUri(Intent.URI_INTENT_SCHEME));
    }

    public void miuiMusicFavIntent() {
        Intent favoritesIntent= new Intent();
        favoritesIntent.setPackage("com.miui.player");
        favoritesIntent.setAction("com.miui.player.play_music");
        Uri uri = new Uri.Builder()
                .scheme("miui-music")
                .authority("play_music")
                .path("favorites")
                .appendQueryParameter("miref", "com.miui.voiceassist")//用于区分来源，使用包名
                .build();
        favoritesIntent.setData(uri);
        favoritesIntent.putExtra("is_started_by_foreground", true);
        startCompatibleServiceSafely(favoritesIntent);
        Log.d(TAG, "fav intent = " + favoritesIntent.toUri(Intent.URI_INTENT_SCHEME));
    }

    public void startCompatibleServiceSafely(Intent intent) {
        if (intent != null) {
            try {
                Context context = this;
                PackageManager pm = context.getPackageManager();
                List<ResolveInfo> infos = pm.queryIntentServices(intent, 0);
                if (infos == null || infos.size() <= 0) {

                } else {
                    int targetSdkVersion = -1;
                    ServiceInfo serviceInfo = infos.get(0).serviceInfo;
                    if (serviceInfo != null && serviceInfo.applicationInfo != null) {
                        targetSdkVersion = serviceInfo.applicationInfo.targetSdkVersion;
                    }
                    Log.d(TAG, "targetSdkVersion = " + targetSdkVersion);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && targetSdkVersion >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    } else {
                        context.startService(intent);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "startServiceSafely", e);
            }
        }
    }

    public class MiuiMusicUtil {

        private static final String TAG = "MiuiMusicUtil";

        public static final String BROADCAST_PREFIX = "com.miui.player";

        public static final String ACTION_CHANGE_MODE = BROADCAST_PREFIX + ".musicservicecommand.change_mode";
        public static final String ACTION_RESPONSE_CHANGE_MODE = BROADCAST_PREFIX + ".responsechangemode";
        public static final String ACTION_PLAY_MUSIC = BROADCAST_PREFIX + ".play_music";
        public static final String ACTION_SET_FAV = BROADCAST_PREFIX + ".set_fav";
        public static final String AUTHORITY_PLAY_MUSIC = "play_music";

        public static final String SCHEME = "miui-music";

        public static final String PARAM_REF = "miref";

        public static final String PATH_LOCAL = "local";

        public static final String PATH_FAV = "favorites";

        public static final String PATH_MUSIC = "music";

        public static final String PARAM_SONG_IDS = "songIds";

        public static final String REQUEST_ID = "request_id";

        public static final String CP = "cp";

        public static final String DOMAIN = "domain";
    }
}
