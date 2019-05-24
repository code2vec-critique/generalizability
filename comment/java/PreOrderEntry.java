import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class PreOrderEntry {
    public static void main(String... args) {

        Set<String> commonTokens = new HashSet<>();

        // read most common tokens
        try (BufferedReader reader = new BufferedReader(new FileReader("most_common.txt"))) {
            reader.lines().forEach(line -> {
                commonTokens.add(line);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Path path = Paths.get(Utils.getFilteredJsonPath());
        try (Stream<String> lines = Files.lines(path);
             BufferedWriter writer = new BufferedWriter(new FileWriter("preorder" + Utils.mode.toString() + ".txt"))) {
            lines.forEach(s -> {
                JsonObject object = new JsonParser().parse(s).getAsJsonObject();
                String code = object.get("code").getAsString();
                parseLine(code, commonTokens, writer);
                try {
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void parseLine(String line, Set<String> commonTokens, Writer writer) {

        line = "class DUMMYVALUEFOR_DEEPCOM {" + line + "}";

        ASTParser parser = ASTParser.newParser(AST.JLS3);

        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(line.toCharArray());
        final CompilationUnit block = (CompilationUnit) parser.createAST(null);

        block.accept(new ASTVisitor() {
            @Override
            public boolean visit(CompilationUnit node) {
                return true;
            }

            @Override
            public boolean visit(MethodDeclaration node) {
                    return true;
            }

            @Override
            public void preVisit(ASTNode node) {
                if (!Utils.shouldProcess(node)) {
                    return;
                }
                node = Utils.normalize(node);

                try {
                    if (Utils.isTerminal(node)) {
                        String nodeString = Utils.terminalName(node, commonTokens);
                        writer.write(nodeString + " ");
                    } else {
                        writer.write(Utils.nonTerminalName(node) + " ");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

}
