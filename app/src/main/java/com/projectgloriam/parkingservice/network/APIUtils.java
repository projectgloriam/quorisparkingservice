package com.projectgloriam.parkingservice.network;

public class APIUtils {
    private APIUtils() {}

    public static final String BASE_URL = "http://quorissolutions.com/";

    public static ApiInterface getAPIService() {
        return ApiClient.getClient(BASE_URL).create(ApiInterface.class);
    }
}
