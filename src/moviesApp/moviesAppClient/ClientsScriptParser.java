package moviesApp.moviesAppClient;

import moviesApp.entities.Movie;
import moviesApp.moviesAppServer.services.MovieStringConverter;
import moviesApp.utils.dtos.CommandRequestDto;
import moviesApp.utils.exceptions.MoviesAppException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;

public class ClientsScriptParser {
    private final MovieStringConverter movieStringConverter;

    public ClientsScriptParser() {
        movieStringConverter = new MovieStringConverter();
    }


    public CommandRequestDto parseCommand(String command, BufferedReader reader) {
        String[] splitCommand = command.split(" ");
        CommandRequestDto commandDto = new CommandRequestDto();
        commandDto.setCommand(splitCommand[0]);
        try {
            if (splitCommand.length > 2) {
                throw new RuntimeException();
            }
            switch (splitCommand[0]) {
                case ("info"):
                case ("sum_of_oscars_count"):
                case ("clear"):
                case ("help"):
                case ("exit"):
                case ("show"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    } else return commandDto;
                }
                case ("remove_lower"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    }
                    commandDto.setMovie(getMovieFromScript(0, reader));
                    return commandDto;
                }
                case ("insert"):
                case ("update"):
                case ("replace_if_greater"):
                    int id = Integer.parseInt(splitCommand[1]);
                    commandDto.setArgument(splitCommand[1]);
                    commandDto.setMovie(getMovieFromScript(id, reader));
                    return commandDto;
                case ("remove"):
                case ("remove_greater_key"):
                case ("filter_greater_than_oscars_count"): {
                    commandDto.setArgument(String.valueOf(Integer.parseInt(splitCommand[1])));
                    return commandDto;
                }
                case ("filter_starts_with_name"): {
                    commandDto.setArgument(splitCommand[1]);
                    return commandDto;
                }
                default:
                    throw new MoviesAppException("Invalid command");
            }
        } catch (Throwable e) {
            System.out.println("Invalid script");
            throw new MoviesAppException("");
        }
    }

    String[] readMovieData(BufferedReader reader) {
        String[] data = new String[8];
        try {
            data[1] = readMovieName(reader);
            data[2] = readMovieCoordinates(reader);
            Calendar calendar = Calendar.getInstance();
            data[3] = calendar.get(Calendar.DAY_OF_MONTH) + "/"
                    + calendar.get(Calendar.MONTH) + "/" +
                    calendar.get(Calendar.YEAR);
            data[4] = readMovieOscarsCount(reader);
            data[5] = readMovieGenre(reader);
            data[6] = readMovieMpaaRating(reader);
            data[7] = readPerson(reader);
            return data;
        } catch (IOException | MoviesAppException e) {
            throw new MoviesAppException("Invalid data");
        }
    }

    private String readPersonName(BufferedReader reader) throws IOException, MoviesAppException {
        String name = reader.readLine();
        if (name.equals("")) {
            throw new MoviesAppException("Invalid director's name");
        }
        return name;
    }

    private String readPersonEyeColor(BufferedReader reader) throws IOException, MoviesAppException {
        String eyeColor = reader.readLine();
        if (eyeColor.equals("GREEN")
                || eyeColor.equals("RED")
                || eyeColor.equals("ORANGE")
                || eyeColor.equals("YELLOW")
                || eyeColor.equals("WHITE")
        ) {
            return eyeColor;
        } else {
            throw new MoviesAppException("Invalid eye color");
        }
    }

    private String readPersonHairColor(BufferedReader reader) throws IOException, MoviesAppException {
        String hairColor = reader.readLine();
        if (hairColor.equals("GREEN")
                || hairColor.equals("RED")
                || hairColor.equals("ORANGE")
                || hairColor.equals("YELLOW")
                || hairColor.equals("WHITE")
        ) {
            return hairColor;
        } else {
            throw new MoviesAppException("Invalid hair color");
        }
    }

    private String readPersonNationality(BufferedReader reader) throws IOException, MoviesAppException {
        String nationality = reader.readLine();
        if (nationality.equals("ITALY")
                || nationality.equals("JAPAN")
                || nationality.equals("VATICAN")
                || nationality.equals("UNITED_KINGDOM")
                || nationality.equals("SPAIN")
        ) {
            return nationality;
        } else {
            throw new MoviesAppException("Invalid nationality");
        }
    }

    private String readPerson(BufferedReader reader) throws IOException, MoviesAppException {
        String name = readPersonName(reader);
        String eyeColor = readPersonEyeColor(reader);
        String hairColor = readPersonHairColor(reader);
        String nationality = readPersonNationality(reader);
        String passportId = reader.readLine();
        try {
            if (10 > passportId.length() || passportId.length() >= 50) {
                throw new RuntimeException();
            }
            return name + " " + passportId + " " + eyeColor + " " + hairColor + " " + nationality;
        } catch (Throwable e) {
            throw new MoviesAppException("Invalid director's passportId");
        }
    }

    private String readMovieMpaaRating(BufferedReader reader) throws IOException, MoviesAppException {
        String mpaaRating = reader.readLine();
        if (mpaaRating.equals("G")
                || mpaaRating.equals("PG")
                || mpaaRating.equals("PG_13")
                || mpaaRating.equals("NC_17")
        ) {
            return mpaaRating;
        } else {
            throw new MoviesAppException("Invalid mpaaRating");
        }
    }

    private String readMovieGenre(BufferedReader reader) throws IOException, MoviesAppException {
        String genre = reader.readLine();
        if (genre.equals("THRILLER") || genre.equals("HORROR") || genre.equals("FANTASY")) {
            return genre;
        } else {
            throw new MoviesAppException("Invalid movie genre");
        }

    }

    private String readMovieName(BufferedReader reader) throws IOException, MoviesAppException {
        String name = reader.readLine();
        if (name.equals("")) {
            throw new MoviesAppException("Invalid movie name");
        }
        return name;
    }

    private String readMovieOscarsCount(BufferedReader reader) throws IOException, MoviesAppException {
        String count = reader.readLine();
        try {
            if (Integer.parseInt(count) < 1) {
                throw new RuntimeException();
            }
            return count;
        } catch (Throwable e) {
            throw new MoviesAppException("Invalid oscars count");
        }
    }

    private String readMovieCoordinates(BufferedReader reader) throws IOException, MoviesAppException {
        String coord = reader.readLine();
        try {
            String[] pair = coord.split(" ");
            if (pair.length != 2) {
                throw new RuntimeException();
            }
            Double.parseDouble(pair[0]);
            Double.parseDouble(pair[1]);
            return coord;
        } catch (Throwable e) {
            throw new MoviesAppException("Invalid movie coordinates");
        }
    }

    private Movie getMovieFromScript(int id, BufferedReader reader) {
        String[] movieData = readMovieData(reader);
        movieData[0] = String.valueOf(id);
        return movieStringConverter.convertDataArrayToMovie(movieData);
    }
}
