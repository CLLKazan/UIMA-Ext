/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_BATCH_SIZE;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_COUNT_QUERY;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_DATABASE_URL;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_DOCUMENT_URL_COLUMN;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_DRIVER_CLASS;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_LIMIT_PARAM_INDEX;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_OFFSET_PARAM_INDEX;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_PASSWORD;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_QUERY;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_TEXT_COLUMN;
import static ru.kfu.itis.cll.uima.cpe.JdbcCollectionReader.PARAM_USERNAME;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.uima.fit.pipeline.SimplePipeline;

import ru.kfu.itis.cll.uima.annotator.AnnotationLogger;

import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class JdbcCollectionReaderTest {

	@BeforeClass
	public static void initDB() throws Exception {
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		Connection con = DriverManager.getConnection(
				"jdbc:hsqldb:mem:jdbc-collection-reader-test",
				"SA", "");
		try {
			Statement createTable = con.createStatement();
			createTable.execute("CREATE TABLE doc (" +
					"id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
					"url VARCHAR(256) UNIQUE NOT NULL, " +
					"txt CLOB)");
			createTable.close();

			PreparedStatement insert = con.prepareStatement(
					"INSERT INTO doc(url, txt) VALUES (?,?)");

			insert.setString(1, "http://example.com/01");
			insert.setClob(2, new StringReader(generateString(200)));
			insert.execute();

			insert.setString(1, "file://test/test.txt");
			insert.setClob(2, new StringReader(generateString(200)));
			insert.execute();

			insert.setString(1, "ftp://test.com/02434523");
			insert.setClob(2, new StringReader(generateString(200)));
			insert.execute();

			insert.close();
		} finally {
			con.close();
		}
	}

	@Test
	public void test() throws UIMAException, IOException {
		TypeSystemDescription tsDesc = createTypeSystemDescription("ru.kfu.itis.cll.uima.commons.Commons-TypeSystem");
		CollectionReaderDescription readerDesc =
				createReaderDescription(JdbcCollectionReader.class, tsDesc,
                        PARAM_DATABASE_URL,
                        "jdbc:hsqldb:mem:jdbc-collection-reader-test;ifexists=true",
                        PARAM_USERNAME, "SA",
                        PARAM_PASSWORD, "",
                        PARAM_DRIVER_CLASS, "org.hsqldb.jdbc.JDBCDriver",
                        PARAM_QUERY, "SELECT url, txt FROM doc ORDER BY id OFFSET ? LIMIT ?",
                        PARAM_OFFSET_PARAM_INDEX, 1,
                        PARAM_LIMIT_PARAM_INDEX, 2,
                        PARAM_DOCUMENT_URL_COLUMN, "url",
                        PARAM_TEXT_COLUMN, "txt",
                        PARAM_BATCH_SIZE, 2,
                        PARAM_COUNT_QUERY, "SELECT count(*) FROM doc");
		AnalysisEngineDescription aeDesc = createEngineDescription(AnnotationLogger.class);
		SimplePipeline.runPipeline(readerDesc, aeDesc);
	}

	// TODO move to TestUtils or smth like that
	private static final List<Character> ALPHABET;
	static {
		List<Character> alphabet = new LinkedList<Character>();
		for (char ch = 'a'; ch <= 'z'; ch++) {
			alphabet.add(ch);
			alphabet.add(Character.toUpperCase(ch));
		}
		for (char ch = 'а'; ch <= 'я'; ch++) {
			alphabet.add(ch);
			alphabet.add(Character.toUpperCase(ch));
		}
		alphabet.add(' ');
		ALPHABET = ImmutableList.copyOf(alphabet);
	}

	private static String generateString(int length) {
		StringBuilder result = new StringBuilder(length);
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			result.append(ALPHABET.get(r.nextInt(ALPHABET.size())));
		}
		return result.toString();
	}
}