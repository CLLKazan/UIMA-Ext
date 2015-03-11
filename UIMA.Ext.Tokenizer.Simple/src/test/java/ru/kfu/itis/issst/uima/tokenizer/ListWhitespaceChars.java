package ru.kfu.itis.issst.uima.tokenizer;

/**
 * @author Rinat Gareev
 */
public class ListWhitespaceChars {
    public static void main(String args[]) {
        System.out.println("Code\tWhite\tCategory");
        for (int i = 0x0000; i <= 0xFFFF; i++) {
            if (Character.isWhitespace(i) || isControl(i) || isSep(i)) {
                System.out.println(format(i));
            }
        }
    }

    private static String getCharType(int ch) {
        int chType = Character.getType(ch);
        switch (chType) {
            case Character.SPACE_SEPARATOR:
                return "SPACE_SEPARATOR";
            case Character.LINE_SEPARATOR:
                return "LINE_SEPARATOR";
            case Character.PARAGRAPH_SEPARATOR:
                return "PARAGRAPH_SEPARATOR";
            default:
                return String.valueOf(chType);
        }
    }

    private static boolean isControl(int ch) {
        return Character.CONTROL == Character.getType(ch);
    }

    private static boolean isSep(int ch) {
        switch (Character.getType(ch)) {
            case Character.SPACE_SEPARATOR:
            case Character.LINE_SEPARATOR:
            case Character.PARAGRAPH_SEPARATOR:
                return true;
            default: return false;
        }
    }

    private static String format(int codePoint) {
        return String.format("U%04x\t%s\t%s", codePoint,
                Character.isWhitespace(codePoint),
                getCharType(codePoint));
    }
}
