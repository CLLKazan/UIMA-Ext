package tokenization;

import java.io.CharArrayReader;
import java.io.IOException;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import tokenization.types.Token;

public class TokenizationAnnotator extends JCasAnnotator_ImplBase {
	public void process (JCas cas) {
		String docText = cas.getDocumentText();
		char[] text = docText.toCharArray();
		CharArrayReader reader = new CharArrayReader(text);
		JFlex_Tokenizer scanner = new JFlex_Tokenizer(reader, cas);
		while (!scanner.isEof()) {
			try {
				Token token = scanner.yylex();
				if (token != null) { 
					token.addToIndexes();
				}
			} catch (IOException e) {				
				e.printStackTrace();
			}	
		}
	}
}












































