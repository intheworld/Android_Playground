package win.intheworld.treenodedb.data;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swliu on 19-1-9.
 * liushuwei@xiaomi.com
 */

public class Bean {
    private static final String TAG = "Bean";

    public String key;
    public String title;
    public List<String> values;
    public String defaultValue;

    public static Bean fromJsonString(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Bean bean = new Bean();
            bean.key = jsonObject.optString("key");
            bean.title = jsonObject.optString("title");
            JSONArray jsonArray = jsonObject.optJSONArray("values");
            if (jsonArray != null && jsonArray.length() > 0) {
                int len = jsonArray.length();
                bean.values = new ArrayList<>(len);
                for (int i=0; i<len; ++i) {
                    bean.values.add(jsonArray.getString(i));
                }
            }
            bean.defaultValue = jsonObject.optString("defaultValue");

            return bean;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bean fromJsonStringUsingStream(String json) {
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        Bean bean = new Bean();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String field = jsonReader.nextName();
                if ("key".equals(field)) {
                    bean.key = jsonReader.nextString();
                } else if ("title".equals(field)) {
                    bean.title = jsonReader.nextString();
                } else if ("defaultValue".equals(field)) {
                    bean.defaultValue = jsonReader.nextString();
                } else if ("values".equals(field)) {
                    jsonReader.beginArray();
                    List<String> valueList = new ArrayList<>();
                    while(jsonReader.hasNext()){
                        valueList.add(jsonReader.nextString());
                    }
                    bean.values = valueList;
                    jsonReader.endArray();
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "error = " + e);
        }
        return bean;
    }

    public static String toJsonString(Bean bean) {
        if (bean == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", bean.key);
            jsonObject.put("title", bean.title);
            if (bean.values != null) {
                JSONArray array = new JSONArray();
                for (String str:bean.values) {
                    array.put(str);
                }
                jsonObject.put("values", array);
            }
            jsonObject.put("defaultValue", bean.defaultValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static void test() {
        String a = "{\"key\":\"123\", \"title\":\"asd\", \"values\":[\"a\", \"b\", \"c\", \"d\"], \"defaultValue\":\"a\"}";

        Gson Gson = new Gson();
        Bean testBean = Gson.fromJson(a, new TypeToken<Bean>(){}.getType());
        Type type = new TypeToken<Bean>(){}.getType();
        Bean testBean1 = Gson.fromJson(a, Bean.class);

        long now = System.currentTimeMillis();
        for (int i=0; i<1000; ++i) {
            Gson.fromJson(a, type);
            //Gson.fromJson(a, Bean.class);
        }
        Log.d(TAG, "Gson parse use time="+(System.currentTimeMillis() - now));

        now = System.currentTimeMillis();
        for (int i=0; i<1000; ++i) {
            Bean.fromJsonString(a);
        }
        Log.d(TAG, "jsonobject parse use time="+(System.currentTimeMillis() - now));

        now = System.currentTimeMillis();
        for (int i=0; i<1000; ++i) {
            Bean.fromJsonStringUsingStream(a);
        }
        Log.d(TAG, "GsonStream parse use time="+(System.currentTimeMillis() - now));

        now = System.currentTimeMillis();
        for (int i=0; i<1000; ++i) {
            Gson.toJson(testBean);
        }
        Log.d(TAG, "Gson tojson use time="+(System.currentTimeMillis() - now));

        now = System.currentTimeMillis();
        for (int i=0; i<1000; ++i) {
            Bean.toJsonString(testBean);
        }
        Log.d(TAG, "jsonobject tojson use time="+(System.currentTimeMillis() - now));
    }
}