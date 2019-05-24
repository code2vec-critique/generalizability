
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CommentPreProcessorEntry {
    public static void main(String... args) {


        Path path = Paths.get(Utils.getOriginalDataPath());
        try (Stream<String> lines = Files.lines(path);
            BufferedWriter writer = new BufferedWriter(new FileWriter(Utils.getExtractedCommentsPath()))) {
            lines.forEach(s -> {
                JsonObject object = new JsonParser().parse(s).getAsJsonObject();
                String comment = object.get("nl").getAsString().trim();
                comment = comment.replaceAll("<.*?>", ""); //non-greedy replace
                comment = comment.toLowerCase();

                try {
                    writer.write(comment + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
