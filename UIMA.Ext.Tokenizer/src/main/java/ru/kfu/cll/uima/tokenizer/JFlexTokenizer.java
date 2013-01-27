package ru.kfu.cll.uima.tokenizer;

import java.io.CharArrayReader;
import java.io.IOException;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import ru.kfu.cll.uima.tokenizer.TokenInfo;

public class JFlexTokenizer extends JCasAnnotator_ImplBase {
	public void process (JCas cas) {
		String docText = cas.getDocumentText();
		char[] text = docText.toCharArray();
		CharArrayReader reader = new CharArrayReader(text);
		JF_Tokenizer scanner = new JF_Tokenizer(reader);
		while (!scanner.isEof()) {
			try {
				TokenInfo token = scanner.yylex();
				if (token != null){
				ru.kfu.cll.uima.tokenizer.fstype.Token tokenU = new ru.kfu.cll.uima.tokenizer.fstype.Token(cas);
				tokenU.setBegin(token.begin);
				tokenU.setEnd(token.end);
				tokenU.setTypeName(token.typeName);
				tokenU.addToIndexes();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
