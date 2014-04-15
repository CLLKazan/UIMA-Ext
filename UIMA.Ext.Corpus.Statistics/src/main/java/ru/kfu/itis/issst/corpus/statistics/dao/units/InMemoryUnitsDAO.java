package ru.kfu.itis.issst.corpus.statistics.dao.units;

import java.io.PrintWriter;
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
		PrintWriter pw = new PrintWriter(writer);
		for (Unit unit : this.getUnits()) {
			URI documentURI = unit.getDocumentURI();
			int begin = unit.getBegin();
			int end = unit.getEnd();
			for (Map.Entry<String, String> annotatorIdAndClass : unit
					.getClassesByAnnotatorId().entrySet()) {
				String annotatorId = annotatorIdAndClass.getKey();
				String annotatorClass = annotatorIdAndClass.getValue();
				pw.format("%s\t%d\t%d\t%s\t%s%n", documentURI, begin, end,
						annotatorId, annotatorClass);
			}
		}
	}

	@Override
	public void addUnitsFromTSV(Reader reader) {
		// TODO Auto-generated method stub
	}

}
