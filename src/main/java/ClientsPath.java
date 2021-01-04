import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClientsPath {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputMessage;
    private String clientName;
    private String address;
    private int port;

    public ClientsPath(String address, int port) {
        this.address = address;
        this.port = port;

        try {
            this.socket = new Socket(address, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputMessage = new BufferedReader((new InputStreamReader(System.in)));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.getName();
            new ReadMessageThread().start();
            new WriteMessageThread().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getName() {
        try {
            System.out.println("Добро пожаловать в ChatNet2.0!");
            while (true) {
                System.out.print("Введите ваше имя: ");
                clientName = inputMessage.readLine();
                if (Server.listOfNames.add(clientName)) {
                    break;
                } else {
                    System.out.println("Уже есть такой пользователь в ChatNet!");
                }
            }
            System.out.println("Список участников чата: " + Server.listOfNames);
            System.out.printf("%s, вы можете начать обмениваться сообщениями " +
                    "(для выхода из чата наберите 'выход')\n", clientName);
            out.write(clientName + "\n");
            out.flush();
        } catch (IOException e) {
            ClientsPath.this.closeSocket();
            e.printStackTrace();
        }
    }

    private void closeSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // поток чтения сообщений от сервера
    private class ReadMessageThread extends Thread {
        @Override
        public void run() {
            String string;
            try {
                while (true) {
                    string = in.readLine();
                    if (string.equals("выход")) {
                        System.out.printf("%s, вы покинули чат!\n", clientName);
                        System.out.println("Всего хорошего! До новых встреч в ChatNet2.0!");
                        Server.listOfNames.remove(clientName);
                        ClientsPath.this.closeSocket();
                        break;
                    } else {
                        System.out.println(string);
                    }
                }
            } catch (IOException e) {
                ClientsPath.this.closeSocket();
                e.printStackTrace();
            }
        }
    }

    // поток сообщений с консоли на сервер
    public class WriteMessageThread extends Thread {
        @Override
        public void run() {
            String clientMessage;
            try {
                while (true) {
                    clientMessage = inputMessage.readLine();
                    out.write(clientMessage + "\n");
                    out.flush();
                    if (clientMessage.equals("выход")) {
                        break;
                    }
                }
            } catch (IOException e) {
                ClientsPath.this.closeSocket();
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientsPath)) return false;
        ClientsPath that = (ClientsPath) o;
        return Objects.equals(clientName, that.clientName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientName);
    }
}


