/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class RusCorporaXmlContentHandler extends DefaultHandler {

	private static final String E_BR = "br";
	private static final String E_ANA = "ana";
	private static final String E_WORD = "w";
	private static final String E_SENTENCE = "se";
	private static final String E_SPAN = "span";
	private static final String E_PARAGRAPH = "p";
	private static final String E_BODY = "body";
	private static final String E_HEAD = "head";
	private static final String E_HTML = "html";
	private static final Set<String> ELEMENTS_TO_IGNORE = ImmutableSet.of(
			"i", "b", "em", "formula", "u", "hr");
	private static final Set<String> ELEMENTS_TO_SKIP = ImmutableSet.of(
			"sup", "sub", "table");
	// config fields
	private Locator locator;
	// state fields
	private LinkedList<ElementTuple> elemStack = Lists.newLinkedList();
	private StringBuilder textBuilder;
	private boolean skipElements = false;
	private Integer paragraphStart;
	private LinkedList<RusCorporaAnnotation> paragraphs = Lists.newLinkedList();
	private Integer sentenceStart;
	private LinkedList<RusCorporaAnnotation> sentences = Lists.newLinkedList();
	private Integer wordStart;
	private LinkedList<RusCorporaWordform> wordforms = Lists.newLinkedList();
	private Integer lastWordEnd;
	private boolean readCharacters = false;
	// derived
	private boolean finished = false;
	private String text;

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
		if (text == null) {
			throw new IllegalStateException("Document does not have proper <body>");
		}
		finished = true;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		String elemName = getElementName(uri, localName, qName);
		elemStack.push(new ElementTuple(elemName));
		if (skipElements) {
			return;
		}
		if (E_HTML.equals(elemName)) {
			onHtmlStart();
		} else if (E_HEAD.equals(elemName)) {
			onHeadStart();
		} else if (E_BODY.equals(elemName)) {
			onBodyStart();
		} else if (E_PARAGRAPH.equals(elemName)) {
			onParagraphStart();
		} else if (E_SENTENCE.equals(elemName)) {
			onSentenceStart();
		} else if (E_WORD.equals(elemName)) {
			onWordStart();
		} else if (E_ANA.equals(elemName)) {
			onAnaStart(attributes);
		} else if (ELEMENTS_TO_IGNORE.contains(elemName)) {
			// simply avoid
		} else if (ELEMENTS_TO_SKIP.contains(elemName)) {
			skipCurrentElementContent();
		} else if (E_SPAN.equals(elemName)) {
			onSpanStart(attributes);
		} else if (E_BR.equals(elemName)) {
			onBrStart();
		} else {
			throw new IllegalStateException(String.format(
					"Unknown element '%s' at %s", elemName, getLocationString()));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String elemName = getElementName(uri, localName, qName);
		if (!skipElements) {
			if (E_HTML.equals(elemName)) {
				onHtmlEnd();
			} else if (E_HEAD.equals(elemName)) {
				onHeadEnd();
			} else if (E_BODY.equals(elemName)) {
				onBodyEnd();
			} else if (E_PARAGRAPH.equals(elemName)) {
				onParagraphEnd();
			} else if (E_SENTENCE.equals(elemName)) {
				onSentenceEnd();
			} else if (E_WORD.equals(elemName)) {
				onWordEnd();
			} else if (E_ANA.equals(elemName)) {
				onAnaEnd();
			} else if (ELEMENTS_TO_IGNORE.contains(elemName)) {
				// do nothing
			} else if (E_SPAN.equals(elemName)) {
				onSpanEnd();
			} else if (E_BR.equals(elemName)) {
				onBrEnd();
			} else {
				throw new IllegalStateException(String.format(
						"End of unknown element '%s' at %s", elemName, getLocationString()));
			}
		}
		ElementTuple elemTuple = elemStack.pop();
		if (!elemName.equals(elemTuple.elementName)) {
			throw new IllegalStateException();
		}
		for (ElementListener elemListener : elemTuple.listeners) {
			elemListener.onElementEnd(elemName);
		}
	}

	private String getElementName(String uri, String localName, String qName) {
		String elemName = localName;
		if (elemName.isEmpty()) {
			elemName = qName;
		}
		if (elemName.isEmpty()) {
			throw new IllegalStateException(String.format(
					"Could not determine element name at %s", getLocationString()));
		}
		return elemName.toLowerCase();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (skipElements) {
			return;
		}
		String newChars = String.valueOf(ch, start, length);
		if (!readCharacters) {
			if (!StringUtils.isBlank(newChars)) {
				throw new IllegalStateException(String.format(
						"Unexpected characters '%s' at %s",
						String.valueOf(ch, start, length), getLocationString()));
			}
		} else {
			textBuilder.append(preprocessChars(newChars));
		}
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		// do nothing
		/*
		if (skipElements) {
			return;
		}
		if (readCharacters) {
			String newChars = String.valueOf(ch, start, length);
			textBuilder.append(preprocessChars(newChars));
		}
		*/
	}

	private String preprocessChars(String str) {
		// normalize line endings
		return StringUtils.replaceChars(str, "\r\n", "");
	}

	private void ensureFinished() {
		if (!finished) {
			throw new IllegalStateException("Parsing is not finished");
		}
	}

	public String getDocumentText() {
		ensureFinished();
		return text;
	}

	public List<RusCorporaAnnotation> getParagraphs() {
		ensureFinished();
		return paragraphs;
	}

	public List<RusCorporaAnnotation> getSentences() {
		ensureFinished();
		return sentences;
	}

	public List<RusCorporaWordform> getWordforms() {
		ensureFinished();
		return wordforms;
	}

	private void onHtmlStart() {
		// no op
	}

	private void onHtmlEnd() {
		// no op
	}

	private void onHeadStart() {
		// ignore content of <head>
		skipCurrentElementContent();
	}

	private void onHeadEnd() {
		// do nothing - listener will set skipElements back to false
	}

	private void skipCurrentElementContent() {
		skipElements = true;
		// add listener waiting for end of current element
		elemStack.peek().listeners.add(new ElementListener() {
			@Override
			public void onElementEnd(String elementName) {
				skipElements = false;
			}
		});
	}

	private void onBodyStart() {
		if (textBuilder != null) {
			throw new IllegalStateException("Duplicate <body> element");
		}
		textBuilder = new StringBuilder();
		readCharacters = true;
	}

	private void onBodyEnd() {
		if (wordStart != null || sentenceStart != null || paragraphStart != null) {
			throw new IllegalStateException();
		}
		text = textBuilder.toString();
		textBuilder = null;
		readCharacters = false;
	}

	private void onParagraphStart() {
		if (paragraphStart != null) {
			throw new IllegalStateException(String.format(
					"Nested paragraphs at %s", getLocationString()));
		}
		if (textBuilder.length() > 0) {
			char lastChar = textBuilder.charAt(textBuilder.length() - 1);
			if (lastChar != '\n' && lastChar != '\r') {
				textBuilder.append('\n');
			}
		}
		paragraphStart = textBuilder.length();
	}

	private void onParagraphEnd() {
		if (paragraphStart == null) {
			throw new IllegalStateException();
		}
		int paragraphEnd = textBuilder.length();
		paragraphs.add(new RusCorporaAnnotation(paragraphStart, paragraphEnd));
		// clear
		paragraphStart = null;
	}

	private void onSentenceStart() {
		if (sentenceStart != null) {
			throw new IllegalStateException(String.format(
					"Nested sentences at %s", getLocationString()));
		}
		// add whitespace if required
		ensureTrailingWhitespace();
		sentenceStart = textBuilder.length();
	}

	private void ensureTrailingWhitespace() {
		if (textBuilder.length() > 0) {
			char lastChar = textBuilder.charAt(textBuilder.length() - 1);
			if (!Character.isWhitespace(lastChar)) {
				textBuilder.append(' ');
			}
		}
	}

	private void onSentenceEnd() {
		if (sentenceStart == null) {
			throw new IllegalArgumentException();
		}
		postprocessNotWordSpan();
		int sentenceEnd = textBuilder.length();
		sentences.add(new RusCorporaAnnotation(sentenceStart, sentenceEnd));
		// clear
		sentenceStart = null;
	}

	private void onWordStart() {
		if (wordStart != null) {
			throw new IllegalStateException(String.format(
					"Nested wordform at %s", getLocationString()));
		}
		ensureTrailingWhitespace();
		postprocessNotWordSpan();
		wordStart = textBuilder.length();
		RusCorporaWordform wf = new RusCorporaWordform(wordStart);
		wordforms.add(wf);
	}

	private void onWordEnd() {
		if (wordStart == null) {
			throw new IllegalStateException();
		}
		// post-process word string - remove stress-mark
		String wordStr = textBuilder.substring(wordStart);
		wordStr = postprocessWordString(wordStr);
		textBuilder.replace(wordStart, textBuilder.length(), wordStr);
		int wordEnd = textBuilder.length();
		wordforms.getLast().setEnd(wordEnd);
		lastWordEnd = wordEnd;
		// clear
		wordStart = null;
	}

	private String postprocessWordString(String str) {
		return StringUtils.remove(str, '`');
	}

	private void postprocessNotWordSpan() {
		// Not Word Span
		int nwSpanBegin = this.lastWordEnd == null ? 0 : this.lastWordEnd;
		// do not cross sentence boundaries
		nwSpanBegin = Math.max(nwSpanBegin, sentenceStart);
		String nwSpan = textBuilder.substring(nwSpanBegin);
		int origLength = nwSpan.length();
		// replace separate double hyphens by single em-dash
		nwSpan = nwSpan.replaceAll("([^-]|^)--([^-]|$)", "$1\u2014$2");
		if (nwSpan.length() != origLength) {
			textBuilder.replace(nwSpanBegin, textBuilder.length(), nwSpan);
		}
	}

	// split into lemma and wf grammems
	private static Splitter lwSplitter = Splitter.on('=').trimResults().omitEmptyStrings();
	private static Splitter grSplitter = Splitter.on(',').trimResults().omitEmptyStrings();

	private void onAnaStart(Attributes attrs) {
		RusCorporaWordform wf = wordforms.getLast();
		String lemma = requiredAttribute(attrs, "lex");
		wf.setLex(lemma);
		final String grAttrVal = requiredAttribute(attrs, "gr");
		{
			// parse 'gr' attribute value
			Iterator<String> grSets = lwSplitter.split(grAttrVal).iterator();
			if (!grSets.hasNext()) {
				throw new IllegalStateException(String.format(
						"Illegal gr argument value: '%s' at %s",
						grAttrVal, getLocationString()));
			}
			String grSetStr = grSets.next();
			Iterator<String> grs = grSplitter.split(grSetStr).iterator();
			if (!grs.hasNext()) {
				throw new IllegalStateException(String.format(
						"POS is not specified in '%s' at %s",
						grAttrVal, getLocationString()));
			}
			wf.setPos(grs.next());
			wf.setLexGrammems(Sets.newLinkedHashSet(Lists.newArrayList(grs)));
			if (grSets.hasNext()) {
				// wf grammems
				grSetStr = grSets.next();
				wf.setWordformGrammems(Sets.newLinkedHashSet(grSplitter.split(grSetStr)));
			} else {
				wf.setWordformGrammems(Sets.<String> newLinkedHashSet());
			}
		}
	}

	private String requiredAttribute(Attributes attrs, String name) {
		String val = attrs.getValue(name);
		if (val == null) {
			throw new IllegalStateException(String.format(
					"Missing attribute '%s' at %s", name, getLocationString()));
		}
		return val;
	}

	private void onAnaEnd() {
		// nothing
	}

	private void onSpanStart(Attributes attrs) {
		if ("note".equals(attrs.getValue("class"))) {
			// skip content of notes in spoken docs
			skipCurrentElementContent();
		} else {
			throw new IllegalStateException(String.format(
					"Unknown <span> with attributes %s at %s",
					attrs, getLocationString()));
		}
	}

	private void onSpanEnd() {
		// do nothing
	}

	private void onBrStart() {
		textBuilder.append('\n');
	}

	private void onBrEnd() {
		// do nothing
	}

	private String getLocationString() {
		return String.format("(%s, %s)",
				locator.getLineNumber(),
				locator.getColumnNumber());
	}

	private class ElementTuple {
		private final String elementName;
		private final LinkedList<ElementListener> listeners = Lists.newLinkedList();

		public ElementTuple(String elementName) {
			this.elementName = elementName;
		}
	}

	private interface ElementListener {
		void onElementEnd(String elementName);
	}
}