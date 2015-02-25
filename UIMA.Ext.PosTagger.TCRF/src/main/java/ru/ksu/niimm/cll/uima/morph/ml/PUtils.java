package ru.ksu.niimm.cll.uima.morph.ml;

/**
 * Package private utils.
 *
 * @author Rinat Gareev
 */
class PUtils {
    static boolean isNullLabel(String arg) {
        return arg == null || arg.isEmpty() || "null".equalsIgnoreCase(arg);
    }
}
