package moviesApp.moviesAppClient;

import moviesApp.utils.dtos.RequestDto;
import moviesApp.utils.dtos.ResponseDto;
import moviesApp.utils.exceptions.MoviesAppException;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ClientApp {
    private static Socket clientSocket;
    private static String Ip;

    public static void main(String[] args) {
        String token = null;
        try {
            try {
                System.out.print("Enter IP: ");
                Scanner scanner = new Scanner(System.in);
                Ip = scanner.nextLine();
                RequestDtoCreator requestDtoCreator = new RequestDtoCreator();
                int connectionTries = 5;
                boolean f = true;
                while (f) {
                    try {
                        if (connectionTries > 500) {
                            System.out.println("Waiting time exceeded");
                            throw new MoviesAppException("Waiting time exceeded");
                        }
                        if (connectionTries > 0) {
                            System.out.println("Sending connection request...");
                        }
                        connectionTries++;
                        clientSocket = new Socket(Ip, 8790);
                        if (connectionTries > 1) {
                            System.out.println("Connection is up");
                        }
                        connectionTries = 0;
                        System.out.print(">");
                        String word = scanner.nextLine();
                        if ("exit".equals(word)) {
                            f = false;
                        }
                        if (word.startsWith("exec")) {
                            List<RequestDto> requestDtoList = new LinkedList<>();
                            try (BufferedReader reader = new BufferedReader(new FileReader(word.split(" ")[1]))) {
                                ClientsScriptParser clientsScriptParser = new ClientsScriptParser();
                                String scriptCommand;
                                while ((scriptCommand = reader.readLine()) != null) {
                                    if (scriptCommand.equals("executeScript")) {
                                        System.out.println("Inner scripts are not allowed");
                                        break;
                                    }
                                    try {
                                        requestDtoList.add(clientsScriptParser.parseCommand(scriptCommand, reader));
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
                            clientSocket.close();
                            token = sendListOfRequests(requestDtoList.iterator(), token);
                        } else {
                            try {
                                sendRequest(requestDtoCreator.parseCommand(word), token);
                                token = readResponse();
                            } catch (MoviesAppException e) {
                                System.out.println("Invalid command");
                            }
                        }
                    } catch (ConnectException e) {
                        if (connectionTries == 1) {
                            System.out.println("Server is not up");
                        }
                    } catch (SocketException e) {
                        if (connectionTries == 1) {
                            System.out.println("Server does not respond");
                        }
                    }
                }
            } catch (UnknownHostException e) {
                System.out.println("Invalid host");
            } catch (Throwable ignored) {
            } finally {
                System.out.println("Client was closed");
                try {
                    clientSocket.close();
                } catch (NullPointerException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static void sendRequest(RequestDto requestDto, String token) throws IOException {
        requestDto.setToken(token);
        OutputStream outputStream = clientSocket.getOutputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(requestDto);
        byteArrayOutputStream.writeTo(outputStream);
    }

    private static String readResponse() throws IOException, ClassNotFoundException {
        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ResponseDto responseDto = (ResponseDto) objectInputStream.readObject();
        inputStream.close();
        responseDto.printMessages();
        return responseDto.getToken();
    }

    private static String sendListOfRequests(Iterator<RequestDto> iterator, String token) {
        boolean f = true;
        while (f) {
            try {
                clientSocket = new Socket(Ip, 8790);
                RequestDto requestDto = iterator.next();
                try {
                    sendRequest(requestDto, token);
                    token = readResponse();
                } catch (ConnectException e) {
                    System.out.println("Сервер не запущен");
                } catch (SocketException e) {
                    System.out.println("Сервер не отвечает");
                }
                if (!iterator.hasNext()) {
                    f = false;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Problems with transferring data");
            }
        }
        return token;
    }
}
