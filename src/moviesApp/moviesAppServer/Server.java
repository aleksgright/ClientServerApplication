package moviesApp.moviesAppServer;

import moviesApp.entities.Movie;
import moviesApp.entities.Person;
import moviesApp.moviesAppServer.services.CommandExecutor;
import moviesApp.moviesAppServer.services.SqlService;
import moviesApp.utils.dtos.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Hashtable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Server implements Runnable {
    private CommandExecutor commandExecutor;
    private ServerSocketChannel serverSocketChannel;
    public static final int PORT = 8790;

    public Server(Hashtable<Integer, Movie> moviesHashtable, Hashtable<String, Person> personsHashtable, SqlService sqlService) {
        try {
            commandExecutor = new CommandExecutor(moviesHashtable, personsHashtable, sqlService);
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RequestDto receiveData(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(16000);
        socketChannel.read(buffer);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
        System.out.println("Message received");
        return (RequestDto) objectInputStream.readObject();
    }

    private ResponseDto performRequest(RequestDto request) {
        ResponseDto responseDto = null;
        if (request instanceof LogInRequestDto) {
            responseDto = commandExecutor.executeFromDto((LogInRequestDto) request);
        }
        if (request instanceof RegisterRequestDto) {
            responseDto = commandExecutor.executeFromDto((RegisterRequestDto) request);
        }
        if (request instanceof CommandRequestDto) {
            responseDto = new ResponseDto();
            if (request.getCommand().equals("log_out")) {
                responseDto.setToken(null);
                responseDto.addMessage("You logged out");
            } else {
                responseDto.setToken(request.getToken());
                responseDto.addMessage(commandExecutor.executeFromDto((CommandRequestDto) request));
            }
        }
        return responseDto;
    }

    private void sendResponse(ResponseDto response, SocketChannel socketChannel) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        objectOutputStream.flush();
        socketChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
    }

    public void run() {
        try {
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                Runnable reading = () -> {
                    try {
                        RequestDto requestDto = receiveData(socketChannel);
                        Runnable performing = () -> {
                            ResponseDto responseDto = performRequest(requestDto);
                            ForkJoinPool forkJoinPool = new ForkJoinPool();
                            forkJoinPool.invoke(new RecursiveTask<Object>() {
                                @Override
                                protected Object compute() {
                                    try {
                                        sendResponse(responseDto, socketChannel);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            });
                        };
                        Thread perform = new Thread(performing);
                        perform.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                Thread perform = new Thread(reading);
                perform.start();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}

