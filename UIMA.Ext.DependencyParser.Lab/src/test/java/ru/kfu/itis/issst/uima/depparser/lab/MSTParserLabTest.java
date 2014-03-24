package ru.kfu.itis.issst.uima.depparser.lab;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Range;

public class MSTParserLabTest {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	public void testMakingSplits1() {
		List<Range<Integer>> testRanges = MSTParserLab.makeCorpusSplits(100, 10);
		log.info("makeCorpusSplits(100,10):\n{}", Joiner.on('\n').join(testRanges));
	}

	@Test
	public void testMakingSplits2() {
		List<Range<Integer>> testRanges = MSTParserLab.makeCorpusSplits(115, 10);
		log.info("makeCorpusSplits(115,10):\n{}", Joiner.on('\n').join(testRanges));
		assertEquals(10, testRanges.size());
		Range<Integer> firstRange = testRanges.get(0);
		assertEquals(Range.closedOpen(0, 11), firstRange);
		Range<Integer> lastRange = testRanges.get(9);
		assertEquals(Range.closedOpen(99, 115), lastRange);
	}
}
