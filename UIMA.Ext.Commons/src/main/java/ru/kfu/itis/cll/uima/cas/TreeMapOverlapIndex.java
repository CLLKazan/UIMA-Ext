/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.uima.cas.text.AnnotationFS;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * TODO test
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class TreeMapOverlapIndex<A extends AnnotationFS> implements OverlapIndex<A> {
	// factory methods
	static <A extends AnnotationFS> TreeMapOverlapIndex<A> from(Iterator<A> srcIter) {
		return new TreeMapOverlapIndex<A>(srcIter);
	}

	// IMPL START
	private NavigableMap<Integer, Set<A>> beginIdx;
	private NavigableMap<Integer, Set<A>> endIdx;
	private Map<A, Integer> annoIds;
	private int annoIdCounter;

	private TreeMapOverlapIndex(Iterator<A> srcIter) {
		beginIdx = Maps.newTreeMap();
		endIdx = Maps.newTreeMap();
		annoIds = Maps.newHashMap();
		while (srcIter.hasNext()) {
			A sa = srcIter.next();
			// by begin
			{
				int saBegin = sa.getBegin();
				Set<A> sameBeginSet = beginIdx.get(saBegin);
				if (sameBeginSet == null) {
					// preserve source iterator ordering
					sameBeginSet = Sets.newLinkedHashSet();
					beginIdx.put(saBegin, sameBeginSet);
				}
				sameBeginSet.add(sa);
			}
			// by end
			{
				int saEnd = sa.getEnd();
				Set<A> sameEndSet = endIdx.get(saEnd);
				if (sameEndSet == null) {
					// preserve source iterator ordering
					sameEndSet = Sets.newLinkedHashSet();
					endIdx.put(saEnd, sameEndSet);
				}
				sameEndSet.add(sa);
			}
			// this is required for internal comparator to preserve source iterator ordering
			// if offsets are equal
			if (!annoIds.containsKey(sa)) {
				annoIds.put(sa, ++annoIdCounter);
			}
		}
	}

	@Override
	public Set<A> getOverlapping(int begin, int end) {
		Set<A> result = Sets.newTreeSet(innerComparator);
		// annotations whose begin in [begin, end) interval
		NavigableMap<Integer, Set<A>> subByBegin = beginIdx.subMap(begin, true, end, false);
		for (Set<A> annoSet : subByBegin.values()) {
			if (annoSet != null) {
				result.addAll(annoSet);
			}
		}
		// annotations whose end in (begin, end]
		NavigableMap<Integer, Set<A>> subByEnd = endIdx.subMap(begin, false, end, true);
		for (Set<A> annoSet : subByEnd.values()) {
			if (annoSet != null) {
				result.addAll(annoSet);
			}
		}
		return result;
	}

	private final Comparator<AnnotationFS> offsetComp = AnnotationOffsetComparator
			.instance(AnnotationFS.class);
	private final Comparator<A> innerComparator = new Comparator<A>() {
		@Override
		public int compare(A o1, A o2) {
			int result = offsetComp.compare(o1, o2);
			if (result != 0) {
				return result;
			}
			Integer o1Id = annoIds.get(o1);
			Integer o2Id = annoIds.get(o2);
			return o1Id.compareTo(o2Id);
		}
	};
}