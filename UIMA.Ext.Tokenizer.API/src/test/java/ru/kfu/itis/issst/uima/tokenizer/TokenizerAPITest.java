/**
 * 
 */
package ru.kfu.itis.issst.uima.tokenizer;

import java.util.Iterator;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;

import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
public class TokenizerAPITest {

	@Test
	public void checkTypeSystemExistence() throws ResourceInitializationException {
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
				TokenizerAPI.TYPESYSTEM_TOKENIZER);
		CAS cas = CasCreationUtils.createCas(tsd, null, null);
		Iterator<Type> typeIterator = cas.getTypeSystem().getTypeIterator();
		Set<String> shortNames = Sets.newHashSet();
		while(typeIterator.hasNext()){
			shortNames.add(typeIterator.next().getShortName());
		}
		Assert.assertTrue("No Token type", shortNames.contains("Token"));
		Assert.assertTrue("No W type", shortNames.contains("W"));
	}

}
