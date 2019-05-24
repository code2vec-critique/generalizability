import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jdt.core.dom.*;

import java.io.*;

/**
 * Run this after setting up the data already. i.e. after tokenizing comments, after generating AST sequences
 */
public class FilterBadCasesEntry {

    public static boolean isAcceptable = false;

    public static void main(String... args) throws IOException {
        try (BufferedReader commentReader = new BufferedReader(new FileReader(Utils.getTokenizedCommentsPath()));
             BufferedReader astReader = new BufferedReader(new FileReader(Utils.getOriginalData()));
             BufferedWriter commentWriter = new BufferedWriter(new FileWriter(Utils.getFilteredTokenizedCommentsPath()));
             BufferedWriter astWriter = new BufferedWriter(new FileWriter(Utils.getFilteredJsonPath()));
             ) {

            while (true) {
                String comment = commentReader.readLine();
                String ast = astReader.readLine();

                if (comment == null || ast == null) {
                    break;
                }

                isAcceptable = false; // reset "global" variable. Actually a static class variable, but its as good as global.

                JsonObject object = new JsonParser().parse(ast).getAsJsonObject();
                String code = object.get("code").getAsString();

                boolean isAcceptableNumberOfTokens = code.split(" ").length <= 100;
                boolean isAcceptableMethod = parseMethodName(code);

                boolean isAcceptableComment = Utils.isAcceptableComment(comment);

                if (isAcceptableNumberOfTokens && isAcceptableComment && isAcceptableMethod) {
                    commentWriter.write(comment);
                    commentWriter.write("\n");
                    astWriter.write(ast);
                    astWriter.write("\n");
                }

            }
        }
    }

   
   
   

    public static boolean parseMethodName(String line) {

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
                isAcceptable = Utils.isAcceptableMethod(node);
                return true;
            }


        });

        return isAcceptable;
    }
}
