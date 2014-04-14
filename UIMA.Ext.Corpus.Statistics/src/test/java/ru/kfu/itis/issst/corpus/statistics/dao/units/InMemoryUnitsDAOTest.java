package ru.kfu.itis.issst.corpus.statistics.dao.units;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import com.google.common.collect.Iterables;

public class InMemoryUnitsDAOTest {

	@Test
	public void testAddAndGetUnits() throws URISyntaxException {
		UnitsDAO dao = new InMemoryUnitsDAO();

		dao.addUnitItem(new URI("1"), 0, 10, "1", "one");
		dao.addUnitItem(new URI("1"), 0, 10, "2", "two");
		dao.addUnitItem(new URI("1"), 11, 15, "1", "one");
		dao.addUnitItem(new URI("1"), 11, 15, "2", "one");
		dao.addUnitItem(new URI("2"), 0, 9, "1", "two");
		dao.addUnitItem(new URI("2"), 0, 9, "2", "two");
		Iterable<Unit> units = dao.getUnits();
		assertEquals(3, Iterables.size(units));
		boolean unitWasFounded = false;
		for (Unit unit : units) {
			if (unit.getLocation().equals(new UnitLocation(new URI("1"), 0, 10))) {
				unitWasFounded = true;
				assertArrayEquals(new String[] {"one", "two"}, unit.getSortedClasses());;
			}
		}
		assertTrue(unitWasFounded);
	}

}
