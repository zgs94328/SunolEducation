package com.yangguangyulu.sunoleducation.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class Jsons {
    public static final Gson GSON = new GsonBuilder().create();

    @Nullable
    public static String toJson(@NonNull Object bean) {
        try {
            return GSON.toJson(bean);
        } catch (Exception e) {
            Log.e(e.toString(), "Jsons.toJson ex, bean=" + bean);
        }
        return null;
    }

    @Nullable
    public static <T> T fromJson(@NonNull String json, @NonNull Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            Log.e(e.toString(), "Jsons.fromJson ex, json=" + json + ", clazz=" + clazz);
        }
        return null;
    }

    @Nullable
    public static <T> T fromJson(@NonNull String json, @NonNull Class<T> clazz, boolean log) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            if(log) {
                Log.e(e.toString(), "Jsons.fromJson ex, json=" + json + ", clazz=" + clazz);
            }
        }
        return null;
    }

    @Nullable
    public static <T> T fromJson(@NonNull String json, @NonNull Type type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            Log.e(e.toString(), "Jsons.fromJson ex, json=" + json + ", type=" + type);
        }
        return null;
    }

    public static boolean mayJson(String json) {
        if (Strings.isBlank(json)) {
            return false;
        }
        if (json.charAt(0) == '{' && json.charAt(json.length() - 1) == '}') {
            return true;
        }
        if (json.charAt(0) == '[' && json.charAt(json.length() - 1) == ']') {
            return true;
        }
        return false;
    }

    public static String toJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(64 * map.size());
        sb.append('{');
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        if (it.hasNext()) {
            append(it.next(), sb);
        }
        while (it.hasNext()) {
            sb.append(',');
            append(it.next(), sb);
        }
        sb.append('}');
        return sb.toString();
    }

    private static void append(Map.Entry<String, String> entry, StringBuilder sb) {
        String key = entry.getKey(), value = entry.getValue();
        if (value == null) {
            value = Strings.EMPTY;
        }
        sb.append('"').append(key).append('"');
        sb.append(':');
        sb.append('"').append(value).append('"');
    }

    public static String toJson(String key, Collection collection, Type type){
        try {
            JsonElement element = GSON.toJsonTree(collection, type);
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(key, element);
            return jsonObject.toString();
        } catch (Exception e) {
            Log.e(e.toString(), "Jsons.toJson ex, bean=" + collection);
        }
        return null;
    }
}
