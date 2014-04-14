package ru.kfu.itis.issst.corpus.statistics.dao.units;

import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Map;

import com.google.common.collect.Maps;

public class InMemoryUnitsDAO implements UnitsDAO {

	private Map<UnitLocation, Unit> unitByUnitLocation = Maps.newHashMap();

	@Override
	public void addUnitItem(URI documentURI, int begin, int end,
			String annotatorId, String annotatorClass) {
		UnitLocation location = new UnitLocation(documentURI, begin, end);
		if (unitByUnitLocation.containsKey(location)) {
			unitByUnitLocation.get(location).putClassByAnnotatorId(annotatorId,
					annotatorClass);
		} else {
			unitByUnitLocation.put(location, new Unit(location, annotatorId,
					annotatorClass));
		}
	}

	@Override
	public Iterable<Unit> getUnits() {
		return unitByUnitLocation.values();
	}

	@Override
	public void toTSV(Writer writer) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addUnitsFromTSV(Reader reader) {
		// TODO Auto-generated method stub
	}

}
