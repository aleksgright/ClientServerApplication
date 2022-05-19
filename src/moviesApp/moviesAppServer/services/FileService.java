package moviesApp.moviesAppServer.services;

import moviesApp.entities.*;
import moviesApp.utils.exceptions.MoviesAppException;

import java.io.*;
import java.util.Hashtable;

public class FileService {
    private final MovieStringConverter movieStringConverter;
    private static final String ENVIRONMENT_KEY = "LABA";
    private final String filePath;

    public FileService() {
        this.filePath = System.getenv(ENVIRONMENT_KEY);
        movieStringConverter = new MovieStringConverter();
    }

    public void read(Hashtable<Integer, Movie> movieHashtable, Hashtable<String, Person> personsTable) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            firstBlock:
            {
                while (true) {
                    reader.readLine();
                    reader.readLine();
                    String[] dataArray = new String[8];
                    for (int i = 0; i < 8; i++) {
                        String line = reader.readLine();
                        if (line == null) {
                            break firstBlock;
                        }
                        dataArray[i] = jsonLineToStringData(line);
                    }
                    movieStringConverter.addMovieToTable(
                            movieStringConverter.convertDataArrayToMovie(dataArray),
                            movieHashtable, personsTable
                    );
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new MoviesAppException("Can not read the file");
        }
    }

    public void write(Hashtable<Integer, Movie> movieHashtable) {
        write(movieHashtable, filePath);
    }

    public void write(Hashtable<Integer, Movie> movieHashtable, String fileName) {
        try {
            int commaCounter = 0;
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            byte[] start = "[\n\t{\n".getBytes();
            fileOutputStream.write(start);
            for (int i = 1; i <= movieHashtable.size(); i++) {
                byte[] movieToBytes = movieStringConverter.movieToJsonFormat(movieHashtable.get(i)).getBytes();
                fileOutputStream.write(movieToBytes);
                if (commaCounter < movieHashtable.size() - 1) {
                    byte[] end = "\t},\n\t{\n".getBytes();
                    fileOutputStream.write(end);
                } else {
                    byte[] end = "\t}\n]".getBytes();
                    fileOutputStream.write(end);
                }
                commaCounter++;
            }
            fileOutputStream.close();
        } catch (IOException e) {
            throw new MoviesAppException("Cannot write in file");
        }
    }

    private String jsonLineToStringData(String line) {
        return line.split("\"")[3];
    }
}
