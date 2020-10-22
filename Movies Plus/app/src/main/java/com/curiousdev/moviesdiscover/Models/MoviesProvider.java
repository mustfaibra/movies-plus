package com.curiousdev.moviesdiscover.Models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesProvider {

    @GET("movie/popular")
    Call<Movies> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );
    @GET("movie/upcoming")
    Call<Movies> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );
    @GET("movie/now_playing")
    Call<Movies> getNewMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );

    @GET("tv/popular")
    Call<Series> getPopularTvShow(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );
    @GET("tv/top_rated")
    Call<Series> getTopRatedTvShow(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );
    @GET("tv/on_the_air")
    Call<Series> getOnAirTvShow(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int pageIndex
    );

    //getting all genres
    @GET("genre/movie/list")
    Call<Genre> getGenres(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );
    @GET("discover/movie")
    Call<Movies> getRecommendations(
            @Query("api_key") String apiKey,
            @Query("with_original_language") String original_language,
            @Query("with_genres") String genres,
            @Query("include_adult") boolean includeAdult,
            @Query("year") int year,
            @Query("vote_average.gte") int rate,
            @Query("language") String language,
            @Query("page") int pageIndex
    );
    @GET("configuration/languages")
    Call<List<Language>> getLanguages(
            @Query("api_key") String apiKey
    );
    @GET("search/tv")
    Call<Series> getSeriesSearchResults(
            @Query("api_key") String apiKey,
            @Query("query") String showName,
            @Query("include_adult") boolean includeAdult,
            @Query("language") String language,
            @Query("page") int pageIndex
    );
    @GET("search/movie")
    Call<Movies> getMoviesSearchResults(
            @Query("api_key") String apiKey,
            @Query("query") String movieName,
            @Query("include_adult") boolean includeAdult,
            @Query("year") int year,
            @Query("language") String language,
            @Query("page") int pageIndex
    );
    @GET("search/person")
    Call<People> getActorsSearchResults(
            @Query("api_key") String apiKey,
            @Query("query") String actorName,
            @Query("include_adult") boolean includeAdult,
            @Query("language") String language,
            @Query("page") int pageIndex
    );
    @GET("tv/{show_id}")
    Call<TvShowDetails> getTvShowDetail(
            @Path("show_id") int showId,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("append_to_response") String appended
    );
    @GET("tv/{show_id}/recommendations")
    Call<Series> getSimilarTvShows(
            @Path("show_id") int showId,
            @Query("page") int currentPage,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );
    @GET("tv/{show_id}/videos")
    Call<Videos> getTvShowVideos(
            @Path("show_id") int showId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/{show_id}/reviews")
    Call<Reviews> getTvshowReviews(
            @Path("show_id") int showId,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );
    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("append_to_response") String appended
    );
    @GET("movie/{movie_id}/recommendations")
    Call<Movies> getSimilarMovies(
            @Path("movie_id") int movieId,
            @Query("page") int currentPage,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );
    @GET("movie/{movie_id}/videos")
    Call<Videos> getMovieVideos(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/reviews")
    Call<Reviews> getReviews(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );
    @GET("person/{person_id}")
    Call<Person> getPersonDetail(
            @Path("person_id") int personId,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("append_to_response") String append
    );

}
