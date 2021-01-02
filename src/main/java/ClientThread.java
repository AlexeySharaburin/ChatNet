import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientThread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputMessage;
    private String clientName;
    private String address;
    private int port;
    private Date date = new Date();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

    public ClientThread(String address, int port) {
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
            new ReadMessage().start();
            new WriteMessage().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getName() {
        System.out.println("Добро пожаловать в ChatNet!");
        System.out.print("Введите ваше имя: ");
        try {
            clientName = inputMessage.readLine();
            System.out.printf("%s, вы можете начать обмениваться сообщениями\n", clientName);
            out.write(clientName + "\n");
            out.flush();
        } catch (IOException e) {
            ClientThread.this.closeSocket();
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
    private class ReadMessage extends Thread {
        @Override
        public void run() {
            String string;
            try {
                while (true) {
                    string = in.readLine();
                    System.out.println(string);
                }
            } catch (IOException e) {
                ClientThread.this.closeSocket();
                e.printStackTrace();
            }
        }
    }

    // поток сообщений с консоли на сервер
    public class WriteMessage extends Thread {
        @Override
        public void run() {
            while (true) {
                String clientMessage;
                try {
                    clientMessage = inputMessage.readLine();
                    if (clientMessage.equals("exit")) {
                        ClientThread.this.closeSocket();
                        break;
                    } else {
                        out.write(clientMessage + "\n");
                    }
                    out.flush();
                } catch (IOException e) {
                    ClientThread.this.closeSocket();
                    e.printStackTrace();
                }

            }
        }
    }
}
