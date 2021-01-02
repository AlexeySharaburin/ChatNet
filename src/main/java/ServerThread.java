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
        Server.archive.sendMessage(out);
        start();
    }

    @Override
    public void run() {
        String name = null;
        String message;
        try {
            name = in.readLine();
            try (FileWriter writerLog = new FileWriter(Server.nameLog, true)) {
                writerLog.write(Server.time + " " + name + " вошёл в чат.\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                message = in.readLine();
                if (message.equals("exit")) {
                    String stringExit = Server.time + " " + name + " покинул чат.\n";
                    System.out.println(stringExit);
                    for (ServerThread serverThread : Server.listOfClients) {
                        if (!serverThread.socket.isClosed()) {
                            if (!serverThread.equals(this)) {
                                serverThread.send(stringExit);
                            }
                        }
                    }
                    try (FileWriter writerLog = new FileWriter(Server.nameLog, true)) {
                        writerLog.write(stringExit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.closeSocket();
                    break;
                }

                String fullMessage = Server.time + " " + name + ": " + message;

                System.out.println(fullMessage);

                Server.archive.addMessage(fullMessage);

                for (ServerThread serverThread : Server.listOfClients) {
                    if (!serverThread.socket.isClosed()) {
                        if (!serverThread.equals(this)) {
                            serverThread.send(fullMessage);
                        }
                    }
                }

                try (FileWriter writerLog = new FileWriter(Server.nameLog, true)) {
                    writerLog.write(fullMessage + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

}
