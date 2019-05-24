import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Experiment {
    public static void main(String... args) {
//        System.out.println(FilterBadCasesEntry.parseMethodName("public TabUma(Tab tab,TabCreationState creationState,TabModel model) {\n" +
//                "        mTab = tab;\n" +
//                "        mTabCreationState = creationState;\n" +
//                "        mTabModel = model;\n" +
//                "        mLastTabStateChangeMillis = System.currentTimeMillis();\n" +
//                "        if (mTabCreationState == TabCreationState.LIVE_IN_FOREGROUND || mTabCreationState == TabCreationState.FROZEN_ON_RESTORE) {\n" +
//                "            updateTabState(TAB_STATE_ACTIVE);\n" +
//                "        } else if (mTabCreationState == TabCreationState.LIVE_IN_BACKGROUND || mTabCreationState == TabCreationState.FROZEN_FOR_LAZY_LOAD) {\n" +
//                "            updateTabState(TAB_STATE_INACTIVE);\n" +
//                "        }\n" +
//                "    }"));


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
        String value = " public static byte[] bitmapToByte(Bitmap b){" +
                "    ByteArrayOutputStream o = new ByteArrayOutputStream();" +
                "    b.compress(Bitmap.CompressFormat.PNG,100,o);" +
                "    return o.toByteArray();" +
                "  }";

        parseLine(value, commonTokens);
    }

    public static void parseLine(String line, Set<String> commonTokens) {
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
                if (Utils.isTerminal(node)) {
                    String nodeString = Utils.terminalName(node, commonTokens);
                    System.out.println("( " + nodeString + " ) " + nodeString + " ");
                } else {
                    System.out.println("( " + Utils.nonTerminalName(node) + " ");
                }

            }

            @Override
            public void postVisit(ASTNode node) {
                if (!Utils.shouldProcess(node)) {
                    return;
                }

                if (!Utils.isTerminal(node)) {

                    System.out.println(") " + Utils.nonTerminalName(node) + " ");

                }
            }
        });
    }
}
