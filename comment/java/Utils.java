import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {


    public enum Mode {
        TRAIN, VALID, TEST
    }
    public static final Mode mode = Mode.TEST;

    public static List<ASTNode> getChildren(ASTNode node) {
        List<ASTNode> children = new ArrayList<ASTNode>();
        List list = node.structuralPropertiesForType();

        if (node instanceof Block) {
            Block block = (Block) node;
            List stmts = block.statements();


            for (Object item : stmts) {
                if (item instanceof  ASTNode) {
                    children.add((ASTNode) item);
                }
            }
        }

        for (int i = 0; i < list.size(); i++) {
            Object child = node.getStructuralProperty((StructuralPropertyDescriptor)list.get(i));
            if (child instanceof ASTNode) {
                children.add((ASTNode) child);
            }
        }
        return children;
    }

    public static boolean isTerminal(ASTNode node) {
        return getChildren(node).isEmpty() && !(node instanceof Block);
    }

    public static boolean shouldProcess(ASTNode node) {
        return !(node instanceof  CompilationUnit
                || node instanceof TypeDeclaration
                || (node instanceof MethodDeclaration && !((MethodDeclaration)node).isConstructor())
                || (node instanceof SimpleName && node.toString().contains("DUMMYVALUEFOR_DEEPCOM")));
    }

    public static boolean isAcceptableComment(String comment) {
        boolean retVal = comment.split(" ").length > 1;
        if (!retVal) {
            System.err.println("failure by comment length: " +comment);
        }
        return retVal;
    }

    public static boolean isAcceptableMethod(MethodDeclaration method) {
        SimpleName methodName = method.getName();
        boolean prefixedWithGet = methodName.getIdentifier().startsWith("get");
        boolean prefixedWithSet = methodName.getIdentifier().startsWith("set");
        boolean prefixedOrSuffixedWithTest = methodName.getIdentifier().startsWith("test") || methodName.getIdentifier().endsWith("Test");


        boolean isCtor = method.isConstructor() || (Character.isUpperCase(methodName.getIdentifier().charAt(0)) && !methodName.getIdentifier().contains("_"));

        List stmts = method.getBody().statements();
        boolean onlyOneLine = stmts.size() <= 1;

        boolean isGetterOrSetter = onlyOneLine && (prefixedWithGet || prefixedWithSet);

        if (isGetterOrSetter || prefixedOrSuffixedWithTest || isCtor) {
//            System.err.println("unacceptable?");
            System.err.println("failure by method :" + methodName.getIdentifier() + " getterSetter:" + isGetterOrSetter +
                    " isTest:" + prefixedOrSuffixedWithTest + " isCtor:" + isCtor);

        }
//        else {
//            System.err.println("successful method :" + methodName.getIdentifier() + " getterSetter:" + isGetterOrSetter +
//                    " isTest:" + prefixedOrSuffixedWithTest + " isCtor:" + isCtor);
//        }

        return !(isGetterOrSetter || prefixedOrSuffixedWithTest || isCtor);
    }

    public static ASTNode normalize(ASTNode node) {
        if (node instanceof StringLiteral) {
            StringLiteral stringLiteral = (StringLiteral) node;
            stringLiteral.setLiteralValue("<STR>");
            return stringLiteral;
        }
        if (node instanceof NumberLiteral){
            NumberLiteral numberLiteral = (NumberLiteral) node;
            numberLiteral.setToken("99");
            return numberLiteral;
        }
        return node;
    }


    public static String terminalName(ASTNode node, Set<String> commonTokens) {
        String typeName = ASTNode.nodeClassForType(node.getNodeType()).getSimpleName();

        // add toLowerCase here and not below.
        String originalNodeName = typeName + "_" + node.toString();
        String lowerCasedNodeName = typeName + "_" + node.toString().toLowerCase();
        String nodeNameSubtokened = typeName + "_" + String.join( "|", splitToSubtokens(node.toString()).toArray(new String[]{}));
        if (commonTokens.contains(originalNodeName)) {
            return lowerCasedNodeName;
        } else {
            return typeName;
        }
    }

    public static String subtokenDelimited(String str) {
        return String.join( "|", splitToSubtokens(str).toArray(new String[]{}));
    }

    public static String terminalName(ASTNode node) {
        // no need to lowercase here.
        String typeName = ASTNode.nodeClassForType(node.getNodeType()).getSimpleName();

        return typeName + "_" + node.toString();

    }

    public static String nonTerminalName(ASTNode node) {
        return ASTNode.nodeClassForType(node.getNodeType()).getSimpleName();
    }

    public static ArrayList<String> splitToSubtokens(String str1) {
        String str2 = str1.trim();
        return Stream.of(str2.split("(?<=[a-z])(?=[A-Z])|_|[0-9]|(?<=[A-Z])(?=[A-Z][a-z])|\\s+"))
                .filter(s -> s.length() > 0).map(s -> s.toLowerCase())
                .filter(s -> s.length() > 0).collect(Collectors.toCollection(ArrayList::new));
    }

    public static String getFilteredJsonPath() {
        if (mode == Mode.TRAIN) {
            return Filepaths.FILTERED_JSON_TRAIN;
        } else if (mode == Mode.VALID) {
            return Filepaths.FILTERED_JSON_VALID;
        } else {
            return Filepaths.FILTERED_JSON_TEST;
        }
    }

    public static String getOriginalData() {
        if (mode == Mode.TRAIN) {
            return Filepaths.ORIGINAL_DATA_TRAIN;
        }  else if (mode == Mode.VALID) {
            return Filepaths.ORIGINAL_DATA_VALIDATION;
        }else {
            return Filepaths.ORIGINAL_DATA_TEST;
        }
    }

    public static String getFilteredTokenizedCommentsPath() {
        if (mode == Mode.TRAIN) {
        return Filepaths.FILTERED_TOKENIZED_COMMENTS_TRAIN;
        }  else if (mode == Mode.VALID) {
            return Filepaths.FILTERED_TOKENIZED_COMMENTS_VALIDATION;
        }else {
            return Filepaths.FILTERED_TOKENIZED_COMMENTS_TEST;
        }
    }

    public static String getTokenizedCommentsPath() {
        if (mode == Mode.TRAIN) {
            return Filepaths.TOKENIZED_COMMENTS_TRAIN;
        }  else if (mode == Mode.VALID) {
            return Filepaths.TOKENIZED_COMMENTS_VALIDATION;
        }else {
            return Filepaths.TOKENIZED_COMMENTS_TEST;
        }
    }

    public static String getOriginalDataPath() {

        if (mode == Mode.TRAIN) {
            return Filepaths.ORIGINAL_DATA_TRAIN;
        }  else if (mode == Mode.VALID) {
            return Filepaths.ORIGINAL_DATA_VALIDATION;
        }else {
            return Filepaths.ORIGINAL_DATA_TEST;
        }

    }

    public static String getExtractedCommentsPath() {


        if (mode == Mode.TRAIN) {
            return Filepaths.EXTRACTED_COMMENTS_TRAIN;
        }  else if (mode == Mode.VALID) {
            return Filepaths.EXTRACTED_COMMENTS_VALIDATION;
        }else {
            return Filepaths.EXTRACTED_COMMENTS_TEST;
        }
    }
}
