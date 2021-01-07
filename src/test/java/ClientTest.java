import junit.framework.TestCase;
import org.junit.Assert;

public class ClientTest extends TestCase {

    public void testReadPortNumber() {
        String nameSettings = "settings.txt";
        int portNumberExpected = 23456;
        int portNumberResult = Client.readPortNumber();
        Assert.assertEquals(portNumberExpected, portNumberResult);
    }
}