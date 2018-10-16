package com.codefuelindia.dnote.Common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static HttpLoggingInterceptor httpLoggingInterceptor;


    public static Retrofit getClient(String baseUrl) {

        httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (retrofit == null) {
            final OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS);

            okHttpClient.addInterceptor(httpLoggingInterceptor);

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;

    }


}
