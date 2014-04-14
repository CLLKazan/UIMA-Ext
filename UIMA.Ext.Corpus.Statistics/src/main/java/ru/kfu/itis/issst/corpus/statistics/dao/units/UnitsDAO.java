package ru.kfu.itis.issst.corpus.statistics.dao.units;

import java.io.Reader;
import java.io.Writer;
import java.net.URI;

public interface UnitsDAO {

	void addUnitItem(URI documentURI, int begin, int end, String annotatorId,
			String annotatorClass);
	
	Iterable<Unit> getUnits();

	void toTSV(Writer writer);

	void addUnitsFromTSV(Reader reader);

}
