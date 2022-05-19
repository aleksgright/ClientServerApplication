package moviesApp.moviesAppServer.services;

import moviesApp.entities.Movie;
import moviesApp.entities.Person;
import moviesApp.utils.enums.Color;
import moviesApp.utils.enums.Country;
import moviesApp.utils.enums.MovieGenre;
import moviesApp.utils.enums.MpaaRating;
import moviesApp.utils.Coordinates;

import java.util.Date;
import java.util.Hashtable;

public class MovieStringConverter {
    public void addMovieToTable(Movie movie, Hashtable<Integer, Movie> movieHashtable, Hashtable<String, Person> personHashtable) {
        movieHashtable.put(movie.getId(), movie);
        personHashtable.put(movie.getDirector().getPassportID(), movie.getDirector());
    }

    public String movieToJsonFormat(Movie movie) {
        String result = "";
        result = result + "\t\t\"id\": \"" + movie.getId() + "\",\n";
        result = result + "\t\t\"name\": \"" + movie.getName() + "\",\n";
        result = result + "\t\t\"coordinates\": \"" + movie.getCoordinates() + "\",\n";
        result = result + "\t\t\"creationDate\": \"" + movie.getCreationDate().getDate() + "/"
                + (movie.getCreationDate().getMonth() + 1) + "/"
                + (movie.getCreationDate().getYear()) + "\",\n";
        result = result + "\t\t\"oscarsCount\": \"" + movie.getOscarsCount() + "\",\n";
        result = result + "\t\t\"genre\": \"" + movie.getGenre() + "\",\n";
        result = result + "\t\t\"mpaaRating\": \"" + movie.getMpaaRating() + "\",\n";
        result = result + "\t\t\"director\": \"" + movie.getDirector() + "\"\n";
        return result;
    }

    public Movie convertDataArrayToMovie(String[] data) {
        try {
            final Movie movie = new Movie();
            movie.setId(Integer.parseInt(data[0]));
            movie.setName(data[1]);
            movie.setCoordinates(stringToCoordinates(data[2]));
            movie.setCreationDate(stringToDate(data[3]));
            movie.setOscarsCount(Integer.parseInt(data[4]));
            movie.setGenre(MovieGenre.valueOf(data[5]));
            movie.setMpaaRating(MpaaRating.valueOf(data[6]));
            movie.setDirector(stringToPerson(data[7]));
            return movie;
        } catch (Throwable e) {
            System.out.println("Invalid data or data format");
            System.exit(0);
        }
        return null;
    }

    public static Movie readFromDb(String[] data) {
        try {
            final Movie movie = new Movie();
            movie.setId(Integer.parseInt(data[0]));
            movie.setName(data[1]);
            movie.setCoordinates(new Coordinates(Double.parseDouble(data[2]), Double.parseDouble(data[3])));
            movie.setCreationDate(stringToDate(data[4]));
            movie.setOscarsCount(Integer.parseInt(data[5]));
            movie.setGenre(MovieGenre.valueOf(data[6]));
            movie.setMpaaRating(MpaaRating.valueOf(data[7]));
            movie.setDirector(new Person(data[8],
                    data[9],
                    Color.valueOf(data[10]),
                    Color.valueOf(data[11]),
                    Country.valueOf(data[12])));
            movie.setOwnerToken(data[13]);
            return movie;
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("Invalid data or data format");
            System.exit(0);
        }
        return null;
    }

    private Person stringToPerson(String line) {
        String[] data = line.split(" ");
        return new Person(
                data[0],
                data[1],
                Color.valueOf(data[2]),
                Color.valueOf(data[3]),
                Country.valueOf(data[4]));
    }

    private Coordinates stringToCoordinates(String line) {
        return new Coordinates(Double.parseDouble(line.split(" ")[0]),
                Double.parseDouble(line.split(" ")[1]));
    }

    private static Date stringToDate(String line) {
        return new Date(Integer.parseInt(line.split("/")[2]),
                Integer.parseInt(line.split("/")[1]) - 1,
                Integer.parseInt(line.split("/")[0]));
    }
}
