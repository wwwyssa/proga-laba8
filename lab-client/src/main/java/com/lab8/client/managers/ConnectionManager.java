package com.lab8.client.managers;

import com.lab8.common.util.Request;
import com.lab8.common.util.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.channels.SocketChannel;

/**
 * Класс для управления соединением с сервером.
 */
public class ConnectionManager {
    private static ConnectionManager instance;
    private final int PORT = 15719;
    private final String SERVER_HOST = "localhost";
    private SocketChannel channel;


    /**
     * Приватный конструктор класса ConnectionManager.
     */
    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    /**
     * Метод для подключения к серверу.
     *
     * @throws IOException Если произошла ошибка ввода-вывода.
     */
    public void connect() throws IOException {
        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(InetAddress.getByName(SERVER_HOST), PORT));
    }

    public int getPort() {
        return PORT;
    }

    public String getHost() {
        return SERVER_HOST;
    }

    /**
     * Метод для отключения от сервера.
     *
     * @throws IOException Если произошла ошибка ввода-вывода.
     */
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }

    /**
     * Метод для проверки, подключен ли клиент к серверу.
     *
     * @return true, если клиент подключен к серверу, иначе false.
     */
    public void send(Request request) throws IOException, ClassNotFoundException {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bytes)) {

            out.writeObject(request);
            ByteBuffer dataToSend = ByteBuffer.wrap(bytes.toByteArray());
            channel.write(dataToSend); // отправляем серверу запрос
            out.flush();
        }
    }

    /**
     * Метод для получения ответа от сервера.
     *
     * @return Ответ от сервера.
     * @throws IOException              Если произошла ошибка ввода-вывода.
     * @throws ClassNotFoundException   Если произошла ошибка при десериализации объекта.
     * @throws BufferUnderflowException Если произошла ошибка при чтении из буфера.
     */
    public Response receive() throws IOException, ClassNotFoundException, BufferUnderflowException {
        ByteBuffer dataToReceiveLength = ByteBuffer.allocate(8);
        channel.read(dataToReceiveLength); // читаем длину ответа от сервера
        dataToReceiveLength.flip(); // переводим в режим чтения
        int responseLength = dataToReceiveLength.getInt(); // достаём её из буфера

        ByteBuffer responseBytes = ByteBuffer.allocate(responseLength); // создаем буфер для ответа от сервера
        ByteBuffer packetFromServer = ByteBuffer.allocate(256);

        while (true) {
            channel.read(packetFromServer);
            if (packetFromServer.position() == 2 && packetFromServer.get(0) == 69 && packetFromServer.get(1) == 69)
                break;
            packetFromServer.flip(); // переводим в режим чтения
            responseBytes.put(packetFromServer);
            packetFromServer.clear();
        }

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(responseBytes.array()))) {
            return (Response) in.readObject();
        }
    }
}