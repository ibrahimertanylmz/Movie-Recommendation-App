package org.tensorflow.lite.examples.recommendation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("/3/movie/{category}") //cat: popular
    Call<MovieResults> listOfMovies(
            @Path("category") String category,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/movie/{id}") //cat: popular
    Call<MovieData> getMovie(
            @Path("id") Integer id,
            @Query("api_key") String apiKey
    );
}

