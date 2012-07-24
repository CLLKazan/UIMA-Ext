/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import java.lang.Character.UnicodeBlock;
import java.util.regex.Pattern;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class WordUtils {

	static boolean isRussianWord(String token) {
		if (token.isEmpty()) {
			return false;
		}
		Character lastLetter = null;
		// find last letter
		for (int i = token.length() - 1; i >= 0; i--) {
			char ch = token.charAt(i);
			if (Character.isLetter(ch)) {
				lastLetter = ch;
				break;
			}
		}
		if (lastLetter == null) {
			return false;
		}
		// check is it cyrillic
		return UnicodeBlock.of(lastLetter).equals(UnicodeBlock.CYRILLIC);
	}

	static String normalizeToDictionaryForm(String str) {
		str = unicodeMarksPattern.matcher(str).replaceAll("");
		str = str.trim().toLowerCase();
		// str = StringUtils.replaceChars(str, "ёЁ", "еЕ");
		return str;
	}

	private static Pattern unicodeMarksPattern = Pattern.compile("[\\p{Mc}\\p{Me}\\p{Mn}]");

	private WordUtils() {
	}

}