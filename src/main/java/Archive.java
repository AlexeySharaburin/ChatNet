import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Archive {

    private static int sizeOfMessages = 10;
    private List<String> archive = new ArrayList<>();

    public void addMessage(String message) {

        if (archive.size() >= sizeOfMessages) {
            archive.remove(0);
            archive.add(message);
        } else {
            archive.add(message);
        }
    }

    public void sendMessage(BufferedWriter bufferedWriter) {
        if (archive.size() > 0) {
            try {
                bufferedWriter.write("История сообщений:" + "\n");
                for (String message : archive) {
                    bufferedWriter.write(message + "\n");
                }

                bufferedWriter.write("пока сообщений в архиве больше нет" + "\n");
                bufferedWriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
