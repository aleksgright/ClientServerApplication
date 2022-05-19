package moviesApp.utils.dtos;

import moviesApp.entities.Movie;

public class CommandRequestDto extends RequestDto {
    private String argument = null;
    private Movie movie = null;

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public String getArgument() {
        return argument;
    }
}
