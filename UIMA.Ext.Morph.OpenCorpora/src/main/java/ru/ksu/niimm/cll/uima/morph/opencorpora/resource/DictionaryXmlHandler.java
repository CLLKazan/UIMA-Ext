/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.LemmaLinkType;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

class DictionaryXmlHandler extends DefaultHandler {

	private static final Logger log = LoggerFactory
			.getLogger(DictionaryXmlHandler.class);

	private static final String ELEM_DICTIONARY = "dictionary";
	private static final String ATTR_DICTIONARY_VERSION = "version";
	private static final String ATTR_DICTIONARY_REVISION = "revision";
	private static final String ELEM_GRAMMEMS = "grammemes";
	private static final String ELEM_GRAMMEM = "grammeme";
	private static final String ATTR_GRAMMEM_PARENT = "parent";
	private static final String ELEM_GRAMMEM_NAME = "name";
	private static final String ELEM_GRAMMEM_ALIAS = "alias";
	private static final String ELEM_GRAMMEM_DESCRIPTION = "description";

	private static final String ELEM_RESTRICTIONS = "restrictions";

	private static final String ELEM_LEMMATA = "lemmata";
	private static final String ELEM_LEMMA = "lemma";
	private static final String ATTR_LEMMA_ID = "id";
	@SuppressWarnings("unused")
	private static final String ATTR_LEMMA_REV = "rev";
	private static final String ELEM_LEMMA_NORM = "l";
	private static final String ATTR_TEXT = "t";
	private static final String ELEM_WF_GRAMMEM = "g";
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

	private abstract class ElementHandler {
		protected final String qName;
		private ElementHandler parentHandler;

		protected ElementHandler(String qName) {
			if (qName == null)
				throw new NullPointerException(qName);
			this.qName = qName;
		}

		protected final <EH> EH getParent(Class<EH> parentClass) {
			return parentClass.cast(parentHandler);
		}

		protected final void setParent(ElementHandler parent) {
			this.parentHandler = parent;
		}

		protected abstract void startElement(Attributes attrs);

		protected abstract void endElement();

		protected abstract void characters(String str);

		/**
		 * @param elem
		 * @return return handler for child element elem
		 */
		protected abstract ElementHandler getHandler(String elem);
	}

	private abstract class ElementHandlerBase extends ElementHandler {

		private Map<String, ElementHandler> children = ImmutableMap.of();

		protected ElementHandlerBase(String qName) {
			super(qName);
		}

		@Override
		protected final void startElement(Attributes attrs) {
			children = declareChildren();
			if (children != null) {
				for (ElementHandler child : children.values()) {
					child.setParent(this);
				}
			}
			startSelf(attrs);
		}

		@Override
		protected final void endElement() {
			endSelf();
			// clear children
			this.children = null;
		}

		@Override
		protected void characters(String str) {
			if (!str.trim().isEmpty()) {
				throw new UnsupportedOperationException(String.format(
						"Unexpected characters within %s:\n%s",
						this.qName, str));
			}
		}

		@Override
		protected final ElementHandler getHandler(String elem) {
			return children == null ? null : children.get(elem);
		}

		protected abstract void startSelf(Attributes attrs);

		protected abstract void endSelf();

		protected abstract Map<String, ElementHandler> declareChildren();
	}

	private class RootHandler extends ElementHandler {
		private ElementHandlerBase topHandler;

		RootHandler(ElementHandlerBase topHandler) {
			super("%ROOT%");
			this.topHandler = topHandler;
		}

		@Override
		protected void startElement(Attributes attrs) {
			throw new IllegalStateException();
		}

		@Override
		protected void endElement() {
			throw new IllegalStateException();
		}

		@Override
		protected void characters(String str) {
			if (!str.trim().isEmpty()) {
				throw new IllegalStateException();
			}
		}

		@Override
		protected ElementHandler getHandler(String elem) {
			if (Objects.equal(elem, topHandler.qName)) {
				return topHandler;
			}
			return null;
		}
	}

	private abstract class NoOpHandler extends ElementHandlerBase {
		NoOpHandler(String qName) {
			super(qName);
		}

		@Override
		protected void startSelf(Attributes attrs) {
		}

		@Override
		protected void endSelf() {
		}
	}

	private class IgnoreHandler extends ElementHandler {
		protected IgnoreHandler(String qName) {
			super(qName);
		}

		@Override
		protected void startElement(Attributes attrs) {
			// ignore
		}

		@Override
		protected void endElement() {
			// ignore
		}

		@Override
		protected void characters(String str) {
			// ignore
		}

		@Override
		protected ElementHandler getHandler(String elem) {
			// ignore all children
			IgnoreHandler result = new IgnoreHandler(elem);
			result.setParent(this);
			return result;
		}
	}

	private class ReadContentHandler extends NoOpHandler {
		private String content;

		ReadContentHandler(String qName) {
			super(qName);
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return null;
		}

		@Override
		protected void characters(String str) {
			this.content = str.trim();
		}

		String getContent() {
			return content;
		}
	}

	private class DictionaryElemHandler extends ElementHandlerBase {
		DictionaryElemHandler() {
			super(ELEM_DICTIONARY);
		}

		@Override
		protected void startSelf(Attributes attrs) {
			String version = requiredAttr(attrs, ATTR_DICTIONARY_VERSION);
			String revision = requiredAttr(attrs, ATTR_DICTIONARY_REVISION);
			dict.setVersion(version);
			dict.setRevision(revision);
		}

		@Override
		protected void endSelf() {
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return toMap(newHashSet(
					new GrammemsHandler(),
					new IgnoreHandler(ELEM_RESTRICTIONS),
					new LemmataHandler(),
					new LinkTypesHandler(),
					new LinksHandler()));
		}
	}

	private class GrammemsHandler extends NoOpHandler {
		GrammemsHandler() {
			super(ELEM_GRAMMEMS);
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return toMap(newHashSet(new GrammemHandler()));
		}

		@Override
		protected void endSelf() {
			dict.completeGramSet();
			super.endSelf();
		}
	}

	private class LemmataHandler extends NoOpHandler {
		LemmataHandler() {
			super(ELEM_LEMMATA);
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return toMap(newHashSet(new LemmaHandler()));
		}
	}

	private class LinkTypesHandler extends NoOpHandler {
		LinkTypesHandler() {
			super(ELEM_LINK_TYPES);
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return toMap(newHashSet(new LinkTypeHandler()));
		}
	}

	private class LinksHandler extends NoOpHandler {
		LinksHandler() {
			super(ELEM_LINKS);
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return toMap(newHashSet(new LinkHandler()));
		}
	}

	private class GrammemHandler extends ElementHandlerBase {
		// state fields
		private String parentId;

		GrammemHandler() {
			super(ELEM_GRAMMEM);
		}

		@Override
		protected void startSelf(Attributes attrs) {
			parentId = requiredAttr(attrs, ATTR_GRAMMEM_PARENT);
		}

		@Override
		protected void endSelf() {
			String id = nameHandler.getContent();
			if (id == null) {
				throw new IllegalStateException("Empty grammeme name");
			}
			if (parentId.isEmpty()) {
				parentId = null;
			}
			String alias = aliasHandler.getContent();
			String description = descHandler.getContent();
			Grammeme gram = new Grammeme(id, parentId, alias, description);
			dict.addGrammeme(gram);
			id = null;
			parentId = null;
			// child handlers are cleared by super class
		}

		private ReadContentHandler nameHandler;
		private ReadContentHandler aliasHandler;
		private ReadContentHandler descHandler;

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			nameHandler = new ReadContentHandler(ELEM_GRAMMEM_NAME);
			aliasHandler = new ReadContentHandler(ELEM_GRAMMEM_ALIAS);
			descHandler = new ReadContentHandler(ELEM_GRAMMEM_DESCRIPTION);
			return toMap(newHashSet(nameHandler, aliasHandler, descHandler));
		}

	}

	private class LemmaHandler extends ElementHandlerBase {
		private Lemma.Builder builder;
		// wf string => set of wf objects
		private Multimap<String, Wordform> wordforms;

		LemmaHandler() {
			super(ELEM_LEMMA);
		}

		@Override
		protected void startSelf(Attributes attrs) {
			builder = Lemma.builder(dict, requiredInt(attrs, ATTR_LEMMA_ID));
			wordforms = LinkedHashMultimap.create();
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return toMap(newHashSet(new LemmaNormHandler(), new WordformHandler()));
		}

		@Override
		protected void endSelf() {
			Lemma lemma = builder.build();
			if (postProcessLemma(lemma, wordforms)) {
				dict.addLemma(lemma);
				for (String wfStr : wordforms.keySet()) {
					for (Wordform wf : wordforms.get(wfStr)) {
						dict.addWordform(wfStr, wf);
					}
				}
				acceptedLemmaCounter++;
			} else {
				rejectedLemmaCounter++;
			}
			builder = null;
			wordforms = null;
			lemmasParsed++;
			if (lemmasParsed % 10000 == 0) {
				log.info("Lemmas have been parsed: {}", lemmasParsed);
			}
		}

		void addWordform(String text, Wordform wf) {
			if (!wordforms.put(text, wf)) {
				throw new IllegalStateException(String.format("Duplicate pair <%s, %s>"));
			}
		}
	}

	private class LemmaNormHandler extends ElementHandlerBase {
		LemmaNormHandler() {
			super(ELEM_LEMMA_NORM);
		}

		@Override
		protected void startSelf(Attributes attrs) {
			String t = requiredAttr(attrs, ATTR_TEXT);
			getParent(LemmaHandler.class).builder.setString(t);
		}

		@Override
		protected void endSelf() {
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return toMap(newHashSet(new LemmaGrammemHandler()));
		}
	}

	private class WordformHandler extends ElementHandlerBase {
		private Wordform.Builder builder;
		private String text;

		WordformHandler() {
			super(ELEM_WORDFORM);
		}

		@Override
		protected void startSelf(Attributes attrs) {
			int lemmaId = getParent(LemmaHandler.class).builder.getLemmaId();
			builder = Wordform.builder(dict, lemmaId);
			text = requiredAttr(attrs, ATTR_TEXT);
		}

		@Override
		protected void endSelf() {
			getParent(LemmaHandler.class).addWordform(text, builder.build());
			builder = null;
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return toMap(newHashSet(new WordformGrammemHandler()));
		}
	}

	private abstract class GrammemRefHandler extends ElementHandlerBase {
		protected String gramId;

		GrammemRefHandler(String qName) {
			super(qName);
		}

		@Override
		protected void startSelf(Attributes attrs) {
			gramId = requiredAttr(attrs, ATTR_WF_GRAMMEME_ID);
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return null;
		}
	}

	private class LemmaGrammemHandler extends GrammemRefHandler {
		LemmaGrammemHandler() {
			super(ELEM_LEMMA_GRAMMEM);
		}

		@Override
		protected void endSelf() {
			getParent(LemmaNormHandler.class).getParent(LemmaHandler.class).builder
					.addGrammeme(gramId);
			gramId = null;
		}
	}

	private class WordformGrammemHandler extends GrammemRefHandler {
		WordformGrammemHandler() {
			super(ELEM_WF_GRAMMEM);
		}

		@Override
		protected void endSelf() {
			getParent(WordformHandler.class).builder.addGrammeme(gramId);
			gramId = null;
		}
	}

	private class LinkTypeHandler extends ElementHandlerBase {
		private String name;
		private Short id;

		LinkTypeHandler() {
			super(ELEM_LINK_TYPE);
		}

		@Override
		protected void startSelf(Attributes attrs) {
			id = requiredShort(attrs, ATTR_LINK_TYPE_ID);
		}

		@Override
		protected void endSelf() {
			if (name == null) {
				throw new IllegalStateException("Link type element is empty");
			}
			LemmaLinkType lemmaLinkType = new LemmaLinkType(id, name);
			dict.addLemmaLinkType(lemmaLinkType);
			id = null;
			name = null;
		}

		@Override
		protected void characters(String str) {
			str = str.trim();
			if (str.isEmpty()) {
				throw new IllegalStateException("Empty lemma link name");
			}
			name = str;
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return null;
		}
	}

	private class LinkHandler extends ElementHandlerBase {
		LinkHandler() {
			super(ELEM_LINK);
		}

		@Override
		protected void startSelf(Attributes attrs) {
			int fromId = requiredInt(attrs, ATTR_LINK_FROM);
			int toId = requiredInt(attrs, ATTR_LINK_TO);
			short linkTypeId = requiredShort(attrs, ATTR_LINK_TYPE);
			dict.addLemmaLink(fromId, toId, linkTypeId);
		}

		@Override
		protected void endSelf() {
		}

		@Override
		protected Map<String, ElementHandler> declareChildren() {
			return null;
		}
	}

	// config fields
	private List<LemmaPostProcessor> lemmaPostProcessors = Lists.newLinkedList();
	// state fields
	private MorphDictionaryImpl dict;
	private Deque<String> elemStack = Lists.newLinkedList();
	private Deque<ElementHandler> handlerStack = Lists.newLinkedList();
	private int lemmasParsed = 0;
	private int acceptedLemmaCounter;
	private int rejectedLemmaCounter;
	private ElementHandler rootHandler;

	DictionaryXmlHandler() {
	}

	/**
	 * NOTE! Order of LemmaPostProcessor instances may be crucial!
	 * 
	 * @param lemmaPP
	 *            instance to add
	 */
	public void addLemmaPostProcessor(LemmaPostProcessor lemmaPP) {
		lemmaPostProcessors.add(lemmaPP);
	}

	@Override
	public void startDocument() throws SAXException {
		handlerStack.clear();
		elemStack.clear();
		lemmasParsed = 0;
		acceptedLemmaCounter = 0;
		rejectedLemmaCounter = 0;
		finished = false;

		dict = new MorphDictionaryImpl();

		rootHandler = new RootHandler(new DictionaryElemHandler());
		handlerStack.addFirst(rootHandler);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		String elem = localName;
		if (elem.isEmpty()) {
			elem = qName;
		}
		elemStack.addFirst(elem);

		ElementHandler contextHandler = handlerStack.getFirst();
		ElementHandler elemHandler = contextHandler.getHandler(elem);
		if (elemHandler == null) {
			throw new IllegalStateException(String.format(
					"Context handler %s have not returned handler for elem %s",
					contextHandler, elemStack));
		}
		handlerStack.addFirst(elemHandler);
		elemHandler.startElement(attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String elem = localName;
		if (elem.isEmpty()) {
			elem = qName;
		}
		// check doc structure sanity
		if (!elemStack.getFirst().equals(elem)) {
			throw new IllegalStateException(String.format(
					"Elem ending expected: %s, but was: %s",
					elemStack.getFirst(), elem));
		}

		ElementHandler elemHandler = handlerStack.removeFirst();
		if (sb != null) {
			String txt = sb.toString();
			// !
			txt.trim();
			elemHandler.characters(txt);
			sb = null;
		}
		elemHandler.endElement();

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

	/**
	 * Invoke lemma post-processors
	 * 
	 * @param lemma
	 * @param wfMap
	 *            mutable map of wordform_string => set_of_wordform_objects
	 * @return true if given lemma must be accepted, false - otherwise.
	 */
	private boolean postProcessLemma(Lemma lemma, Multimap<String, Wordform> wfMap) {
		for (LemmaPostProcessor filter : lemmaPostProcessors) {
			if (!filter.process(dict, lemma, wfMap)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	private boolean insideElem(String elem) {
		return elemStack.contains(elem);
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

	private static Map<String, ElementHandler> toMap(Set<? extends ElementHandler> set) {
		Map<String, ElementHandler> result = newHashMapWithExpectedSize(set.size());
		for (ElementHandler handler : set) {
			result.put(handler.qName, handler);
		}
		return result;
	}
}