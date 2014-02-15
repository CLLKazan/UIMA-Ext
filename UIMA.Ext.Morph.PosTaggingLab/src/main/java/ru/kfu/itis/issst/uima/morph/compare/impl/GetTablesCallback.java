/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.compare.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import com.beust.jcommander.internal.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class GetTablesCallback implements ConnectionCallback<Set<String>> {
	@Override
	public Set<String> doInConnection(Connection con) throws SQLException,
			DataAccessException {
		ResultSet tablesRS = con.getMetaData().getTables(
				null, null, "%", new String[] { "TABLE" });
		Set<String> tableNames = Sets.newHashSet();
		try {
			while (tablesRS.next()) {
				tableNames.add(tablesRS.getString("TABLE_NAME"));
			}
		} finally {
			tablesRS.close();
		}
		return tableNames;
	}
}
