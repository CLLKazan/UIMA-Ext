/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class HandlersAggregate {

	private File dictFile;

	protected HandlersAggregate(File dictFile) {
		this.dictFile = dictFile;
	}

	public void run() throws IOException {
		BufferedReader reader = Utils.reader(dictFile);
		List<LineHandler> handlers = getHandlers();

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				Iterator<LineHandler> hIter = handlers.iterator();
				while (hIter.hasNext() && line != null) {
					LineHandler curHandler = hIter.next();
					line = curHandler.handle(line);
				}
			}
		} finally {
			for (LineHandler handler : handlers) {
				try {
					handler.close();
				} catch (Exception e) {
				}
			}
			IOUtils.closeQuietly(reader);
		}
	}

	protected abstract List<LineHandler> getHandlers();
}