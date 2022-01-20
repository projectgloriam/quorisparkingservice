package com.projectgloriam.parkingservice.network;

import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import com.projectgloriam.parkingservice.model.APIResponse;
import com.projectgloriam.parkingservice.model.Ticket;

public interface ApiInterface {

    @GET("/ticket.php")
    Observable<Ticket> getTicket(@Query("userid") Integer user_id);

    @POST("/tickets.php")
    @FormUrlEncoded
    Observable<Ticket> saveTicket(
                          @Field("userid") Integer user_id,
                            @Field("parkid") Integer park_id);
}
