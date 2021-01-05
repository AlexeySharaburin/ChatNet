import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

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
        try {
            if (archive.size() > 0) {

                bufferedWriter.write("\n*** Архив сообщений чата:" + "\n");
                for (String message : archive) {
                    bufferedWriter.write(message + "\n");
                }
                bufferedWriter.write("*** В архиве сообщений больше нет.\n" + "\n");
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
