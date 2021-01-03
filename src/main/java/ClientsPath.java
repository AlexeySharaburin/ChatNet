import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        System.out.println("Добро пожаловать в ChatNet!");
        System.out.print("Введите ваше имя: ");
        try {
            clientName = inputMessage.readLine();
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
                        System.out.println("Всего хорошего! До новых встреч в ChatNet!");
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
            while (true) {
                String clientMessage;
                try {
                    clientMessage = inputMessage.readLine();
                    out.write(clientMessage + "\n");
                    out.flush();
                    if (clientMessage.equals("выход")) {
                        break;
                    }
                } catch (IOException e) {
                    ClientsPath.this.closeSocket();
                    e.printStackTrace();
                }

            }
        }
    }
}
