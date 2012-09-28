/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao;

import static java.lang.Math.min;

import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.event.EventListenerSupport;

import ru.kfu.itis.issst.uima.consumer.cao.impl.AnnotationDTO;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DeltaCalc {

	private static Logger log = Logger.getLogger(DeltaCalc.class.getSimpleName());

	// dependencies
	private CasAccessObject cao;

	// input config
	private Set<Integer> pastLaunchIds;
	private Set<Integer> newLaunchIds;
	private Set<String> topAnnoTypes;

	// state fields
	private EventListenerSupport<DeltaListener> deltaListenerSupport = EventListenerSupport
			.create(DeltaListener.class);
	private AnnoIndex pastIndex;

	// private AnnoIndex newIndex;

	public DeltaCalc(CasAccessObject cao) {
		this.cao = cao;
	}

	public void setPastLaunchIds(Set<Integer> pastLaunchIds) {
		this.pastLaunchIds = pastLaunchIds;
	}

	public void setNewLaunchIds(Set<Integer> newLaunchIds) {
		this.newLaunchIds = newLaunchIds;
	}

	public void setTopAnnoTypes(Set<String> topAnnoTypes) {
		this.topAnnoTypes = topAnnoTypes;
	}

	public void addListener(DeltaListener listener) {
		deltaListenerSupport.addListener(listener);
	}

	public void run() {
		List<AnnotationDTO> pastAnnotations = cao.getTopAnnotationsByLaunch(pastLaunchIds,
				topAnnoTypes);
		List<AnnotationDTO> newAnnotations = cao.getTopAnnotationsByLaunch(newLaunchIds,
				topAnnoTypes);
		pastIndex = new AnnoIndex(pastAnnotations);
		// newIndex = new AnnoIndex(newAnnotations);

		Set<Long> handledPastAnnotations = new HashSet<Long>();
		for (AnnotationDTO newAnno : newAnnotations) {
			AnnotationDTO pastAnno = pastIndex.getClosestOverlappingOfSameType(newAnno);
			if (pastAnno == null) {
				fireNewAnnotation(newAnno);
				continue;
			}
			// sanityCheck
			if (!ObjectUtils.equals(pastAnno.getDocUri(), newAnno.getDocUri())
					|| newAnno.getStartOffset() > pastAnno.getEndOffset()
					|| newAnno.getEndOffset() < pastAnno.getStartOffset()) {
				throw new IllegalStateException("Assertion failed. Index is incorrect");
			}
			handledPastAnnotations.add(pastAnno.getId());
			if (newAnno.getStartOffset() == pastAnno.getStartOffset()
					&& newAnno.getEndOffset() == pastAnno.getEndOffset()) {
				// TODO it's place to check features
				fireSavedAnnotation(pastAnno, newAnno);
			} else {
				fireChangedAnnotation(pastAnno, newAnno);
			}
		}

		// catch all lost annotations
		for (AnnotationDTO oldAnno : pastAnnotations) {
			if (!handledPastAnnotations.contains(oldAnno.getId())) {
				fireLostAnnotation(oldAnno);
			}
		}
	}

	private void fireNewAnnotation(AnnotationDTO anno) {
		deltaListenerSupport.fire().onNewAnnotation(anno);
	}

	private void fireLostAnnotation(AnnotationDTO anno) {
		deltaListenerSupport.fire().onLostAnnotation(anno);
	}

	private void fireChangedAnnotation(AnnotationDTO oldAnno, AnnotationDTO newAnno) {
		deltaListenerSupport.fire().onChangedAnnotation(oldAnno, newAnno);
	}

	private void fireSavedAnnotation(AnnotationDTO oldAnno, AnnotationDTO newAnno) {
		deltaListenerSupport.fire().onSavedAnnotation(oldAnno, newAnno);
	}

	class AnnoIndex {
		// keys in order: docURI, annoType, startOffset
		private Map<String, Map<String, NavigableMap<Integer, AnnotationDTO>>> indexMap = new HashMap<String, Map<String, NavigableMap<Integer, AnnotationDTO>>>();

		AnnoIndex(Collection<AnnotationDTO> initialSet) {
			for (AnnotationDTO anno : initialSet) {
				Map<String, NavigableMap<Integer, AnnotationDTO>> typeIndex =
						indexMap.get(anno.getDocUri());
				if (typeIndex == null) {
					typeIndex = new HashMap<String, NavigableMap<Integer, AnnotationDTO>>();
					indexMap.put(anno.getDocUri(), typeIndex);
				}
				NavigableMap<Integer, AnnotationDTO> startOffsetIdx = typeIndex.get(anno.getType());
				if (startOffsetIdx == null) {
					startOffsetIdx = new TreeMap<Integer, AnnotationDTO>();
					typeIndex.put(anno.getType(), startOffsetIdx);
				}
				AnnotationDTO oldAnno = startOffsetIdx.put(anno.getStartOffset(), anno);
				if (oldAnno != null) {
					log.warning(String
							.format(
									"Detected 2 annotations of the same type starting from same position:\n"
											+ "First: %s\nSecond: %s", oldAnno, anno));
				}
			}
		}

		public AnnotationDTO getClosestOverlappingOfSameType(AnnotationDTO target) {
			Map<String, NavigableMap<Integer, AnnotationDTO>> typeIdx = indexMap.get(target
					.getDocUri());
			if (typeIdx == null) {
				return null;
			}
			NavigableMap<Integer, AnnotationDTO> positionIdx = typeIdx.get(target.getType());
			if (positionIdx == null) {
				return null;
			}
			AnnotationDTO leftClosest = null;
			Entry<Integer, AnnotationDTO> leftClosestEntry = positionIdx.floorEntry(
					target.getStartOffset());
			if (leftClosestEntry != null) {
				leftClosest = leftClosestEntry.getValue();
				if (leftClosest.getStartOffset() == target.getStartOffset()) {
					// exact match
					return leftClosest;
				}
				if (leftClosest.getEndOffset() <= target.getStartOffset()) {
					// does not overlap
					leftClosest = null;
				}
			}
			AnnotationDTO rightClosest = null;
			Entry<Integer, AnnotationDTO> rightClosestEntry = positionIdx.higherEntry(
					target.getStartOffset());
			if (rightClosestEntry != null) {
				rightClosest = rightClosestEntry.getValue();
				if (rightClosest.getStartOffset() >= target.getEndOffset()) {
					// does not overlap
					rightClosest = null;
				}
			}
			if (leftClosest != null) {
				if (rightClosest != null) {
					int targetLength = getLength(target);
					// return one with longer overlap
					int leftOverlap = leftClosest.getEndOffset() - target.getStartOffset();
					if (leftOverlap < 0) {
						throw new IllegalStateException("Assertion failed");
					}
					leftOverlap = min(leftOverlap, targetLength);

					int rightOverlap =
							min(rightClosest.getEndOffset(), target.getEndOffset())
									- rightClosest.getStartOffset();
					if (leftOverlap > rightOverlap) {
						return leftClosest;
					} else if (rightOverlap > leftOverlap) {
						return rightClosest;
					} else {
						// equal overlap! return the longest
						int leftLength = getLength(leftClosest);
						int rightLength = getLength(rightClosest);
						if (leftLength > rightLength) {
							return leftClosest;
						} else {
							return rightClosest;
						}
					}
				} else {
					// right is null
					return leftClosest;
				}
			} else {
				// left is null
				if (rightClosest != null) {
					return rightClosest;
				} else {
					return null;
				}
			}
		}
	}

	private int getLength(AnnotationDTO anno) {
		return anno.getEndOffset() - anno.getStartOffset();
	}

	public interface DeltaListener extends EventListener {
		void onNewAnnotation(AnnotationDTO anno);

		void onChangedAnnotation(AnnotationDTO oldAnno, AnnotationDTO newAnno);

		void onLostAnnotation(AnnotationDTO anno);

		void onSavedAnnotation(AnnotationDTO oldAnno, AnnotationDTO newAnno);
	}
}