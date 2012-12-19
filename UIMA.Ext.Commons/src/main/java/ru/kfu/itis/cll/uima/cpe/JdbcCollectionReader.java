/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import static com.google.common.collect.Lists.newLinkedList;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.requireParam;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.requireParams;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class JdbcCollectionReader extends CasCollectionReader_ImplBase {

	public static final String PARAM_DATABASE_URL = "databaseUrl";
	public static final String PARAM_DRIVER_CLASS = "driverClass";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	
	public static final String PARAM_QUERY = "query";
	public static final String PARAM_OFFSET_PARAM_INDEX = "offsetParamIndex";
	public static final String PARAM_LIMIT_PARAM_INDEX = "limitParamIndex";
	public static final String PARAM_TEXT_COLUMN = "textColumn";
	public static final String PARAM_DOCUMENT_URL_COLUMN = "documentUrlColumn";
	
	public static final String PARAM_BATCH_SIZE = "batchSize";
	
	public static final String PARAM_COUNT_QUERY = "countQuery";
	
	@ConfigurationParameter(name = PARAM_DATABASE_URL, mandatory = true)
	private String dbUrl;
	@ConfigurationParameter(name = PARAM_USERNAME, mandatory = true)
	private String dbUsername;
	@ConfigurationParameter(name = PARAM_PASSWORD, mandatory = true)
	private String dbPassword;
	@ConfigurationParameter(name = PARAM_DRIVER_CLASS, mandatory = true)
	private String dbDriverClassName;
	@ConfigurationParameter(name = PARAM_QUERY, mandatory = true)
	private String query;
	@ConfigurationParameter(name = PARAM_COUNT_QUERY)
	private String countQuery;
	@ConfigurationParameter(name = PARAM_LIMIT_PARAM_INDEX, mandatory = true)
	private Integer limitParamIndex;
	@ConfigurationParameter(name = PARAM_OFFSET_PARAM_INDEX, mandatory = true)
	private Integer offsetParamIndex;
	@ConfigurationParameter(name = PARAM_DOCUMENT_URL_COLUMN)
	private String documentUrlColumn;
	@ConfigurationParameter(name = PARAM_TEXT_COLUMN, mandatory = true)
	private String textColumn;
	@ConfigurationParameter(name = PARAM_BATCH_SIZE, defaultValue = "20")
	private Integer batchSize;

	// state fields
	private Connection dbConnection;
	private PreparedStatement queryStatement;

	private Integer expectedTotalCount;
	private int consumedCount = 0;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		// check parameters
		requireParam(batchSize > 0, PARAM_BATCH_SIZE, batchSize);
		requireParams(limitParamIndex != offsetParamIndex
				&& limitParamIndex > 0 && limitParamIndex < 3
				&& offsetParamIndex > 0 && offsetParamIndex < 3,
				new String[] { PARAM_LIMIT_PARAM_INDEX, PARAM_OFFSET_PARAM_INDEX },
				new Object[] { limitParamIndex, offsetParamIndex });

		// initialize db connection && prepare statement
		try {
			Class.forName(dbDriverClassName);
			dbConnection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			queryStatement = dbConnection.prepareStatement(query);
			initTotalCount();
		} catch (Exception e) {
			closeQuietly(dbConnection);
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			queryStatement.close();
		} catch (SQLException e) {
		} finally {
			closeQuietly(dbConnection);
		}
		super.close();
	}

	private Iterator<DbTuple> dbIterator = new AbstractIterator<DbTuple>() {
		private int curOffset;
		private boolean lastBatchFetched = false;
		private Iterator<DbTuple> batchIterator;

		@Override
		protected DbTuple computeNext() {
			if (batchIterator == null) {
				makeNextBatchIterator();
			}
			if (batchIterator.hasNext()) {
				return batchIterator.next();
			} else if (lastBatchFetched) {
				return endOfData();
			} else {
				makeNextBatchIterator();
				return computeNext();
			}
		}

		private void makeNextBatchIterator() {
			try {
				List<DbTuple> batchList = queryBatch(curOffset, batchSize);
				curOffset += batchSize;
				if (batchList.isEmpty()) {
					lastBatchFetched = true;
				}
				batchIterator = batchList.iterator();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			// assertions
			if (batchIterator == null) {
				throw new IllegalStateException();
			}
		}
	};

	private List<DbTuple> queryBatch(int offset, int limit) throws SQLException, IOException {
		queryStatement.setInt(offsetParamIndex, offset);
		queryStatement.setInt(limitParamIndex, limit);
		ResultSet rs = queryStatement.executeQuery();
		try {
			LinkedList<DbTuple> result = newLinkedList();
			while (rs.next()) {
				result.add(toTuple(rs));
			}
			return ImmutableList.copyOf(result);
		} finally {
			rs.close();
		}
	}

	private DbTuple toTuple(ResultSet rs) throws SQLException, IOException {
		String url = rs.getString(documentUrlColumn);
		String text = null;
		Clob textClob = rs.getClob(textColumn);
		if (textClob != null) {
			Reader textReader = null;
			try {
				textReader = textClob.getCharacterStream();
				text = IOUtils.toString(textReader);
			} finally {
				IOUtils.closeQuietly(textReader);
				textClob.free();
			}
		}
		return new DbTuple(url, text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getNext(CAS cas) throws IOException, CollectionException {
		if (!dbIterator.hasNext()) {
			throw new NoSuchElementException();
		}
		DbTuple tuple = dbIterator.next();
		consumedCount++;
		cas.setDocumentText(tuple.text);
		try {
			DocumentMetadata docMeta = new DocumentMetadata(cas.getJCas());
			docMeta.setSourceUri(tuple.url);
			docMeta.addToIndexes();
		} catch (CASException e) {
			throw new CollectionException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return dbIterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Progress[] getProgress() {
		return new Progress[] {
				new ProgressImpl(consumedCount, expectedTotalCount, Progress.ENTITIES)
		};
	}

	private void initTotalCount() throws SQLException {
		if (countQuery == null) {
			return;
		}
		PreparedStatement countStmt = dbConnection.prepareStatement(countQuery);
		ResultSet rs = countStmt.executeQuery();
		if (rs.next()) {
			expectedTotalCount = rs.getInt(1);
		} else {
			getLogger().warn("Count query returned empty result set");
		}
	}

	private class DbTuple {
		DbTuple(String url, String text) {
			this.url = url;
			this.text = text;
		}

		private final String url;
		private final String text;
	}

	private static void closeQuietly(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
	}
}