package com.exinnos.popularmovies.data;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RAMPRASAD on 4/3/2016.
 */
public class MovieTrailersData {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("results")
    @Expose
    private List<MovieTrailer> results = new ArrayList<MovieTrailer>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    public List<MovieTrailer> getMovieTrailers() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setMovieTrailers(List<MovieTrailer> results) {
        this.results = results;
    }


}
