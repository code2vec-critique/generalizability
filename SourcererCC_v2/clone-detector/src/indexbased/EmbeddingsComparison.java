package indexbased;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.*;
import java.util.*;

public class EmbeddingsComparison {

    public static Map<String, float[]> embeddings = new HashMap<>();
    public static int dims = 0;
    public static float[] empty;

    public static long hits;

    public static List<String> hitWords = new ArrayList<>();
    public static long misses;

    public static List<String> missWords= new ArrayList<>();

    public static boolean hasToken(String token) {
        if (!embeddings.containsKey(token)) {
            misses+=1;
//            missWords.add(token);
            if (misses >= Long.MAX_VALUE -5) {
                System.err.println("misses overflowed");
            }

            return false;
        }

        return true;
    }


    public static float[] vectorOf(String token) {
        if (embeddings.isEmpty() || dims == 0)  {
            throw new RuntimeException("initialise the embeddings first with readEmbeddings()! If you already did but still see this, it means init failed. ");
        }

        if (!embeddings.containsKey(token)) {
            throw new RuntimeException("should have checked first using hasToken!");
        }

        hits +=1 ;
//        hitWords.add(token);
        if (hits >= Long.MAX_VALUE -5) {
            System.err.println("hits overflowed");
        }

        return embeddings.get(token.toLowerCase());
    }

    public static  boolean cosineSimilarityExceedsThreshold(List<String> tokensA, List<String> tokensB, float threshold ) {
        Map<String, Integer> tokensASet = new HashMap<String, Integer>();
        Map<String, Integer> tokensBSet = new HashMap<String, Integer>();

        for (String tok : tokensA) {

            if (!tokensASet.containsKey(tok)) {
                tokensASet.put(tok, 0);
            }
            tokensASet.put(tok, tokensASet.get(tok) + 1);
        }
        for (String tok : tokensB) {

            if (!tokensBSet.containsKey(tok)) {
                tokensBSet.put(tok, 0);
            }
            tokensBSet.put(tok, tokensBSet.get(tok) + 1);
        }

        return cosineSimilarityExceedsThreshold(tokensASet.entrySet(), tokensBSet.entrySet(), threshold);
    }

    public static  boolean cosineSimilarityExceedsThreshold(Set<Map.Entry<String, Integer>> tokensA, Set<Map.Entry<String, Integer>> tokensB, float threshold ) {
        // TODO evaluate this and see if the speed is acceptable. May need to use some library using native call instead
        float[] sumA = sum(tokensA);


//        System.err.println("sum(tokensA) took: " + (System.nanoTime() - start) + " nanoseconds");

        float[] sumB = sum(tokensB);

//        System.err.println("sum(tokensB) took: " + (System.nanoTime() - start) + " nanoseconds");

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < sumA.length; i++) {
            dotProduct += sumA[i] * sumB[i];
            normA += sumA[i] * sumA[i];
            normB += sumB[i] * sumB[i];
        }

//        System.err.println("CosineSimilarity took: " + (System.nanoTime() - start) + " nanoseconds");
        double value = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)) * 1000; // for some reason SearchManager.th is 700
//        System.err.println("CosineSimilarity : " + value);
//        System.err.println(tokensA);System.err.println(tokensB);
        return value > threshold;
    }


    public static float[] sum(Set<Map.Entry<String, Integer>> tokens) {
        float[] sum = new float[dims];
        for (Map.Entry<String, Integer> token : tokens) {
            float[] vector = vectorOf(token.getKey());
            for (int i = 0; i < vector.length; i++) {
                sum[i] = sum[i] + token.getValue() * vector[i];
            }
        }

//        for (int i = 0; i < dims; i++) {
//            sum[i] = sum[i] / tokens.size();
//        }
        return sum;
    }


    public static void readEmbeddings() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader("/Users/kanghongjin/repos/gen_nn_w_embeddings/vectors_lower_parseable.txt")
//                new FileReader("/Users/kanghongjin/repos/code2vec/models/java14_model/tokens.txt")

        )) {

            String metadata = reader.readLine();
            int dimensions = Integer.parseInt(metadata.split(" ")[1]);

            String line;
            int j = 0;
            while ((line = reader.readLine()) != null) {
                String[] splitted = line.split(" ");
                String token = splitted[0].intern();
                float[] vector = new float[dimensions];
                try {
                    for (int i = 0; i < dimensions; i++) {
                        vector[i] = Float.parseFloat(splitted[i + 1]);
                    }

                    embeddings.put(token, vector);
                    j++;
                } catch (ArrayIndexOutOfBoundsException aioobe) {
                    continue;
                }
            }

            System.out.println("embeddings vocab " + j);

            dims = dimensions;

            empty = new float[dimensions];
            for (int i = 0; i < dims; i++) {
                empty[i] = 0.0f;
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String... args) {
        // test speed here
        EmbeddingsComparison.readEmbeddings();
        System.out.println("read");
//
//        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
//                Sets.newHashSet("isPrintable", "getQueueInfo", "getBodyAsString"),
//                Sets.newHashSet("jastadd", "getAddressValue", "banded"),
//                0.7f);
//        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
//                Sets.newHashSet("isPrintable", "getQueueInfo", "getBodyAsString"),
//                Sets.newHashSet("jastadd", "getAddressValue", "banded"),
//                0.7f);
//        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
//                Sets.newHashSet("isPrintable", "getQueueInfo", "getBodyAsString"),
//                Sets.newHashSet("jastadd", "getAddressValue", "banded"),
//                0.7f);
//        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
//                Sets.newHashSet("isPrintable", "getQueueInfo", "getBodyAsString", "isPrintable", "getQueueInfo", "getBodyAsString", "isPrintable", "getQueueInfo", "getBodyAsString", "isPrintable", "getQueueInfo", "getBodyAsString"),
//                Sets.newHashSet("jastadd", "getAddressValue", "banded", "isPrintable", "getQueueInfo", "getBodyAsString", "isPrintable", "getQueueInfo", "getBodyAsString"),
//                0.7f);
//        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
//                Sets.newHashSet("isPrintable", "getQueueInfo", "getBodyAsString", "isPrintable", "getQueueInfo", "getBodyAsString", "isPrintable", "getQueueInfo", "getBodyAsString", "isPrintable", "getQueueInfo", "getBodyAsString"),
//                Sets.newHashSet("jastadd", "getAddressValue", "banded", "isPrintable", "getQueueInfo", "getBodyAsString", "isPrintable", "getQueueInfo", "getBodyAsString"),
//                0.7f);

        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
                Lists.newArrayList("Serializable"),
                Lists.newArrayList("oggetto"),
                0.7f);

        System.out.println(EmbeddingsComparison.hits);
        System.out.println(EmbeddingsComparison.misses);
        System.out.println(EmbeddingsComparison.hitWords);
        System.out.println(EmbeddingsComparison.missWords);
        EmbeddingsComparison.hitWords.clear();
        EmbeddingsComparison.missWords.clear();


        List<String> a = Lists.newArrayList("Serializable", "sess", "ArrayList", "0xFFFF0000", "0xFF000000", "String", "path", "makeMinimumLengthString", "getCommonPrefix", "outStr", "quote", "0000", "IllegalArgumentException", "0x0000FFFF", "outArr", "Copying", "addSeperator", "getFormattedSize", "printStackTrace", "FILE_EXTENSION", "%", "an", "PropertyVetoException", "0", "1", "contains", "2", "at", "3", "4", "size", "util", "8", "ObjectProxy", "Arrays", "removeDelimiters", "AudioUtilities", "lowDiff", "bc", "compare", "be", "Matcher", "DecimalFormat", "MAX_VALUE", "br", "Entry", "same", "strs", "addSeparator", "T", "filterWaveFiles", "parseDoubles", "by", "applyToByteArray", "msbi", "sep", "_", "ubyte", "b", "valueStrings", "c", "d", "rtWinAxes", "e", "f", "zDispose", "h", "i", "j", "e1", "l", "sample", "m", "n", "o", "p", "getMenuComponents", "swing", "presets", "s", "t", "throw", "removeFirstPattern", "v", "sampleDescriptor", "w", "getWindowFadeIn", "hasExtension", "toString", "getShortestLength", "0xFF0000", "getName", "E4", "Comparator", "zdispose", "correctly", "javax", "intersection", "superCmdObjects", "ZCommand", "ZUtilities", "List", "concatZCommands", "Collections", "getLastToken", "ec", "setVisible", "add", "static", "wavFiles", "Color", "count", "pcmsolutions", "EMU", "constrainDimension", "ReadableSample", "highFade", "setContentPane", "final", "private", "outObjs", "for", "replaceAll", "actionPerformed", "getRed", "end", "Preferences", "Component", "class", "dlg", "getByteString", "extractChars", "BackingStoreException", "false", "length", "i2", "getContentPane", "subSequence", "prefs", "instanceof", "makeStringArray", "makeTaggedField", "clone", "eliminateDuplicates", "hb", "hiword", "JMenu", "charAt", "append", "deleteCharAt", "JPanel", "closePath", "getRootPane", "number", "else", "toArray", "setDefaultCloseOperation", "SOUTH", "text", "if", "0xFF", "io", "byte", "index", "hasNext", "childrenNames", "getGreen", "system", "field", "nextToken", "getExternalName", "invalid", "gui", "arrays", "Map", "getKeyWinAxes", "applyToCharArray", "l1", "l2", "tokenizeIntegers", "l3", "tok", "file", "too", "ncp", "getHighWindowShape", "max", "break", "menu", "off", "applyHideButton", "StringTokenizer", "joinIntegerArrays", "equals", "filler", "return", "getRealObjects", "com", "eobjs", "HashMap", "ActionEvent", "compareTo", "while", "clearSubtree", "0x00FF", "loword", "high", "copies", "mc", "find", "castToIntArray", "numNulls", "lastVal", "indexOf", "castToByteArray", "new", "extractUnsignedInt", "o1", "o2", "void", "divisible", "entrySet", "toLowerCase", "filesExist", "sort", "0xFF00", "fill", "throws", "Double", "setIcon", "quantify", "null", "velWinAxes", "iFrame", "grabFocus", "true", "Collection", "nh", "dispose", "try", "position", "JInternalFrame", "File", "getRealObject", "maxLength", "no", "np", "postfixString", "JButton", "nw", "extractFirst", "inRange", "firstOfPairs", "prefixPath", "substring", "integerPairs", "parseDouble", "of", "arr2", "arr1", "arr3", "or", "src", "numericValueStrings", "getWindowFadeOut", "bytes", "getLowWindowShape", "makeSampleSummary", "outFiles", "CENTER", "isInstance", "removeAll", "iterator", "java", "len", "isMapContentsSerializable", "height", "WAV_EXTENSION", "ext", "12", "allCmds", "applyBytes", "16", "getText", "obj2", "pack", "str", "node", "regex", "zDisposeCollection", "127", "128", "ReadablePreset", "must", "device", "lfy", "lfx", "24", "replaceFirst", "SampleDescriptor", "sa", "sb", "fillIncrementally", "256", "min", "createIdentityMap", "pos", "arraycopy", "audio", "sp", "DISPOSE_ON_CLOSE", "kids", "arr", "getParent", "lhc", "objs", "expandList", "getFormattedDurationInSeconds", "StringBuffer", "to", "applyBytesAsChar", "chars", "moveTo", "getRTWinAxes", "dest", "bigEndian", "barr", "getNearestDoubleIndex", "BorderLayout", "replaceExtension", "extractClassOfObjects", "segment", "getParentFile", "extractIndexes", "AbstractAction", "getKey", "invert", "eliminateInstances", "howManyFilesExist", "formatIndex", "appendArray", "extract", "name", "Class", "JDialog", "next", "extractOneOfIntegerPairs", "GeneralPath", "import", "show", "detokenizeIntegers", "lowFade", "System", "HIDE_ON_CLOSE", "indexes", "common", "Iterator", "setDefaultButton", "sortSubMenus", "HideIcon", "0x0000FF00", "comps", "apply", "hibyte", "0x000000FF", "parseInt", "Integer", "lastIndexOf", "getValue", "indexFormat", "short", "getLengthInSampleFrames", "getInstance", "base", "awt", "getFileCountForPattern", "midy", "midx", "getVelWinAxes", "getChannelDescription", "geom", "type", "put", "getWindowShape", "extractTaggedField", "getPath", "assertSameLength", "aligned", "catch", "val", "removeAllPattern", "double", "getFormattedSampleRateInKhz", "matcher", "samples", "exist", "doubles", "hfy", "elementCount", "Object", "hfx", "highDiff", "hasMoreTokens", "preset", "specified", "disposeOnHide", "ContextLocation", "low", "beans", "get", "event", "Pattern", "getExtension", "makeExactLengthString", "Dimension", "makeTag", "0xFFFF", "IntPool", "stripPatternFromFiles", "char", "width", "exists", "keyWinAxes", "getIndexForString", "small", "getIndex", "data", "subMenus", "html", "from", "tag", "ZDisposable", "lobyte", "package", "0x00FF0000", "format", "constrain", "getBlue", "illegal", "both", "cmdObjects", "files", "getWindowMiddle", "castToCharArray", "filesThatExist", "STRING_FIELD_SEPERATOR", "lineTo", "database", "public", "array", "DOT_POSTFIX", "delim", "makeCopyingString", "setResizable", "intValue", "clear", "tagIdentifier", "Exception", "int", "boolean", "postFix", "stripExtension");
        List<String> b = Lists.newArrayList("getdestinatario", "oggetto", "alice", "String", "put", "java", "MimeMessage", "else", "rip", "metodo", "catch", "if", "ext", "mittente", "byte", "it", "getceck", "RecipientType", "props", "0", "util", "Transport", "getbox", "@", "prova", "out", "getcorpo2", "getDefaultInstance", "getcorpo1", "pos", "extv", "event", "a", "smtp", "getoggetto", "e", "setFrom", "f", "getcorpo", "i", "m", "n", "MailFrame2", "setRecipient", "TO", "return", "setSubject", "mail", "ActionEvent", "isp", "dest", "toAddress", "getBytes", "javax", "host", "ng1", "getmittente", "new", "void", "mitt1", "test", "ope", "mitt2", "this", "throws", "fastweb", "ActionListener", "null", "try", "send", "internet", "implements", "InternetAddress", "Message", "import", "session", "for", "Properties", "substring", "getoperator", "ngi", "actionPerformed", "public", "array", "AzioneMail", "fromAddress", "class", "length", "message", "Exception", "int", "parseInt", "Integer", "tiscali", "libero", "Session", "setText", "awt");

//        a.removeIf(item -> item.length() <= 2);
//        b.removeIf(item -> item.length() <= 2);

        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
                a,
        b, 0.7f);

        System.out.println(EmbeddingsComparison.hits);
        System.out.println(EmbeddingsComparison.misses);
        System.out.println(EmbeddingsComparison.hitWords);
        System.out.println(EmbeddingsComparison.missWords);
        EmbeddingsComparison.hitWords.clear();
        EmbeddingsComparison.missWords.clear();

        ArrayList<String> a1 = Lists.newArrayList("READBUFSIZE", "OPEN_DELETE", "jar_url", "JarFileCache", "jar_entry", "doInput", "entry_name", "jar_file", "Us", "getJarFileURL", "useCaches", "OPEN_READ", "En", "guessContentTypeFromName", "getEntryName", "unquote",
                "tJarEntry", "EEE", "getJarFile", "Unix", "urlconn", "hh", "couldn", "MMM", "ProtocolException", "GMT", "dateFormat", "getHeaderField", "getLastModified", "JarURLConnection", "jf", "getEntry", "getProtocol",
                "th", "fn", "JarFile", "JarEntry", "modified", "createTempFile", "gnu", "connected", "yyyy", "Can", "mm", "ZipFile", "dd", "ss", "Locale", "last", "getSize", "protocol", "getFile", "SimpleDateFormat", "cache", "getTime", "No",
                "ection", "openConnection", "connect", "Connection", "fos", "MalformedURLException", "open", "1024", "jar", "Hashtable", "field", "getInputStream", "found", "Long", "zip", "getClass", "be", "content", "Date", "synchronized",
                "sts", "FileOutputStream", "entry", "format", "len", "super", "extends", "buf", "text", "InputStream", "package", "type", "4", "f", "is", "URL", "read", "put", "getName", "net", "long", "url", "t", "toString", "while", "protected", "write",
                "lose", "get", "equals", "throw", "file", "util", "class", "io", "true", "boolean", "false", "byte", "IOException", "throws", "try", "File", "catch", "length", "for", "else", "final", "e", "static", "this", "void", "java", "private", "1", "null",
                "return", "int", "0", "import", "public", "String", "if", "new");

        ArrayList<String> b1 = Lists.newArrayList("getStore", "Store", "String", "setHeader", "put", "getFolder", "password", "java", "MimeMessage", "else", "record", "text", "catch", "if", "addBodyPart", "msgs", "successfully", "BCC", "mbp2", "printStackTrace", "mbp1", "in", "io",
                "getProperties", "Mailer", "HOLDS_MESSAGES", "mailer", "RecipientType", "setSentDate", "props", "0", "exit", "1", "mailbox", "folder", "util", "passwd", "Transport", "InetAddress", "setContent", "bcc", "H", "Folder", "L", "M", "sb",
                "ut", "P", "prot", "file", "T", "U", "get", "X", "attach", "connect", "CC", "cc", "a", "Usage", "b", "smtp", "address", "c", "err", "d", "break", "e", "setFrom", "f", "store", "IOException", "appendMessages", "url", "n", "o", "StringBuffer", "s", "t", "nMail", "equals", "BufferedReader", "exists", "toString", "TO", "to", "return", "msg", "setSubject", "addresses", "attachFile", "mail", "subject", "optind", "main", "while", "protocol", "MimeMultipart", "javax", "host", "create", "from", "To", "net", "new", "mp", "static", "void", "msgsend", "throws", "sent", "Can", "mailhost", "null", "true", "try", "collect", "send", "internet", "InternetAddress", "Message", "import", "session", "line", "URLName", "for", "Properties", "setRecipients", "setDebug", "argv", "System", "println", "flush", "public", "class", "debug", "InputStreamReader", "was", "false", "length", "urln", "transport", "parse", "recorded", "readLine", "Subject", "Date", "Exception", "int", "print", "Mail", "boolean", "getInstance", "MimeBodyPart", "user", "append", "Session", "startsWith", "setText");
        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
               a1 ,
               b1 , 0.7f);
        System.out.println(EmbeddingsComparison.hits);
        System.out.println(EmbeddingsComparison.misses);
        System.out.println(EmbeddingsComparison.hitWords);
        System.out.println(EmbeddingsComparison.missWords);

        ArrayList<String> intersect1 = new ArrayList<>(a1);
        intersect1.retainAll(b1);
        System.out.println(intersect1);
        intersect1.retainAll(EmbeddingsComparison.hitWords);
        System.out.println(intersect1);
        System.out.println(EmbeddingsComparison.hitWords.size());
        System.out.println(intersect1.size());
        System.out.println(a1.size());
        System.out.println(b1.size());
        System.out.println("=====");

        EmbeddingsComparison.hitWords.clear();
        EmbeddingsComparison.missWords.clear();

        ArrayList<String> a2 = Lists.newArrayList("void", "f", "int[]", "array", "{", "boolean", "swapped", "=", "true;", "for", "(int", "i", "=", "0;", "i", "<", "array.length", "&&", "swapped;", "i++)", "{", "swapped", "=", "false;", "for", "(int", "j", "=", "0;", "j", "<", "array.length", "-", "1", "-", "i;", "j++)", "{", "if", "array", "j", ">", "array", "j", "1", "{", "int", "temp", "=", "array[j];", "array[j]", "=", "array", "j", "array", "j", "1", "temp", "swapped", "=", "true", "}", "}", "}}");
        ArrayList<String> b2 = Lists.newArrayList("for", "(", "Object", "elem", ":", "this", ".", "elements", ")", "{", "if", "(", "elem", ".", "equals", "(", "target", ")", ")", "{", "return", "true;", "}", "}", "return", "false;");
        EmbeddingsComparison.cosineSimilarityExceedsThreshold(
                a2,
                b2, 0.7f);
        System.out.println(EmbeddingsComparison.hits);
        System.out.println(EmbeddingsComparison.misses);
        System.out.println(EmbeddingsComparison.hitWords);
        System.out.println(EmbeddingsComparison.missWords);

        ArrayList<String> intersect2 = new ArrayList<>(a2);
        intersect2.retainAll(b2);
        System.out.println(intersect2);
        intersect2.retainAll(EmbeddingsComparison.hitWords);
        System.out.println(intersect2);
        System.out.println(EmbeddingsComparison.hitWords.size());
        System.out.println(intersect2.size());
        System.out.println(a2.size());
        System.out.println(b2.size());
        System.out.println("=====");


        EmbeddingsComparison.hitWords.clear();
        EmbeddingsComparison.missWords.clear();



        System.out.println("end");
    }
}
