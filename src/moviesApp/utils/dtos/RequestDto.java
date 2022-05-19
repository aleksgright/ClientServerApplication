package moviesApp.utils.dtos;

import java.io.Serializable;

public class RequestDto implements Serializable {
    private String command;
    private String token;

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
