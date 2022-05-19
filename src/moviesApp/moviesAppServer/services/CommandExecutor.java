package moviesApp.moviesAppServer.services;

import moviesApp.entities.Movie;
import moviesApp.entities.Person;
import moviesApp.utils.dtos.CommandRequestDto;
import moviesApp.utils.dtos.LogInRequestDto;
import moviesApp.utils.dtos.RegisterRequestDto;
import moviesApp.utils.dtos.ResponseDto;
import moviesApp.utils.exceptions.MoviesAppException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CommandExecutor {
    private final SqlService sqlService;
    private final MovieStringConverter movieStringConverter;
    private CommandParser commandParser;
    private final Hashtable<String, Person> personsHashtable;
    private final Hashtable<Integer, Movie> moviesHashtable;
    private Date initialisationDate;
    private ScriptParser scriptParser;
    private CommandRequestDto currentCommandDto;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final TokenService tokenService;

    public CommandExecutor(Hashtable<Integer, Movie> moviesHashtable, Hashtable<String, Person> personsHashtable, SqlService sqlService) {
        this.personsHashtable = personsHashtable;
        this.moviesHashtable = moviesHashtable;
        this.movieStringConverter = new MovieStringConverter();
        this.commandParser = new CommandParser(this);
        this.sqlService = sqlService;
        this.tokenService = new TokenService();
    }

    public void startRunning() {
        initialisationDate = new Date();
        commandParser = new CommandParser(this);
        scriptParser = new ScriptParser(this);
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print(">");
            String command = in.nextLine();
            System.out.println(commandParser.parseCommand(command));
        }
    }

    public String executeFromDto(CommandRequestDto commandDto) {
        this.currentCommandDto = commandDto;
        if (commandDto.getCommand().equals("help")) {
            return help();
        }
        if (commandDto.getCommand().equals("exit")) {
            return "";
        }
        if (commandDto.getToken() == null) {
            return "You are not authorized";
        }
        if (commandDto.getArgument() != null) {
            return commandParser.parseCommand(commandDto.getCommand() + " " + commandDto.getArgument());
        }
        return commandParser.parseCommand(commandDto.getCommand());
    }

    public ResponseDto executeFromDto(RegisterRequestDto registerRequestDto) {
        String login = registerRequestDto.getLogin();
        String password = registerRequestDto.getPassword();
        writeLock.lock();
        ResponseDto responseDto = new ResponseDto();
        try {
            if (registerRequestDto.getToken() != null) {
                responseDto.setToken(registerRequestDto.getToken());
                responseDto.addMessage("You are already authorized");
                return responseDto;
            }
            if (!sqlService.findUserWithLogin(login).next()) {
                String token = tokenService.createToken(login);
                sqlService.registerUser(token, login, SaltService.createHash(password));
                responseDto.setToken(token);
                responseDto.addMessage("You registered successfully");
            } else {
                responseDto.addMessage("This login is already in use");
            }
            return responseDto;
        } catch (SQLException e) {
            e.printStackTrace();
            responseDto.addMessage("Problems with DB");
            return responseDto;
        } finally {
            writeLock.unlock();
        }
    }

    public ResponseDto executeFromDto(LogInRequestDto loginDto) {
        writeLock.lock();
        ResponseDto responseDto = new ResponseDto();
        try {
            if (loginDto.getToken() != null) {
                responseDto.setToken(loginDto.getToken());
                responseDto.addMessage("You are already authorized");
                return responseDto;
            }
            ResultSet resultSet = sqlService.findUserWithLogin(loginDto.getLogin());
            if (!resultSet.next()) {
                responseDto.addMessage("User with this login does not exist");
                return responseDto;
            }
            if (!SaltService.checkPassword(loginDto.getPassword(), resultSet.getString(3))) {
                responseDto.addMessage("Wrong password");
                return responseDto;
            }
            responseDto.setToken(resultSet.getString(1));
            responseDto.addMessage("You logged in successfully");
            return responseDto;
        } catch (SQLException e) {
            e.printStackTrace();
            responseDto.addMessage("Problems with DB");
            return responseDto;
        } finally {
            writeLock.unlock();
        }
    }

    public String info() {
        readLock.lock();
        try {
            return "Collection type: HashTable\n" + "Creation date: "
                    + initialisationDate + "\nКол-во элементов: " + moviesHashtable.size();
        } finally {
            readLock.unlock();
        }
    }

    public String executeScript(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String command;
            while ((command = reader.readLine()) != null) {
                if (command.equals("executeScript")) {
                    System.out.println("Inner scripts are not allowed");
                    break;
                }
                try {
                    scriptParser.parseCommand(command, reader);
                } catch (MoviesAppException e) {
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new MoviesAppException("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            throw new MoviesAppException("Can not read the file");
        }
        return "execute_script completed successfully";
    }

    private void removeDirectorByMoviesId(int id) {
        String personToRemovePassportId = moviesHashtable.get(id).getDirector().getPassportID();
        if (moviesHashtable.values()
                .stream()
                .filter(movie -> movie.getDirector().getPassportID().equals(personToRemovePassportId))
                .count() <= 1
        ) {
            personsHashtable.remove(personToRemovePassportId);
        }
    }

    public String filterGreaterThanOscarsCount(int oscarsCount) {
        readLock.lock();
        StringBuilder result = new StringBuilder();
        for (Movie movie : moviesHashtable.values()) {
            if (movie.getOscarsCount() > oscarsCount) {
                result.append(movie);
            }
        }
        try {
            return result.toString();
        } finally {
            readLock.unlock();
        }
    }

    public String show() {
        readLock.lock();
        StringBuilder result = new StringBuilder();
        for (Movie movie : moviesHashtable.values()) {
            result.append(movie);
        }
        try {
            return result.toString();
        } finally {
            readLock.unlock();
        }
    }

    public String filterStartsWithName(String beginning) {
        readLock.lock();
        StringBuilder result = new StringBuilder();
        for (Movie movie : moviesHashtable.values()) {
            if (movie.getName().indexOf(beginning) == 0) {
                result.append(movie);
            }
        }
        try {
            return result.toString();
        } finally {
            readLock.unlock();
        }
    }

    public String sumOfOscarsCount() {
        readLock.lock();
        try {
            return String.valueOf(moviesHashtable.values().stream().mapToInt(Movie::getOscarsCount).sum());
        } finally {
            readLock.unlock();
        }
    }

    public String removeGreaterKey(int id) {
        writeLock.lock();
        int size = moviesHashtable.size();
        try {
            for (int i = id + 1; i <= size; i++) {
                sqlService.removeMovieById(i, currentCommandDto.getToken());
                if (moviesHashtable.get(id).getOwnerToken() == null ||
                        moviesHashtable.get(id).getOwnerToken().equals(currentCommandDto.getToken())) {
                    removeDirectorByMoviesId(id);
                    moviesHashtable.remove(id);
                }
            }
            return "remove_greater_key completed successfully";
        } catch (SQLException e) {
            return "problems with DB";
        } finally {
            writeLock.unlock();
        }
    }

    public String remove(int id) {
        writeLock.lock();
        try {
            if (moviesHashtable.get(id).getOwnerToken() != null &&
                    !moviesHashtable.get(id).getOwnerToken().equals(currentCommandDto.getToken())) {
                return "You don't have access to this element";
            }
            if (moviesHashtable.containsKey(id)) {
                if (moviesHashtable.get(id).getOwnerToken() == null) {
                    sqlService.removeMovieById(id);
                } else {
                    sqlService.removeMovieById(id, currentCommandDto.getToken());
                }
                removeDirectorByMoviesId(id);
                moviesHashtable.remove(id);
            } else {
                return "No such id";
            }
            return "remove completed successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "problems with DB";
        } finally {
            writeLock.unlock();
        }
    }

    public String clear() {
        writeLock.lock();
        try {
            for (int id : moviesHashtable.keySet()) {
                if (moviesHashtable.get(id).getOwnerToken() == null) {
                    sqlService.removeMovieById(id);
                } else {
                    sqlService.removeMovieById(id, currentCommandDto.getToken());
                }
            }
            Hashtable<Integer, Movie> copy = new Hashtable<>();
            moviesHashtable.values().forEach(movie -> copy.put(movie.getId(), movie));
            copy.values()
                    .stream()
                    .filter(movie -> movie.getOwnerToken() == null || movie.getOwnerToken().equals(currentCommandDto.getToken()))
                    .forEach((movie) -> {
                        removeDirectorByMoviesId(movie.getId());
                        moviesHashtable.remove(movie.getId());
                    });
            return "clear completed successfully";
        } catch (SQLException e) {
            return "problems with DB";
        } finally {
            writeLock.unlock();
        }
    }

    public String help() {
        readLock.lock();
        try {
            return "help : вывести справку по доступным командам\n" +
                    "log_in: авторизация\n" +
                    "log_out: авторизация\n" +
                    "register: регистрация\n" +
                    "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                    "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                    "insert null {element} : добавить новый элемент с заданным ключом\n" +
                    "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                    "remove_key null : удалить элемент из коллекции по его ключу\n" +
                    "clear : очистить коллекцию\n" +
                    "save : сохранить коллекцию в файл\n" +
                    "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                    "exit : завершить программу (без сохранения в файл)\n" +
                    "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                    "replace_if_greater null {element} : заменить значение по ключу, если новое значение больше старого\n" +
                    "remove_greater_key null : удалить из коллекции все элементы, ключ которых превышает заданный\n" +
                    "sum_of_oscars_count : вывести сумму значений поля oscarsCount для всех элементов коллекции\n" +
                    "filter_starts_with_name name : вывести элементы, значение поля name которых начинается с заданной подстроки\n" +
                    "filter_greater_than_oscars_count oscarsCount : вывести элементы, значение поля oscarsCount которых больше заданного";
        } finally {
            readLock.unlock();
        }
    }

    public String exit() {
        System.exit(0);
        return "exit completed successfully";
    }

    public String informAboutInvalidCommand() {
        readLock.lock();
        try {
            return "Invalid command. Enter help to see the list of possible commands";
        } finally {
            readLock.unlock();
        }
    }

    public String replaceIfGreater(int id) {
        writeLock.lock();
        Movie movieToUpdate = getMovieFromConsoleOrCommandDto(id);
        return commonReplaceIfGreater(id, movieToUpdate);
    }

    public String replaceIfGreater(int id, BufferedReader reader) {
        writeLock.lock();
        Movie movieToUpdate = getMovieFromScript(id, reader);
        return commonReplaceIfGreater(id, movieToUpdate);
    }

    private String commonReplaceIfGreater(int id, Movie movieToUpdate) {
        try {
            if (moviesHashtable.containsKey(id)) {
                if (!moviesHashtable.get(id).getOwnerToken().equals(currentCommandDto.getToken())) {
                    return "You don't have access to this element";
                }
                if (movieToUpdate.compareTo(moviesHashtable.get(id)) > 0) {
                    if (moviesHashtable.get(id).getOwnerToken() == null) {
                        sqlService.removeMovieById(id);
                    } else {
                        sqlService.removeMovieById(id, currentCommandDto.getToken());
                    }
                    sqlService.addMovie(movieToUpdate, currentCommandDto.getToken());
                    removeDirectorByMoviesId(id);
                    moviesHashtable.replace(id, movieToUpdate);
                }
            } else {
                return "No such id";
            }
            return "replace_if_greater completed successfully";
        } catch (SQLException e) {
            return "problems with DB";
        } finally {
            writeLock.unlock();
        }
    }

    public String removeLower() {
        writeLock.lock();
        Movie movieToCompare = getMovieFromConsoleOrCommandDto(0);
        return commonRemoveLower(movieToCompare);
    }

    public String removeLower(BufferedReader reader) {
        writeLock.lock();
        Movie movieToCompare = getMovieFromScript(0, reader);
        return commonRemoveLower(movieToCompare);
    }

    private String commonRemoveLower(Movie movieToCompare) {
        try {
            for (int id : moviesHashtable.keySet()) {
                if (movieToCompare.compareTo(moviesHashtable.get(id)) > 0) {
                    if (moviesHashtable.get(id).getOwnerToken() == null) {
                        sqlService.removeMovieById(id);
                    } else {
                        sqlService.removeMovieById(id, currentCommandDto.getToken());
                    }
                }
            }
            Hashtable<Integer, Movie> copy = new Hashtable<>();
            moviesHashtable.values().forEach(movie -> copy.put(movie.getId(), movie));
            copy.values()
                    .stream()
                    .filter(movie -> movieToCompare.compareTo(movie) > 0 && (movie.getOwnerToken() == null
                            || movie.getOwnerToken().equals(currentCommandDto.getToken())))
                    .forEach((movie) -> {
                        removeDirectorByMoviesId(movie.getId());
                        moviesHashtable.remove(movie.getId());
                    });
            return "remove_lower completed successfully";
        } catch (SQLException e) {
            return "problems with DB";
        } finally {
            writeLock.unlock();
        }
    }

    public String insert(int id) {
        writeLock.lock();
        Movie movieToInsert = getMovieFromConsoleOrCommandDto(id);
        return commonInsert(id, movieToInsert);
    }

    public String insert(int id, BufferedReader reader) {
        writeLock.lock();
        Movie movieToInsert = getMovieFromScript(id, reader);
        return commonInsert(id, movieToInsert);
    }

    private String commonInsert(int id, Movie movieToInsert) {
        try {
            if (moviesHashtable.containsKey(id)) {
                return "This id is already in use";
            }
            sqlService.addMovie(movieToInsert, currentCommandDto.getToken());
            movieToInsert.setOwnerToken(currentCommandDto.getToken());
            moviesHashtable.put(id, movieToInsert);
            return "insert completed successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "problems with DB";
        } finally {
            writeLock.unlock();
        }
    }

    public String update(int id) {
        writeLock.lock();
        Movie movieToUpdate = getMovieFromConsoleOrCommandDto(id);
        try {
            if (moviesHashtable.containsKey(id)) {
                if (!moviesHashtable.get(id).getOwnerToken().equals(currentCommandDto.getToken())) {
                    return "You don't have access to this element";
                }
                if (moviesHashtable.get(id).getOwnerToken() == null) {
                    sqlService.removeMovieById(id);
                } else {
                    sqlService.removeMovieById(id, currentCommandDto.getToken());
                }
                sqlService.addMovie(movieToUpdate, currentCommandDto.getToken());
                removeDirectorByMoviesId(id);
                moviesHashtable.replace(id, movieToUpdate);
            } else {
                return "No such id";
            }
            return "replace_if_greater completed successfully";
        } catch (SQLException e) {
            return "problems with DB";
        } finally {
            writeLock.unlock();
        }
    }

    public String update(int id, BufferedReader reader) {
        writeLock.lock();
        if (moviesHashtable.containsKey(id)) {
            removeDirectorByMoviesId(id);
            moviesHashtable.replace(id, getMovieFromScript(id, reader));
        } else {
            return "No such id";
        }
        try {
            return "update completed successfully";
        } finally {
            writeLock.unlock();
        }
    }

    private Movie getMovieFromConsoleOrCommandDto(int id) {
        if (currentCommandDto.getMovie() != null) {
            return currentCommandDto.getMovie();
        }
        String[] movieData = commandParser.inputMovieData();
        movieData[0] = String.valueOf(id);
        Movie movie = movieStringConverter.convertDataArrayToMovie(movieData);
        movie.setOwnerToken(currentCommandDto.getToken());
        personsHashtable.put(movie.getDirector().getPassportID(), movie.getDirector());
        return movie;
    }

    private Movie getMovieFromScript(int id, BufferedReader reader) {
        String[] movieData = scriptParser.readMovieData(reader);
        movieData[0] = String.valueOf(id);
        Movie movie = movieStringConverter.convertDataArrayToMovie(movieData);
        personsHashtable.put(movie.getDirector().getPassportID(), movie.getDirector());
        return movie;
    }

    public Hashtable<String, Person> getPersonsHashtable() {
        return personsHashtable;
    }
}
