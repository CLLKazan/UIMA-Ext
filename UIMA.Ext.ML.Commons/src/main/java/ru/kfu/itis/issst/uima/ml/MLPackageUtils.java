package ru.kfu.itis.issst.uima.ml;

/**
 * Package private utils.
 *
 * @author Rinat Gareev
 */
class MLPackageUtils {
    static boolean isNullLabel(String arg) {
        return arg == null || arg.isEmpty() || "null".equalsIgnoreCase(arg);
    }
}
