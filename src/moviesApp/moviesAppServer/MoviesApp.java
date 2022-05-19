package moviesApp.moviesAppServer;

import moviesApp.entities.Movie;
import moviesApp.entities.Person;
import moviesApp.moviesAppServer.services.CommandExecutor;
import moviesApp.moviesAppServer.services.SqlService;
import org.postgresql.util.PSQLException;

import java.util.Hashtable;
import java.util.Scanner;

//ввести ssh -L 5432:pg:5432 s336504@se.ifmo.ru -p 2222
public class MoviesApp {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter DB login: ");
        String login = in.nextLine();
        System.out.print("Enter DB password: ");
        String password = in.nextLine();
        Hashtable<Integer, Movie> moviesTable = new Hashtable<>();
        Hashtable<String, Person> personsTable = new Hashtable<>();
        try {
            SqlService sqlService = new SqlService("jdbc:postgresql://pg:5432/studs", login, password);
            sqlService.load(moviesTable, personsTable);
            System.out.println("fine");
            Server server = new Server(moviesTable, personsTable, sqlService);
            Thread thread = new Thread(server);
            thread.start();
            CommandExecutor commandExecutor = new CommandExecutor(moviesTable, personsTable, sqlService);
            commandExecutor.startRunning();
        } catch (PSQLException e) {
            System.out.println("Wrong password");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
