import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class Client {

    public static String nameSettings = "settings.txt";
    public static String ip = "127.0.0.1";
    public static int portNumber;


    public static void main(String[] args) {

        portNumber = readPortNumber();
        new ClientsPath(ip, portNumber);

    }

    public static int readPortNumber() {
        String portNumber = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(nameSettings))) {
            String s;
            while ((s = reader.readLine()) != null) {
                portNumber = s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(portNumber);
    }

}
