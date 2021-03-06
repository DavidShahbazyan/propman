import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author David.Shahbazyan
 * @since May 17, 2017
 */
public class ISO_8895_1_TO_UTF_8 {
    public static void main(String[] args) throws Exception {
        File in = new File("C:\\Users\\David.Shahbazyan\\Desktop\\messages_es_PE.properties");
        File out = new File("C:\\Users\\David.Shahbazyan\\Desktop\\_messages_es_PE.properties");

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(in), StandardCharsets.ISO_8859_1));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8));

        String line;

        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
        }

        reader.close();
        writer.close();
    }
}
