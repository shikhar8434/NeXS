package com.example.nexs.services;

import androidx.annotation.NonNull;

import com.example.nexs.models.Article;
import com.example.nexs.models.ArticleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface NexsApi {

    //ARTICLE FUNCTIONS
    public static final String GET_ALL = "getAll";
    public static final String GET_BY_ID = "getOne";
    public static final String GET_BY_CATEGORY = "getByCategory";
    public static final String DELETE_BY_ID = "deleteOne";
    public static final String UPDATE_BY_ID = "updateOne";
    public static final String LIKE_BY_ID = "like";
    public static final String NEW_POST = "newPost";

    @GET("/?resource=article&func=" + GET_ALL)
    Call<ArticleResponse> articleGetAll();

    @GET("/?resource=article&func=" + GET_BY_ID)
    Call<ArticleResponse> articleGetById(@Query("id") @NonNull String id);

    @GET("/?resource=article&func=" + GET_BY_CATEGORY)
    Call<ArticleResponse> articleGetByCategory(@Query("category") @NonNull String category);

    @DELETE("/?resource=article&func=" + DELETE_BY_ID)
    Call<ArticleResponse> articleDeleteById(@Query("id") @NonNull String id);

    @PUT("/?resource=article&func=" + UPDATE_BY_ID)
    Call<ArticleResponse> articleUpdateById(@Query("id") @NonNull String id, @Body @NonNull Article article);

    @POST("/?resource=article&func=" + NEW_POST)
    Call<ArticleResponse> articleNewPost(@Body @NonNull Article article);

    @PUT("/?resource=article&func=" + LIKE_BY_ID)
    Call<ArticleResponse> articleLikeById(@Query("id") @NonNull String id);
}
