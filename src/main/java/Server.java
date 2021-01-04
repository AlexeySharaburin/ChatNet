import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    public static String nameSettings = "settings.txt";
    public static String nameLog = "file.log";

    public static int portNumber = 23456;

    public static String time = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date());

    public static CopyOnWriteArrayList<ServerThread> listOfClients = new CopyOnWriteArrayList<>();

    public static Archive archive = new Archive();

    final public static ConcurrentSkipListSet<String> listOfNames = new ConcurrentSkipListSet<>();

    public static void main(String[] args) throws IOException {


        createFiles();

        ServerSocket serverSocket = new ServerSocket(portNumber);

        System.out.println("Добро пожаловать в ChatNet!");
        try (FileWriter writerLog = new FileWriter(nameLog, true)) {
            writerLog.write(time + ": Чат начал работу.\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    listOfClients.add(new ServerThread(socket));
                } catch (IOException e) {
                    socket.close();
                    e.printStackTrace();
                }
            }
        } finally {
            serverSocket.close();
        }

    }

    public static void createFiles() {
        String msgSettings = "Файл settings.txt успешно создан";
        String msgLog = "Файл file.log успешно создан";

        File settingsFile = new File(nameSettings);
        try {
            if (settingsFile.createNewFile())
                System.out.println(msgSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writerPortNumber = new FileWriter(nameSettings, false)) {
            writerPortNumber.write(String.valueOf(portNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }

        File logFile = new File(nameLog);
        try {
            if (logFile.createNewFile())
                System.out.println(msgLog);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writerLogs = new FileWriter(nameLog, true)) {
            writerLogs.write(time + ": " + msgSettings + "\n");
            writerLogs.write(time + ": " + msgLog + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
