package ru.edgar.nlremake.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import ru.edgar.nlremake.model.Api;
import ru.edgar.nlremake.model.Details;
import ru.edgar.nlremake.model.Main;
import ru.edgar.nlremake.model.Server;
import ru.edgar.nlremake.model.Stories;

public interface Interface {

    /*@GET
    Call<edgar> getAuth(@Url String url);*/

    @GET
    Call<Api> getApi(@Url String url);

    @GET
    Call<Main> getMain(@Url String url);

    @GET
    Call<List<Stories>> getStories(@Url String url);

    @POST
    @FormUrlEncoded
    Call<List<Server>> getServers(@Url String url, @Field("uid") String uid);

    @POST
    @FormUrlEncoded
    Call<String> verifyAuth(@Url String url, @Field("user_email") String user_email);

    @POST
    @FormUrlEncoded
    Call<String> resetPassword(@Url String url, @Field("user_email") String user_email, @Field("new_pass") String new_pass);

    @POST
    @FormUrlEncoded
    Call<String> сharacter(@Url String url, @Field("nick") String nick, @Field("sex") String sex, @Field("skin") String skin, @Field("promo") String promo);

    @POST
    @FormUrlEncoded
    Call<List<Details>> getAccountDetails(@Url String url, @Field("name") String name);

    @POST
    @FormUrlEncoded
    Call<String> getIsAcc(@Url String url, @Field("user_name") String user_name);

    @POST
    @FormUrlEncoded
    Call<String> getDeleteAcc(@Url String url, @Field("nick") String name);
}
