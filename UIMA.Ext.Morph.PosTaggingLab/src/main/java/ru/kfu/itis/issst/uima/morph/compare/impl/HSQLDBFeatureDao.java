/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.compare.impl;

import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import com.beust.jcommander.internal.Maps;

import ru.kfu.itis.issst.uima.morph.compare.FeatureDao;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class HSQLDBFeatureDao extends NamedParameterJdbcDaoSupport implements FeatureDao {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final String tableName;
	// statements
	private final String insertStringFeature;

	public HSQLDBFeatureDao(DataSource ds, String tableName) {
		this.tableName = tableName;
		setDataSource(ds);
		//
		insertStringFeature = String.format(
				"INSERT INTO %s (fsId, stringValue) VALUES (:fsId, :stringValue)",
				this.tableName);
	}

	@Override
	protected void initTemplateConfig() {
		super.initTemplateConfig();
		// check whether the target table exists using JDBC metadata
		Set<String> tableNames = getJdbcTemplate().execute(new GetTablesCallback());
		if (!tableNames.contains(this.tableName)) {
			log.info("Creating DB table '{}'", tableName);
			// create table named by annotationType
			getJdbcTemplate().execute(
					"CREATE TABLE " + tableName + " (fsId INTEGER PRIMARY KEY, "
							+ "stringValue VARCHAR(256)"
							+ ")");
		}
	}

	@Override
	public void saveString(long fsId, String value) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("fsId", fsId);
		paramMap.put("stringValue", value);
		getNamedParameterJdbcTemplate().update(insertStringFeature, paramMap);
	}

}
