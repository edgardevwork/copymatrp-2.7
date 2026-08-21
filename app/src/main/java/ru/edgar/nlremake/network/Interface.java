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
import ru.edgar.nlremake.model.FaqList;
import ru.edgar.nlremake.model.Main;
import ru.edgar.nlremake.model.News;
import ru.edgar.nlremake.model.Servers;
import ru.edgar.nlremake.model.edgar;

public interface Interface {

    @GET
    Call<Api> getApi(@Url String url);

    @GET
    Call<edgar> getAuth(@Url String url);

    @GET
    Call<Main> getMain(@Url String url);

    @GET
    Call<List<Servers>> getServers(@Url String url);

    @GET
    Call<List<News>> getStories(@Url String url);

    @GET
    Call<FaqList> getFaqList(@Url String url);

    @POST
    @FormUrlEncoded
    Call<String> createCharacter(@Url String url, @Field("nick") String nick, @Field("sex") String sex, @Field("skin") String skin, @Field("promo") String promo);

    @POST
    @FormUrlEncoded
    Call<String> authMail(@Url String url, @Field("user_email") String user_email);

    @POST
    @FormUrlEncoded
    Call<String> resetPassword(@Url String url, @Field("user_email") String user_email, @Field("new_pass") String new_pass);

    @POST
    @FormUrlEncoded
    Call<List<Details>> getAccountDetails(@Url String url, @Field("name") String name);

    @POST
    @FormUrlEncoded
    Call<String> getIsAcc(@Url String url, @Field("user_name") String user_name);

}
