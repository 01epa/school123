package com.journal.nn.school123.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.content.Context.MODE_PRIVATE;

public class IntentHelper {
    private static final String DATA_REPOSITORY = "data_repository";

    private static void add(@NonNull Context context,
                            @NonNull Class clazz,
                            @NonNull Object object) {
        put(context, clazz.getName(), object);
    }

    @Nullable
    private static <T> Collection<T> getCollection(@NonNull Context context,
                                                   @NonNull Class<T> clazz) {
        return (Collection<T>) get(context, clazz.getName());
    }

    @Nullable
    private static <K, V> Map<K, V> getMap(@NonNull Context context,
                                           @NonNull Class<V> clazz) {
        return (Map<K, V>) get(context, clazz.getName());
    }

    @Nullable
    public static Data getData(@NonNull Context context,
                               @NonNull String userId) {
        Map<Object, Data> datas = getMap(context, Data.class);
        if (datas == null) {
            datas = new ConcurrentHashMap<>();
            add(context, Data.class, datas);
        }
        return datas.get(userId);
    }

    public static void clearData(@NonNull Context context,
                                 @NonNull String userId) {
        Data data = getData(context, userId);
        data.clear();
        Map<Object, Data> datas = getMap(context, Data.class);
        datas.put(userId, data);
        add(context, Data.class, datas);
    }

    public static void setData(@NonNull Context context,
                               @NonNull Data data) {
        Data oldData = getData(context, data.getId());
        if (oldData != null) {
            data.update(oldData);
        }
        Map<Object, Data> datas = getMap(context, Data.class);
        datas.put(data.getId(), data);
        add(context, Data.class, datas);
    }

    public static void setSchools(@NonNull Context context,
                                  @NonNull Collection<School> schools) {
        add(context, School.class, schools);
    }

    @NonNull
    public static Collection<School> getSchools(@NonNull Context context) {
        Collection<School> schools = getCollection(context, School.class);
        if (schools == null) {
            schools = new CopyOnWriteArrayList<>();
            add(context, School.class, schools);
        }
        return schools;
    }

    private static void put(@NonNull Context context,
                            @NonNull String key,
                            @NonNull Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_REPOSITORY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(arrayOutputStream);
            objectOutput.writeObject(object);
            byte[] data = arrayOutputStream.toByteArray();
            objectOutput.close();
            arrayOutputStream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
            b64.write(data);
            b64.close();
            out.close();

            editor.putString(key, new String(out.toByteArray()));
            editor.apply();
        } catch (IOException e) {
            System.out.println("Could not set value for key=" + key + ". Error=" + e);
        }
    }

    @Nullable
    private static <T> T get(@NonNull Context context,
                             @NonNull String key) {
        T object = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_REPOSITORY, MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        if (value != null && value.getBytes().length != 0) {
            try {
                ByteArrayInputStream byteArray = new ByteArrayInputStream(value.getBytes());
                Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
                ObjectInputStream in = new ObjectInputStream(base64InputStream);
                object = (T) in.readObject();
                byteArray.close();
                base64InputStream.close();
            } catch (Exception e) {
                System.out.println("Could not get value for key=" + key + ". Error=" + e);
            }
        }
        return object;
    }

    public static void clear(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_REPOSITORY, MODE_PRIVATE);
        sharedPreferences.edit()
                .clear()
                .apply();
    }
}