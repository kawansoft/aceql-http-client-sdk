/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aceql.jdbc.commons.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import com.aceql.jdbc.commons.AceQLConnection;
import com.aceql.jdbc.commons.AceQLException;
import com.aceql.jdbc.commons.test.connection.ConnectionBuilder;

/**
 *
 * @author Nicolas de Pomereu
 *
 */
public class AceQLDatabaseMetaDataTest {

    private static Connection connection;

    public static void main(String[] args) throws Exception {
	try {
	    doIt();
	} finally {
	    if (connection != null) {
		connection.close();
	    }
	}
    }

    /**
     * Test main SQL orders.
     *
     * @throws SQLException
     * @throws AceQLException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void doIt()
	    throws SQLException, AceQLException, FileNotFoundException, IOException, NoSuchAlgorithmException {

	connection = ConnectionBuilder.createOnConfig();

	((AceQLConnection) connection).setTraceOn(false);
	//((AceQLConnection) connection).setFillResultSetMetaData(true);

	System.out.println(new Date() + " Begin");

	System.out.println("aceQLConnection.getServerVersion(): " + ((AceQLConnection) connection).getServerVersion());
	System.out.println("aceQLConnection.getClientVersion(): " + ((AceQLConnection) connection).getClientVersion());
	System.out.println();
	System.out.println("connection.getCatalog(): " + connection.getCatalog());

	//testResultSetMetaData();

	// Futur usage
	System.out.println("connection.getSchema() : " + connection.getSchema());
	System.out.println();

	boolean testAllMetaData = true;
	if (! testAllMetaData) {
	    return;
	}

	DatabaseMetaData databaseMetaData = connection.getMetaData();
	System.out
		.println("databaseMetaData.allProceduresAreCallable(): " + databaseMetaData.allProceduresAreCallable());

	System.out.println();
	System.out.println("databaseMetaData.getClientInfoProperties():");
	ResultSet rs = databaseMetaData.getClientInfoProperties();
	while (rs.next()) {
	    String name = rs.getString(1);
	    System.out.println("NAME   : " + name);
	    int maxLen = rs.getInt(2);
	    System.out.println("MAX_LEN: " + maxLen);
	    System.out.println();
	}

	System.out.println("databaseMetaData.getTableTypes():");
	rs = databaseMetaData.getTableTypes();

	while (rs.next()) {
	    String tableType = rs.getString(1);
	    System.out.println("tableType: " + tableType);
	}


	String schema = null;
	String catalog = null;

	System.out.println();
	System.out.println("databaseMetaData.getTables():");
	String[] types = { "TABLE", "VIEW" };
	String tableNamePattern = null; // List all tables
	rs = databaseMetaData.getTables(catalog, schema, tableNamePattern, types);
	while (rs.next()) {
	    String tableName = rs.getString(3);
	    String tableType = rs.getString(4);
	    System.out.println("TABLE_NAME: " + tableName);
	    System.out.println("TABLE_TYPE: " + tableType);
	    System.out.println();
	}

	System.out.println();
	System.out.println("databaseMetaData.getColumns():");
	rs = databaseMetaData.getColumns(schema, catalog, null, "customer_id");

	while (rs.next()) {
	    String tableName = rs.getString(3);
	    String colName = rs.getString(4);
	    String dataType = rs.getString(5);
	    System.out.println("TABLE_NAME : " + tableName);
	    System.out.println("COLUMN_NAME: " + colName);
	    System.out.println("DATA_TYPE  : " + dataType);
	    System.out.println();
	}

	rs.close();

	System.out.println(new Date() + " End");

    }

    public static void testResultSetMetaData() throws SQLException {
	String sql = "select * from customer where customer_id >= ? order by customer_id";
	PreparedStatement preparedStatement = connection.prepareStatement(sql);

	preparedStatement.setInt(1, 1);
	ResultSet rs = preparedStatement.executeQuery();

	if (rs.next()) { // Ask only one row
	    System.out.println();
	    System.out.println("customer_id   : " + rs.getInt("customer_id"));
	    System.out.println("customer_title: " + rs.getString("customer_title"));
	    System.out.println("fname         : " + rs.getString("fname"));
	}

	ResultSetMetaData resultSetMetaData = rs.getMetaData();
	if (resultSetMetaData == null) {
	    System.out.println("resultSetMetaData is null!");
	}
	else {

	    int count = resultSetMetaData.getColumnCount();
	    System.out.println("resultSetMetaData.getColumnCount(): " + count);

	    for (int i = 1; i < count + 1; i++) {
		System.out.println();
		System.out.println("resultSetMetaData.getColumnName(i)    : " + resultSetMetaData.getColumnName(i));
		System.out.println("resultSetMetaData.getColumnLabel(i)   : " + resultSetMetaData.getColumnLabel(i));
		System.out.println("resultSetMetaData.getColumnType(i)    : " + resultSetMetaData.getColumnType(i));
		System.out.println("resultSetMetaData.getColumnTypeName(i): " + resultSetMetaData.getColumnTypeName(i));
	    }
	}

    }

}