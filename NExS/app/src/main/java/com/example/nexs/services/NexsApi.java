package com.example.nexs.services;

import com.example.nexs.models.Article;
import com.example.nexs.models.ArticleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @POST("/article")
    Call<ArticleResponse> operateArticle(@Query("func") String func,
                                         @Body Article article,
                                         @Query("id") String id,
                                         @Query("category") String category);
}
