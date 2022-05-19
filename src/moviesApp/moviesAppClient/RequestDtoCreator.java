package moviesApp.moviesAppClient;

import moviesApp.entities.Movie;
import moviesApp.moviesAppServer.services.MovieStringConverter;
import moviesApp.utils.dtos.CommandRequestDto;
import moviesApp.utils.dtos.LogInRequestDto;
import moviesApp.utils.dtos.RegisterRequestDto;
import moviesApp.utils.dtos.RequestDto;
import moviesApp.utils.exceptions.MoviesAppException;

import java.util.Calendar;
import java.util.Scanner;

public class RequestDtoCreator {
    private final MovieStringConverter movieStringConverter;

    public RequestDtoCreator() {
        movieStringConverter = new MovieStringConverter();
    }

    public RequestDto parseCommand(String command) {
        String[] splitCommand = command.split(" ");
        CommandRequestDto commandDto = new CommandRequestDto();
        commandDto.setCommand(splitCommand[0]);
        try {
            if (splitCommand.length > 2) {
                throw new RuntimeException();
            }
            switch (splitCommand[0]) {
                case ("register"): {
                    return getRegisterDto(enterLoginAndPassword());
                }
                case ("log_in"): {
                    return getLoginDto(enterLoginAndPassword());
                }
                case ("log_out"):
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
                    commandDto.setMovie(getMovieFromConsole(0));
                    return commandDto;
                }
                case ("insert"):
                case ("update"):
                case ("replace_if_greater"):
                    int id = Integer.parseInt(splitCommand[1]);
                    commandDto.setArgument(splitCommand[1]);
                    commandDto.setMovie(getMovieFromConsole(id));
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
            throw new MoviesAppException("Invalid command");
        }
    }

    private RegisterRequestDto getRegisterDto(String[] args) {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setCommand("register");
        registerRequestDto.setLogin(args[0]);
        registerRequestDto.setPassword(args[1]);
        return registerRequestDto;
    }

    private LogInRequestDto getLoginDto(String[] args) {
        LogInRequestDto requestDto = new LogInRequestDto();
        requestDto.setCommand("register");
        requestDto.setLogin(args[0]);
        requestDto.setPassword(args[1]);
        return requestDto;
    }

    private String[] enterLoginAndPassword() {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter login>");
        String login = in.nextLine();
        while (login.equals("")) {
            System.out.println("Login can not be null");
            System.out.print("Enter login>");
            login = in.nextLine();
        }
        System.out.print("Enter password>");
        String password = in.nextLine();
        while (password.equals("")) {
            System.out.println("Login can not be null");
            System.out.print("Enter password>");
            password = in.nextLine();
        }
        return new String[]{login, password};
    }

    public String[] inputMovieData() {
        Scanner in = new Scanner(System.in);
        String[] data = new String[8];
        data[1] = inputMovieName(in);
        data[2] = inputMovieCoordinates(in);
        Calendar calendar = Calendar.getInstance();
        data[3] = calendar.get(Calendar.DAY_OF_MONTH) + "/"
                + calendar.get(Calendar.MONTH) + "/" +
                calendar.get(Calendar.YEAR);
        data[4] = inputMovieOscarsCount(in);
        data[5] = inputMovieGenre(in);
        data[6] = inputMovieMpaaRating(in);
        data[7] = inputPerson(in);
        return data;
    }

    private String inputPersonName(Scanner in) {
        System.out.print("Enter director's name>");
        String name = in.nextLine();
        while (name.equals("")) {
            System.out.println("Invalid name. Try again");
            System.out.print("Enter director's name>");
            name = in.nextLine();
        }
        return name;
    }

    private String inputPersonEyeColor(Scanner in) {
        String eyeColor;
        while (true) {
            System.out.print("Enter director's eye color (GREEN, RED, ORANGE, YELLOW, WHITE)>");
            eyeColor = in.nextLine();
            if (eyeColor.equals("GREEN")
                    || eyeColor.equals("RED")
                    || eyeColor.equals("ORANGE")
                    || eyeColor.equals("YELLOW")
                    || eyeColor.equals("WHITE")
            ) {
                break;
            }
            System.out.println("Invalid director's eye color. Try again");
        }
        return eyeColor;
    }

    private String inputPersonHairColor(Scanner in) {
        String hairColor;
        while (true) {
            System.out.print("Enter director's hair color (GREEN, RED, ORANGE, YELLOW, WHITE)>");
            hairColor = in.nextLine();
            if (hairColor.equals("GREEN")
                    || hairColor.equals("RED")
                    || hairColor.equals("ORANGE")
                    || hairColor.equals("YELLOW")
                    || hairColor.equals("WHITE")
            ) {
                break;
            }
            System.out.println("Invalid director's hair color. Try again");
        }
        return hairColor;
    }

    private String inputPersonNationality(Scanner in) {
        String nationality;
        while (true) {
            System.out.print("Enter director's nationality (ITALY, JAPAN, VATICAN, UNITED_KINGDOM, SPAIN)>");
            nationality = in.nextLine();
            if (nationality.equals("ITALY")
                    || nationality.equals("JAPAN")
                    || nationality.equals("VATICAN")
                    || nationality.equals("UNITED_KINGDOM")
                    || nationality.equals("SPAIN")
            ) {
                break;
            }
            System.out.println("Invalid director's nationality. Try again");
        }
        return nationality;
    }

    private String inputPerson(Scanner in) {
        boolean f = true;
        String name = inputPersonName(in);
        String eyeColor = inputPersonEyeColor(in);
        String hairColor = inputPersonHairColor(in);
        String nationality = inputPersonNationality(in);
        String passportId = null;
        while (f) {
            System.out.print("Enter director's passportId>");
            passportId = in.nextLine();
            try {
                if (10 > passportId.length() || passportId.length() >= 50) {
                    throw new RuntimeException();
                }
                f = false;
            } catch (Throwable e) {
                System.out.println("Invalid director's passportId. Try again");
            }
        }
        return name + " " + passportId + " " + eyeColor + " " + hairColor + " " + nationality;
    }

    private String inputMovieMpaaRating(Scanner in) {
        String mpaaRating;
        while (true) {
            System.out.print("Enter Mpaa Rating (G, PG, PG_13, NC_17)>");
            mpaaRating = in.nextLine();
            if (mpaaRating.equals("G")
                    || mpaaRating.equals("PG")
                    || mpaaRating.equals("PG_13")
                    || mpaaRating.equals("NC_17")
            ) {
                break;
            }
            System.out.println("Invalid Mpaa Rating. Try again");
        }
        return mpaaRating;
    }

    private String inputMovieGenre(Scanner in) {
        String genre;
        while (true) {
            System.out.print("Enter genre (THRILLER, HORROR, FANTASY)>");
            genre = in.nextLine();
            if (genre.equals("THRILLER") || genre.equals("HORROR") || genre.equals("FANTASY")) {
                break;
            }
            System.out.println("Invalid genre. Try again");
        }
        return genre;
    }

    private String inputMovieName(Scanner in) {
        System.out.print("Enter name>");
        String name = in.nextLine();
        while (name.equals("")) {
            System.out.println("Invalid name. Try again");
            System.out.print("Enter name>");
            name = in.nextLine();
        }
        return name;
    }

    private String inputMovieOscarsCount(Scanner in) {
        String count = null;
        boolean f = true;
        while (f) {
            System.out.print("Enter oscars count>");
            count = in.nextLine();
            try {
                if (Integer.parseInt(count) < 1) {
                    throw new RuntimeException();
                }
                f = false;
            } catch (Throwable e) {
                System.out.println("Invalid oscars count. Try again");
            }
        }
        return count;
    }

    private String inputMovieCoordinates(Scanner in) {
        boolean f = true;
        String coord = null;
        while (f) {
            System.out.print("Enter coordinates in format \"double double\">");
            coord = in.nextLine();
            try {
                String[] pair = coord.split(" ");
                if (pair.length != 2) {
                    throw new RuntimeException();
                }
                Double.parseDouble(pair[0]);
                Double.parseDouble(pair[1]);
                f = false;
            } catch (Throwable e) {
                System.out.println("Invalid coordinates. Try again");
            }
        }
        return coord;
    }

    private Movie getMovieFromConsole(int id) {
        String[] movieData = inputMovieData();
        movieData[0] = String.valueOf(id);
        return movieStringConverter.convertDataArrayToMovie(movieData);
    }
}
