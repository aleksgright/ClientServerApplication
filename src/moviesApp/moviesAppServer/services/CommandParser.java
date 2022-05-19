package moviesApp.moviesAppServer.services;

import moviesApp.entities.Person;

import java.util.*;

public class CommandParser {
    private final CommandExecutor commandExecutor;

    public CommandParser(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public String parseCommand(String command) {
        String[] splitCommand = command.split(" ");
        try {
            if (splitCommand.length > 2) {
                throw new RuntimeException();
            }
            switch (splitCommand[0]) {
                case ("info"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    } else return commandExecutor.info();
                }
                case ("exit"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    } else return commandExecutor.exit();
                }
                case ("help"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    } else return commandExecutor.help();
                }
                case ("clear"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    } else return commandExecutor.clear();
                }
                case ("show"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    } else return commandExecutor.show();
                }
                case ("remove_lower"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    } else return commandExecutor.removeLower();
                }
                case ("sum_of_oscars_count"): {
                    if (splitCommand.length > 1) {
                        throw new RuntimeException();
                    } else return commandExecutor.sumOfOscarsCount();
                }
                case ("insert"):
                    return commandExecutor.insert(Integer.parseInt(splitCommand[1]));
                case ("update"):
                    return commandExecutor.update(Integer.parseInt(splitCommand[1]));
                case ("remove"):
                    return commandExecutor.remove(Integer.parseInt(splitCommand[1]));
                case ("remove_greater_key"):
                    return commandExecutor.removeGreaterKey(Integer.parseInt(splitCommand[1]));
                case ("replace_if_greater"):
                    return commandExecutor.replaceIfGreater(Integer.parseInt(splitCommand[1]));
                case ("filter_greater_than_oscars_count"):
                    return commandExecutor.filterGreaterThanOscarsCount(Integer.parseInt(splitCommand[1]));
                case ("filter_starts_with_name"):
                    return commandExecutor.filterStartsWithName(splitCommand[1]);
                case ("execute_script"):
                    return commandExecutor.executeScript(splitCommand[1]);
                default:
                    return commandExecutor.informAboutInvalidCommand();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return commandExecutor.informAboutInvalidCommand();
        }
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
                Person personToCompare = commandExecutor.getPersonsHashtable().get(passportId);
                if (personToCompare != null && (
                        !name.equals(personToCompare.getName())
                                || !eyeColor.equals(personToCompare.getEyeColor().toString())
                                || !hairColor.equals(personToCompare.getHairColor().toString())
                                || !nationality.equals(personToCompare.getNationality().toString())
                )) {
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
}
