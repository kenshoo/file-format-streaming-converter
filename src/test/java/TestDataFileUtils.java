import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: liorh
 * Date: Dec 14, 2010
 * Time: 4:16:42 PM
 */
public class TestDataFileUtils {

    public static String getFilePath(Class<?> c, String fileName) {
        URL resource = c.getResource(fileName);
        if (resource != null && resource.getProtocol().equals("file")) {
            return resource.getPath();
        }
        throw new IllegalArgumentException("Couldn't find resource " + fileName + " relative to class " + c.getName()
                + ". Please modify your test to work with resources properly without relying on specific files locations.");
    }

}
