/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.LemmaLinkType;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class DictionaryXmlHandler extends DefaultHandler {

	private static final Logger log = LoggerFactory
			.getLogger(DictionaryXmlHandler.class);

	private static final String ELEM_DICTIONARY = "dictionary";
	private static final String ATTR_DICTIONARY_VERSION = "version";
	private static final String ATTR_DICTIONARY_REVISION = "revision";
	private static final String ELEM_GRAMMEMS = "grammems";
	private static final String ELEM_GRAMMEM = "grammem";
	private static final String ATTR_GRAMMEM_PARENT = "parent";
	private static final String ELEM_LEMMATA = "lemmata";
	private static final String ELEM_LEMMA = "lemma";
	private static final String ATTR_LEMMA_ID = "id";
	@SuppressWarnings("unused")
	private static final String ATTR_LEMMA_REV = "rev";
	private static final String ELEM_LEMMA_NORM = "l";
	private static final String ATTR_TEXT = "t";
	private static final String ELEM_WF_GRAMMEM = "g";
	@SuppressWarnings("unused")
	private static final String ELEM_LEMMA_GRAMMEM = ELEM_WF_GRAMMEM;
	private static final String ATTR_WF_GRAMMEME_ID = "v";
	private static final String ELEM_WORDFORM = "f";
	private static final String ELEM_LINK_TYPES = "link_types";
	private static final String ELEM_LINK_TYPE = "type";
	private static final String ATTR_LINK_TYPE_ID = "id";
	private static final String ELEM_LINKS = "links";
	private static final String ELEM_LINK = "link";
	@SuppressWarnings("unused")
	private static final String ATTR_LINK_ID = "id";
	private static final String ATTR_LINK_FROM = "from";
	private static final String ATTR_LINK_TO = "to";
	private static final String ATTR_LINK_TYPE = "type";

	private interface ElementHandler {
		void startElement(Attributes attrs);

		void endElement();

		void characters(String str);
	}

	private abstract class ElemHandlerBase implements ElementHandler {
		protected final String qName;

		ElemHandlerBase(String qName) {
			this.qName = qName;
			if (elemHandlersMap.put(qName, this) != null) {
				throw new IllegalStateException(String.format(
						"Duplicate elem handler for %s", qName));
			}
		}

		@Override
		public void startElement(Attributes attrs) {
		}

		@Override
		public void endElement() {
		}

		@Override
		public void characters(String str) {
			if (!str.trim().isEmpty()) {
				throw new IllegalStateException(String.format(
						"characters inside of %s:\n%s", qName, str));
			}
		}
	}

	private class NoOpHandler extends ElemHandlerBase {
		public NoOpHandler(String qName) {
			super(qName);
		}
	}

	private class DictionaryElemHandler extends ElemHandlerBase {
		DictionaryElemHandler() {
			super(ELEM_DICTIONARY);
		}

		@Override
		public void startElement(Attributes attrs) {
			String version = requiredAttr(attrs, ATTR_DICTIONARY_VERSION);
			String revision = requiredAttr(attrs, ATTR_DICTIONARY_REVISION);
			dict.setVersion(version);
			dict.setRevision(revision);
		}
	}

	private class GrammemHandler extends ElemHandlerBase {
		// state fields
		private String parentId;
		private String id;

		GrammemHandler() {
			super(ELEM_GRAMMEM);
		}

		@Override
		public void startElement(Attributes attrs) {
			parentId = requiredAttr(attrs, ATTR_GRAMMEM_PARENT);
		}

		@Override
		public void endElement() {
			if (id == null) {
				throw new IllegalStateException(String.format(
						"Empty %s element", qName));
			}
			if (parentId.isEmpty()) {
				parentId = null;
			}
			Grammeme gram = new Grammeme(id, parentId);
			dict.addGrammeme(gram);
			id = null;
			parentId = null;
		}

		@Override
		public void characters(String str) {
			id = str.trim();
			if (id.isEmpty()) {
				throw new IllegalStateException("Empty grammem id");
			}
		}
	}

	private class LemmaHandler extends ElemHandlerBase {
		private Lemma.Builder builder;
		// index 0 - wf string
		// index 1 - wf object
		private List<Object[]> wordforms;

		LemmaHandler() {
			super(ELEM_LEMMA);
		}

		@Override
		public void startElement(Attributes attrs) {
			builder = Lemma.builder(dict, requiredInt(attrs, ATTR_LEMMA_ID));
			wordforms = newLinkedList();
		}

		@Override
		public void endElement() {
			Lemma lemma = builder.build();
			if (acceptLemma(lemma)) {
				dict.addLemma(lemma);
				for (Object[] tuple : wordforms) {
					dict.addWordform((String) tuple[0], (Wordform) tuple[1]);
				}
				acceptedLemmaCounter++;
			} else {
				rejectedLemmaCounter++;
			}
			builder = null;
			wordforms = null;
			lemmasParsed++;
			if (lemmasParsed % 5000 == 0) {
				log.info("Lemmas have been parsed: {}", lemmasParsed);
			}
		}

		void addWordform(String text, Wordform wf) {
			wordforms.add(new Object[] { text, wf });
		}
	}

	private class LemmaNormHandler extends ElemHandlerBase {
		LemmaNormHandler() {
			super(ELEM_LEMMA_NORM);
		}

		@Override
		public void startElement(Attributes attrs) {
			String t = requiredAttr(attrs, ATTR_TEXT);
			getHandler(ELEM_LEMMA, LemmaHandler.class).builder.setString(t);
		}
	}

	private class WordformHandler extends ElemHandlerBase {
		private Wordform.Builder builder;
		private String text;

		WordformHandler() {
			super(ELEM_WORDFORM);
		}

		@Override
		public void startElement(Attributes attrs) {
			int lemmaId = getLemmaBuilder().getLemmaId();
			builder = Wordform.builder(dict, lemmaId);
			text = requiredAttr(attrs, ATTR_TEXT);
		}

		@Override
		public void endElement() {
			getHandler(ELEM_LEMMA, LemmaHandler.class).addWordform(text,
					builder.build());
			builder = null;
		}
	}

	private class WordformGrammemHandler extends ElemHandlerBase {
		private String gramId;

		WordformGrammemHandler() {
			super(ELEM_WF_GRAMMEM);
		}

		@Override
		public void startElement(Attributes attrs) {
			gramId = requiredAttr(attrs, ATTR_WF_GRAMMEME_ID);
		}

		@Override
		public void endElement() {
			if (insideElem(ELEM_LEMMA_NORM)) {
				getLemmaBuilder().addGrammeme(gramId);
			} else if (insideElem(ELEM_WORDFORM)) {
				getHandler(ELEM_WORDFORM, WordformHandler.class).builder
						.addGrammeme(gramId);
			} else {
				throw new IllegalStateException(String.format(
						"%s outside of %s OR %s", qName, ELEM_LEMMA_NORM,
						ELEM_WORDFORM));
			}
			gramId = null;
		}
	}

	private class LinkTypeHandler extends ElemHandlerBase {
		private String name;
		private Short id;

		LinkTypeHandler() {
			super(ELEM_LINK_TYPE);
		}

		@Override
		public void startElement(Attributes attrs) {
			id = requiredShort(attrs, ATTR_LINK_TYPE_ID);
		}

		@Override
		public void endElement() {
			if (name == null) {
				throw new IllegalStateException("Link type element is empty");
			}
			LemmaLinkType lemmaLinkType = new LemmaLinkType(id, name);
			dict.addLemmaLinkType(lemmaLinkType);
			id = null;
			name = null;
		}

		@Override
		public void characters(String str) {
			str = str.trim();
			if (str.isEmpty()) {
				throw new IllegalStateException("Empty lemma link name");
			}
			name = str;
		}
	}

	private class LinkHandler extends ElemHandlerBase {
		LinkHandler() {
			super(ELEM_LINK);
		}

		@Override
		public void startElement(Attributes attrs) {
			int fromId = requiredInt(attrs, ATTR_LINK_FROM);
			int toId = requiredInt(attrs, ATTR_LINK_TO);
			short linkTypeId = requiredShort(attrs, ATTR_LINK_TYPE);
			dict.addLemmaLink(fromId, toId, linkTypeId);
		}
	}

	// config fields
	private List<LemmaFilter> lemmaFilters = Lists.newLinkedList();
	// state fields
	private MorphDictionaryImpl dict;
	private Map<String, ElementHandler> elemHandlersMap = Maps.newHashMap();
	private Deque<String> elemStack = Lists.newLinkedList();
	private int lemmasParsed = 0;
	private int acceptedLemmaCounter;
	private int rejectedLemmaCounter;

	public DictionaryXmlHandler() {
	}

	public void addLemmaFilter(LemmaFilter lemmaFilter) {
		lemmaFilters.add(lemmaFilter);
	}

	@Override
	public void startDocument() throws SAXException {
		elemHandlersMap.clear();
		elemStack.clear();
		lemmasParsed = 0;
		acceptedLemmaCounter = 0;
		rejectedLemmaCounter = 0;
		finished = false;

		dict = new MorphDictionaryImpl();

		new DictionaryElemHandler();
		new NoOpHandler(ELEM_GRAMMEMS);
		new GrammemHandler();
		new NoOpHandler(ELEM_LEMMATA);
		new LemmaHandler();
		new LemmaNormHandler();
		new WordformHandler();
		new WordformGrammemHandler();
		new NoOpHandler(ELEM_LINK_TYPES);
		new LinkTypeHandler();
		new NoOpHandler(ELEM_LINKS);
		new LinkHandler();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		String elem = localName;
		if (elem.isEmpty()) {
			elem = qName;
		}
		ElementHandler elemHandler = getHandler(elem, ElementHandler.class);
		elemStack.addFirst(elem);
		elemHandler.startElement(attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String elem = localName;
		if (elem.isEmpty()) {
			elem = qName;
		}
		ElementHandler elemHandler = getHandler(elem, ElementHandler.class);
		if (sb != null) {
			String txt = sb.toString();
			// !
			txt.trim();
			elemHandler.characters(txt);
			sb = null;
		}
		elemHandler.endElement();
		if (!elemStack.getFirst().equals(elem)) {
			throw new IllegalStateException(String.format(
					"Elem ending expected: %s, but was: %s",
					elemStack.getFirst(), elem));
		}
		elemStack.removeFirst();
	}

	private StringBuilder sb;

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (sb == null) {
			sb = new StringBuilder();
		}
		sb.append(ch, start, length);
	}

	private boolean finished;

	@Override
	public void endDocument() throws SAXException {
		// sanity check
		if (!elemStack.isEmpty()) {
			throw new IllegalStateException(
					"Elem stack is not empty at the end: " + elemStack);
		}
		log.info("Lemmas accepted: {}\nLemmas rejected: {}",
				acceptedLemmaCounter, rejectedLemmaCounter);
		dict.complete();
		finished = true;
	}

	public MorphDictionaryImpl getDictionary() {
		if (!finished) {
			throw new IllegalStateException("Parsing is not finished");
		}
		return dict;
	}

	private boolean acceptLemma(Lemma lemma) {
		for (LemmaFilter filter : lemmaFilters) {
			if (!filter.accept(dict, lemma)) {
				return false;
			}
		}
		return true;
	}

	private boolean insideElem(String elem) {
		return elemStack.contains(elem);
	}

	@SuppressWarnings("unchecked")
	private <T extends ElementHandler> T getHandler(String qName, Class<T> clazz) {
		T result = (T) elemHandlersMap.get(qName);
		if (result == null) {
			throw new IllegalStateException("No handler for qName: " + qName);
		}
		return result;
	}

	private Lemma.Builder getLemmaBuilder() {
		return getHandler(ELEM_LEMMA, LemmaHandler.class).builder;
	}

	private static String requiredAttr(Attributes attrs, String qName) {
		String result = attrs.getValue(qName);
		if (result == null) {
			throw new IllegalStateException("attribute " + qName
					+ " is required");
		}
		return result.trim();
	}

	private static short requiredShort(Attributes attrs, String qName) {
		String resultStr = requiredAttr(attrs, qName);
		try {
			return Short.valueOf(resultStr);
		} catch (NumberFormatException e) {
			throw new IllegalStateException(String.format(
					"Attribute %s value is not number: %s", qName, resultStr),
					e);
		}
	}

	private static int requiredInt(Attributes attrs, String qName) {
		String resultStr = requiredAttr(attrs, qName);
		try {
			return Integer.valueOf(resultStr);
		} catch (NumberFormatException e) {
			throw new IllegalStateException(String.format(
					"Attribute %s value is not number: %s", qName, resultStr),
					e);
		}
	}
}