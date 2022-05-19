package moviesApp.entities;

import moviesApp.utils.enums.MovieGenre;
import moviesApp.utils.enums.MpaaRating;
import moviesApp.utils.Coordinates;

import java.io.Serializable;
import java.util.Date;

public class Movie implements Comparable<Movie>, Serializable {
    private int id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int oscarsCount;
    private MovieGenre genre;
    private MpaaRating mpaaRating;
    private Person director;
    private String ownerToken;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        return getCreationDate().getDate() + "/"
                + (getCreationDate().getMonth() + 1) + "/"
                + (getCreationDate().getYear());
    }

    public int getOscarsCount() {
        return oscarsCount;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public MpaaRating getMpaaRating() {
        return mpaaRating;
    }

    public Person getDirector() {
        return director;
    }

    public String getOwnerToken() {
        return ownerToken;
    }

    public void setOwnerToken(String ownerToken) {
        this.ownerToken = ownerToken;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setDirector(Person director) {
        this.director = director;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public void setMpaaRating(MpaaRating mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public void setOscarsCount(int oscarsCount) {
        this.oscarsCount = oscarsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;

        Movie movie = (Movie) o;

        if (oscarsCount != movie.oscarsCount) return false;
        if (!name.equals(movie.name)) return false;
        if (!coordinates.equals(movie.coordinates)) return false;
        if (!creationDate.equals(movie.creationDate)) return false;
        if (genre != movie.genre) return false;
        if (mpaaRating != movie.mpaaRating) return false;
        return director != null ? director.equals(movie.director) : movie.director == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + coordinates.hashCode();
        result = 31 * result + creationDate.hashCode();
        result = 31 * result + oscarsCount;
        result = 31 * result + genre.hashCode();
        result = 31 * result + mpaaRating.hashCode();
        result = 31 * result + (director != null ? director.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String result = "Name: \"" + getName() + "\"\n";
        result = result + "\tid: " + getId() + "\n";
        result = result + "\tcoordinates: " + getCoordinates() + "\n";
        result = result + "\tcreationDate: " + getCreationDate().getDate() + "/"
                + (getCreationDate().getMonth() + 1) + "/"
                + (getCreationDate().getYear()) + "\n";
        result = result + "\toscarsCount: " + getOscarsCount() + "\n";
        result = result + "\tgenre: " + getGenre() + "\n";
        result = result + "\tmpaaRating: " + getMpaaRating() + "\n";
        result = result + "\tdirector: \"" + getDirector() + "\"\n";
        return result;
    }

    @Override
    public int compareTo(Movie movie) {
        return oscarsCount - movie.getOscarsCount();
    }
}
