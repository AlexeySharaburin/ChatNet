import java.io.*;
import java.net.Socket;


public class ServerThread extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {

        String name = null;
        String message;

        try { // вход в чат

            while (true) { // проверка имени пользователя

                name = in.readLine();

                if (Server.listOfNames.add(name)) {
                    out.write("ОК" + "\n");
                    out.flush();
                    break;
                } else {
                    out.write("Ошибка!" + "\n");
                    out.flush();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Server.archive.sendMessage(out);

        String stringEnter = Server.time + " " + name + " вошёл в чат.";
        writeMessageEverywhere(stringEnter);


        try {
            out.write(name + ", теперь вы можете начать обмениваться сообщениями " +
                    "(для выхода из чата наберите 'выход')" + "\n");
            out.flush();
            while (true) { // выход из чата
                message = in.readLine();
                if (message.equals("выход")) {
                    String stringExit = Server.time + " " + name + " покинул чат.";
                    writeMessageEverywhere(stringExit);
                    this.send("выход");
                    this.closeSocket();
                    break;
                }
                // штатная работа чата
                String fullMessage = Server.time + " " + name + ": " + message;
                writeMessageEverywhere(fullMessage);
            }
        } catch (IOException e) {
            this.closeSocket();
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerThread serverThread : Server.listOfClients) {
                    if (serverThread.equals(this)) serverThread.interrupt();
                    Server.listOfClients.remove(this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMessageEverywhere(String string) {

        System.out.println(string);

        Server.archive.addMessage(string);

        for (ServerThread serverThread : Server.listOfClients) {
            if (!serverThread.socket.isClosed()) {
                if (!serverThread.equals(this)) {
                    serverThread.send(string);
                }
            }
        }

        try (FileWriter writerLog = new FileWriter(Server.nameLog, true)) {
            writerLog.write(string + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}