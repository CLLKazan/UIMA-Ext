/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.annolab.tt4j.TokenAdapter;
import org.annolab.tt4j.TokenHandler;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class HunposWrapperTest {

	@Test
	public void test() throws IOException, HunposException {
		if (System.getProperty("hunpos.home") == null) {
			System.err
					.println("System property 'hunpos.home' is not set. Skipping HunposWrapperTest");
			return;
		}
		HunposWrapper<Token> hunpos = new HunposWrapper<Token>();
		// configure
		hunpos.setTokenAdapter(new TokenAdapter<Token>() {
			@Override
			public String getText(Token token) {
				return token.string;
			}
		});
		// get model name
		File modelFile = new File(System.getProperty("hunpos.home") + separator
				+ "models" + separator + "english.model");
		hunpos.setModelName(modelFile.getPath());
		// run
		testTagging(hunpos, "The input must be one token per line .");
		// run again
		testTagging(hunpos, "There are many other parameters of the algorithm .");
		// destroy
		hunpos.destroy();
	}

	private void testTagging(HunposWrapper<Token> hunpos, String text)
			throws IOException, HunposException {
		ListTokenHandler tokenHandler = new ListTokenHandler();
		hunpos.setTokenHandler(tokenHandler);
		List<Token> tokens = toTokens(text);
		hunpos.process(tokens);
		System.out.println(tokens);
		System.out.println(tokenHandler.getTags());
		Assert.assertEquals(tokenHandler.getTags().size(), tokens.size());
	}

	private static class ListTokenHandler implements TokenHandler<Token> {
		private List<String> tags = newLinkedList();

		@Override
		public void token(Token token, String pos, String lemma) {
			tags.add(pos);
		}

		public List<String> getTags() {
			return tags;
		}
	}

	private static List<Token> toTokens(String text) {
		ArrayList<String> tokStrings = newArrayList(Splitter.on(' ').trimResults().split(text));
		return Lists.transform(tokStrings, new Function<String, Token>() {
			@Override
			public Token apply(String input) {
				return new Token(input);
			}
		});
	}

	private static class Token {
		final String string;

		Token(String string) {
			this.string = string;
		}
	}
}
