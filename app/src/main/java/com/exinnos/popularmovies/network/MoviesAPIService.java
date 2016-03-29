package com.exinnos.popularmovies.network;

import com.exinnos.popularmovies.data.MovieDetails;
import com.exinnos.popularmovies.data.MoviesData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by RAMPRASAD on 2/27/2016.
 * Interface for Movies API service.
 */
public interface MoviesAPIService {

    //@GET("movie")
    //Call<MoviesData> fetchMoviesData(@Query("sort_by") String sortBy, @Query("api_key") String apiKey);


    // http://api.themoviedb.org/3/movie/top_rated?api_key=###
    @GET("popular")
    Call<MoviesData> fetchPopularMoviesData(@Query("api_key") String apiKey);

    // http://api.themoviedb.org/3/movie/popular?api_key=###
    @GET("top_rated")
    Call<MoviesData> fetchHighestRatedMoviesData(@Query("api_key") String apiKey);


    @GET("movie/{movieId}")
    Call<MovieDetails> fetchMoviesDetails(@Path("movieId") int movieId, @Query("api_key") String apiKey);

}
