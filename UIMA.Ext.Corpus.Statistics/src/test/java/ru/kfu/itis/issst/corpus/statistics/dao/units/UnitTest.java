package ru.kfu.itis.issst.corpus.statistics.dao.units;

import static org.junit.Assert.assertArrayEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class UnitTest {

	@Test
	public void testGetSortedClasses() throws URISyntaxException {
		Unit unit = new Unit(new UnitLocation(new URI("1"), 10, 15), "vasya",
				"cat");
		assertArrayEquals(new String[] { "cat" }, unit.getSortedClasses());
		unit.putClassByAnnotatorId("petya", "dog");
		assertArrayEquals(new String[] { "dog", "cat" },
				unit.getSortedClasses());
		unit.putClassByAnnotatorId("sasha", "bird");
		assertArrayEquals(new String[] { "dog", "bird", "cat" },
				unit.getSortedClasses());
	}

}
