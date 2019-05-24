
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

public class TokenizerPreprocessorEntry {
    public static boolean useSubtokenDelimiter = true; // true for c2v

    public static void main(String... args) {


        Path path = Paths.get(Utils.getFilteredJsonPath());

        try (Stream<String> lines = Files.lines(path);
            BufferedWriter writer = new BufferedWriter(new FileWriter("just_tokenize_" + Utils.mode.toString() + ".txt"));
            BufferedWriter writerLower = new BufferedWriter(new FileWriter("just_tokenize_" + Utils.mode.toString() + "_lower.txt"))) {
            lines.forEach(s -> {
                JsonObject object = new JsonParser().parse(s).getAsJsonObject();
                String code = object.get("code").getAsString();

//                parseLine(code, writer);

                try {
                    if (TokenizerPreprocessorEntry.useSubtokenDelimiter) {
                        code = code.replaceAll("\\b", " ").replaceAll("\n", " ").replaceAll("\\s+", " ");
                        String[] tokens = code.split(" ");
                        for (String token : tokens) {
                            String tokenString = Utils.subtokenDelimited(token);
                            writer.write(tokenString + " ");
                        }
                        writer.write("\n");
                    } else {

                        writer.write(
                                code.replaceAll("\\b", " ").replaceAll("\n", " ").replaceAll("\\s+", " ")
                        );
                        writerLower.write(
                                code.replaceAll("\\b", " ").replaceAll("\n", " ").replaceAll("\\s+", " ").toLowerCase()
                        );
                        writer.write("\n");
                        writerLower.write("\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void parseLine(String line, Writer writer) {
        String sourceStart = "class DUMMYVALUEFOR_DEEPCOM {";
        String sourceEnd = "}";

        line = sourceStart + line + sourceEnd;

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(line.toCharArray());

        parser.setKind(ASTParser.K_COMPILATION_UNIT);
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

                String nodeString = node.toString().toLowerCase().replaceAll("\n", "");
                if (TokenizerPreprocessorEntry.useSubtokenDelimiter) {
                    nodeString = Utils.subtokenDelimited(nodeString);
                    System.out.println(nodeString);
                }

                try {
                    writer.write(nodeString + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
