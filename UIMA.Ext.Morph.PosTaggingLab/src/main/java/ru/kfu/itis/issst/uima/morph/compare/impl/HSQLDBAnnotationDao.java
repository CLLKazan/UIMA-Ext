package ru.kfu.itis.issst.uima.morph.compare.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ru.kfu.itis.issst.uima.morph.compare.AnnotationDao;
import ru.kfu.itis.issst.uima.morph.compare.EnclosedAnnotationEntity;

import com.beust.jcommander.internal.Maps;

public class HSQLDBAnnotationDao extends NamedParameterJdbcDaoSupport implements AnnotationDao {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final String tableName;
	// derived
	private final String selectByOffsets;
	private final String selectById;
	private final String insertAnnotation;

	public HSQLDBAnnotationDao(DataSource ds, String annotationTypeSimpleName) {
		this.tableName = annotationTypeSimpleName;
		setDataSource(ds);
		// prepare query strings
		selectByOffsets = String.format(
				"SELECT id, docUri, begin, end, coveredText, enclosingAnnotationId FROM %s "
						+ "WHERE docUri = :docUri AND begin = :begin AND end = :end",
				tableName);
		selectById = String
				.format(
						"SELECT id, docUri, begin, end, coveredText, enclosingAnnotationId FROM %s WHERE id = :id",
						tableName);
		insertAnnotation = String.format(
				"INSERT INTO %s (docUri, begin, end, coveredText, enclosingAnnotationId) "
						+ "VALUES (:docUri, :begin, :end, :coveredText, :enclosingAnnotationId)",
				tableName);
	}

	@Override
	protected void initTemplateConfig() {
		super.initTemplateConfig();
		// check whether the target table exists using JDBC metadata
		Set<String> tableNames = getJdbcTemplate().execute(new GetTablesCallback());
		if (!tableNames.contains(this.tableName.toUpperCase())) {
			log.info("Creating DB table '{}'", tableName);
			// create table named by annotationType
			getJdbcTemplate().execute(
					"CREATE TABLE " + tableName
							+ " (id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
							+ "docUri VARCHAR(256) NOT NULL, "
							+ "begin INTEGER NOT NULL, "
							+ "end INTEGER NOT NULL, "
							+ "coveredText VARCHAR(64), "
							+ "enclosingAnnotationId INTEGER"
							+ ")");
			getJdbcTemplate().execute(
					"CREATE INDEX " + tableName + "_doc_offset_index ON " + tableName
							+ "(docUri, begin, end)");
		}
	}

	@Override
	public EnclosedAnnotationEntity getAnnotation(String docUri, int begin, int end) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("docUri", docUri);
		paramMap.put("begin", begin);
		paramMap.put("end", end);
		return extractSingleResult(getNamedParameterJdbcTemplate().query(selectByOffsets, paramMap,
				new AnnotationEntityMapper()));
	}

	@Override
	public EnclosedAnnotationEntity getAnnotation(long id) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("id", id);
		return extractSingleResult(getNamedParameterJdbcTemplate().query(selectById, paramMap,
				new AnnotationEntityMapper()));
	}

	@Override
	public EnclosedAnnotationEntity save(EnclosedAnnotationEntity annoEntity) {
		KeyHolder genIdHolder = new GeneratedKeyHolder();
		getNamedParameterJdbcTemplate().update(insertAnnotation,
				new BeanPropertySqlParameterSource(annoEntity), genIdHolder);
		long id = genIdHolder.getKey().longValue();
		return new EnclosedAnnotationEntity(id, annoEntity);
	}

	static class AnnotationEntityMapper implements RowMapper<EnclosedAnnotationEntity> {
		@Override
		public EnclosedAnnotationEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
			long id = rs.getLong("id");
			String docUri = rs.getString("docUri");
			int begin = rs.getInt("begin");
			int end = rs.getInt("end");
			String coveredText = rs.getString("coveredText");
			Long enclosingAnnotationId = rs.getLong("enclosingAnnotationId");
			if (rs.wasNull()) {
				enclosingAnnotationId = null;
			}
			return new EnclosedAnnotationEntity(id,
					docUri, begin, end, coveredText,
					enclosingAnnotationId);
		}
	}

	private static <E> E extractSingleResult(List<E> resultList) {
		if (resultList == null || resultList.isEmpty()) {
			return null;
		}
		if (resultList.size() > 1) {
			throw new IllegalStateException(String.format("Too much results"));
		}
		return resultList.get(0);
	}
}
