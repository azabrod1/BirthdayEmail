import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    List<String> data;

    public FileManager(String file){

        try {
            data = Files.readAllLines(Paths.get(file));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    List<String> getData(){
        return new ArrayList<>(data);
    }
}
