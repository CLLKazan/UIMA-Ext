/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.uima.resource.SharedResourceObject;

import ru.kfu.itis.issst.uima.consumer.cao.impl.AnnotationDTO;

/**
 * TODO destroy implementations correctly by binding to UIMA life-cycle events
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface CasAccessObject extends SharedResourceObject {

	long persistAnnotation(String type, long spanId, String coveredText, int startOffset,
			int endOffset);

	void persistFeature(long ownerAnnoId, String featureName, long valueAnnoId);

	long persistLaunch(Date startedTime);

	long persistDocument(long launchId, String docURI, Long size, Long processingTime);

	long persistSpan(long docId, String coveredText);

	List<AnnotationDTO> getTopAnnotationsByLaunch(Set<Integer> launchIds,
			Set<String> topAnnoTypes);
}