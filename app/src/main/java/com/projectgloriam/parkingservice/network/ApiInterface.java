package com.projectgloriam.parkingservice.network;

import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import com.projectgloriam.parkingservice.model.APIResponse;
import com.projectgloriam.parkingservice.model.Ticket;
import com.projectgloriam.parkingservice.model.User;

public interface ApiInterface {

    //Ticket
    @GET("/ticket.php")
    Observable<Ticket> getTicket(@Query("userid") Integer user_id);

    @POST("/tickets.php")
    @FormUrlEncoded
    Observable<Ticket> saveTicket(
                          @Field("userid") Integer user_id,
                            @Field("parkid") Integer park_id);

    //User
    @POST("/users.php")
    @FormUrlEncoded
    Observable<User> saveUser(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone") String phone);

    @POST("/auth.php")
    @FormUrlEncoded
    Observable<User> authUser(@Field("name") String email,
                                @Field("pass") String password);

    @PATCH("/user/edit.php")
    @FormUrlEncoded
    Observable<User> editUser(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("id") String id);

    @GET("/user/delete.php")
    Observable<User> deleteUser(@Query("userid") Integer user_id);
}
