/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao.impl;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ru.kfu.itis.issst.uima.consumer.cao.CasAccessObject;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MysqlJdbcCasAccessObject extends NamedParameterJdbcDaoSupport
		implements CasAccessObject {

	private static final String INSERT_DOCUMENT =
			"INSERT INTO DOCUMENT (uri, launch_id, size, processing_time) " +
					"VALUES (:uri, :launchId, :size, :processingTime)";
	private static final String INSERT_SPAN = "INSERT INTO SPAN (doc_id, txt) VALUES (:docId, :txt)";
	private static final String INSERT_ANNOTATION =
			"INSERT INTO ANNOTATION (anno_type, span_id, txt, start_offset, end_offset) " +
					"VALUES (:type, :spanId, :txt, :startOffset, :endOffset)";
	private static final String INSERT_FEATURE =
			"INSERT INTO FEATURE (owner_anno_id, value_anno_id, feature_name) " +
					"VALUES (:ownerAnnoId, :valueAnnoId, :name)";
	private static final String INSERT_LAUNCH =
			"INSERT INTO LAUNCH (started) VALUES (:started)";

	// SELECTS
	private static final String GET_TOP_ANNOS_BY_LAUNCH_AND_TYPE =
			"SELECT an.id AS annoId, an.anno_type AS annoType, " +
					"an.txt AS annoTxt, an.start_offset AS annoStartOffset, " +
					"an.end_offset AS annoEndOffset, doc.uri AS docURI " +
					"FROM annotation an JOIN span sp ON an.span_id=sp.id " +
					"JOIN document doc ON sp.doc_id=doc.id " +
					"WHERE anno_type IN (:types) " +
					"AND doc.launch_id IN (:launchIds)" +
					"ORDER BY an.id";

	@Override
	public void load(DataResource data) throws ResourceInitializationException {
		Properties dataSourceConfig = new Properties();
		InputStream is = null;
		try {
			is = data.getInputStream();
			try {
				dataSourceConfig.load(is);
			} finally {
				is.close();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		load(dataSourceConfig);
	}

	public void load(Properties configProps) {
		final BasicDataSource ds;
		try {
			ds = (BasicDataSource) BasicDataSourceFactory.createDataSource(configProps);
			setDataSource(ds);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Closing MysqlJdbcCasAccessObject...");
				try {
					ds.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */

	public long persistAnnotation(String type, long spanId, String coveredText, int startOffset,
			int endOffset) {
		KeyHolder genIdHolder = new GeneratedKeyHolder();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("spanId", spanId);
		params.put("txt", truncateString(coveredText, 512));
		params.put("startOffset", startOffset);
		params.put("endOffset", endOffset);
		getNamedParameterJdbcTemplate().update(INSERT_ANNOTATION, mapSource(params), genIdHolder);
		return genIdHolder.getKey().longValue();
	}

	/**
	 * {@inheritDoc}
	 */

	public void persistFeature(long ownerAnnoId, String featureName, long valueAnnoId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerAnnoId", ownerAnnoId);
		params.put("valueAnnoId", valueAnnoId);
		params.put("name", featureName);
		getNamedParameterJdbcTemplate().update(INSERT_FEATURE, params);
	}

	public long persistLaunch(Date startedTime) {
		KeyHolder genIdHolder = new GeneratedKeyHolder();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("started", startedTime);
		getNamedParameterJdbcTemplate().update(INSERT_LAUNCH, mapSource(params), genIdHolder);
		return genIdHolder.getKey().longValue();
	}

	/**
	 * {@inheritDoc}
	 */

	public long persistDocument(long launchId, String docURI, Long size, Long processingTime) {
		KeyHolder genIdHolder = new GeneratedKeyHolder();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uri", docURI);
		params.put("launchId", launchId);
		params.put("size", size);
		params.put("processingTime", processingTime);
		getNamedParameterJdbcTemplate().update(INSERT_DOCUMENT, mapSource(params), genIdHolder);
		return genIdHolder.getKey().longValue();
	}

	/**
	 * {@inheritDoc}
	 */

	public long persistSpan(long docId, String coveredText) {
		KeyHolder genIdHolder = new GeneratedKeyHolder();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("docId", docId);
		// TODO config?
		params.put("txt", truncateString(coveredText, 512));
		getNamedParameterJdbcTemplate().update(INSERT_SPAN, mapSource(params), genIdHolder);
		return genIdHolder.getKey().longValue();
	}

	@Override
	public List<AnnotationDTO> getTopAnnotationsByLaunch(Set<Integer> launchIds,
			Set<String> topAnnoTypes) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("types", topAnnoTypes);
		params.put("launchIds", launchIds);
		return getNamedParameterJdbcTemplate().query(GET_TOP_ANNOS_BY_LAUNCH_AND_TYPE,
				params, new AnnotationDTORowMapper());
	}

	private MapSqlParameterSource mapSource(Map<String, Object> paramsMap) {
		return new MapSqlParameterSource(paramsMap);
	}

	private String truncateString(String src, int maxLength) {
		if (src.length() > maxLength) {
			src = src.substring(0, maxLength - TRUNCATED_STRING_PREFIX.length());
			src = TRUNCATED_STRING_PREFIX + src;
			// sanity check
			if (src.length() > maxLength) {
				throw new IllegalStateException("assertion failed when truncate string");
			}
		}
		return src;
	}

	private static final String TRUNCATED_STRING_PREFIX = "%tr%";

	private class AnnotationDTORowMapper implements RowMapper<AnnotationDTO> {
		@Override
		public AnnotationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			AnnotationDTO result = new AnnotationDTO();
			result.setId(rs.getLong("annoId"));
			result.setType(rs.getString("annoType"));
			result.setTxt(rs.getString("annoTxt"));
			result.setStartOffset(rs.getInt("annoStartOffset"));
			result.setEndOffset(rs.getInt("annoEndOffset"));
			result.setDocUri(rs.getString("docURI"));
			return result;
		}
	}
}