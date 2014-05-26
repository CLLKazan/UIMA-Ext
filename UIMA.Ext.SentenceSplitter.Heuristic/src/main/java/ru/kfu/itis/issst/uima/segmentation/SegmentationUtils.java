package ru.kfu.itis.issst.uima.segmentation;

import java.util.Set;

import org.apache.uima.cas.text.AnnotationFS;

import ru.kfu.cll.uima.tokenizer.fstype.Token;

import com.google.common.collect.ImmutableSet;
import static ru.kfu.cll.uima.tokenizer.TokenUtils.*;

public class SegmentationUtils {

	private static final Set<String> undirectedQMs = ImmutableSet.of(
			"\"", "'", "\u201A", "\u201B", "\u201E", "\u201F", "\uFF02", "\uFF07");
	private static final Set<String> leftQMs = ImmutableSet.of(
			"«", "\u2018", "\u201C", "\u2039");
	private static final Set<String> rightQMs = ImmutableSet.of(
			"»", "\u2019", "\u201D", "\u203A");

	public static boolean isLeftQuoted(Token token) {
		Token tokenBefore = getTokenBefore(token);
		return tokenBefore != null && isLeftQM(tokenBefore);
	}

	public static boolean isRightQuoted(Token token) {
		Token tokenAfter = getTokenAfter(token);
		return tokenAfter != null && isRightQM(tokenAfter);
	}

	public static boolean isLeftQM(AnnotationFS anno) {
		String chars = anno.getCoveredText();
		return undirectedQMs.contains(chars) || leftQMs.contains(chars);
	}

	public static boolean isRightQM(AnnotationFS anno) {
		String chars = anno.getCoveredText();
		return undirectedQMs.contains(chars) || rightQMs.contains(chars);
	}

	private SegmentationUtils() {
	}

	public static void main(String[] args) {
		System.out.println("Undirected:\n" + undirectedQMs);
		System.out.println("Left:\n" + leftQMs);
		System.out.println("Right:\n" + rightQMs);
	}
}