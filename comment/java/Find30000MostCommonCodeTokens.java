import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jdt.core.dom.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Find30000MostCommonCodeTokens {

    public static void main(String... args) throws Exception {

        if (Utils.mode != Utils.Mode.TRAIN) {
            System.err.println("should not override common tokens file in non-training mode");
            throw new Exception("blah");
        }
        Map<String, Integer> counter = new HashMap<>();

        Path path = Paths.get(Utils.getFilteredJsonPath());
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(s -> {
                JsonObject object = new JsonParser().parse(s).getAsJsonObject();
                String code = object.get("code").getAsString();
                List<String> tokens = parseLine(code);

                tokens.stream().forEach(token -> {
                    if (!counter.containsKey(token)) {
                        counter.put(token, 0);
                    }
                    counter.put(token, counter.get(token) + 1);
                });
            });


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        List<String> sorted = counter.entrySet().stream()
                .sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
                .limit(30_000) // 2 tokens already reserved for ( and ). tensorflow nmt will add 3 tokens on its own
                .map(pair -> pair.getKey())
                .collect(Collectors.toList());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("most_common.txt"))) {
            for (String token : sorted) {
                System.out.println(token);
                writer.write(token);
                writer.write("\n");
            }
            writer.write('(');
            writer.write('\n');
            writer.write(')');
            writer.write('\n');
        }

    }



    public static List<String> parseLine(String line) {
        line = "public class A {" + line + "}";

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(line.toCharArray());
        final CompilationUnit block = (CompilationUnit) parser.createAST(null);

        List<String> result = new ArrayList<>();
        block.accept(new ASTVisitor() {

            @Override
            public void preVisit(ASTNode node) {
                if (!Utils.shouldProcess(node)) {
                    return;
                }
                node = Utils.normalize(node);

                String token;
                if (Utils.isTerminal(node)) {
                    token = Utils.terminalName(node);

                } else {
                    token = Utils.nonTerminalName(node);
                }

                result.add(token);

            }
        });

        return result;
    }
}
