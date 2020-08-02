/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.
 * Copyright (C) 2020,  KawanSoft SAS
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
package com.aceql.client.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aceql.client.jdbc.AceQLConnection;
import com.aceql.client.jdbc.AceQLException;
import com.aceql.client.test.connection.ConnectionBuilder;
import com.aceql.client.test.connection.ConnectionParms;
import com.aceql.client.test.util.Sha1;

/**
 *
 * @author Nicolas de Pomereu
 *
 */
public class AceQLConnectionTest {

    private static Connection connection;

    public static void main(String[] args) throws Exception {
	doIt();
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
	new File(ConnectionParms.IN_DIRECTORY).mkdirs();
	new File(ConnectionParms.OUT_DIRECTORY).mkdirs();

	boolean falseQuery = false;
	boolean doSelectOnRegions = false;
	boolean doInsertOnRegions = false;

	((AceQLConnection) connection).setTraceOn(true);
	((AceQLConnection) connection).setGzipResult(true);

	System.out.println();

	System.out.println("aceQLConnection.getServerVersion(): " + ((AceQLConnection) connection).getServerVersion());
	System.out.println("aceQLConnection.getClientVersion(): " + ((AceQLConnection) connection).getClientVersion());

	System.out.println("aceQLConnection.getAutoCommit() : " + connection.getAutoCommit());
	System.out.println("aceQLConnection.isReadOnly()    : " + connection.isReadOnly());
	System.out.println("aceQLConnection.getHoldability(): " + connection.getHoldability());
	System.out.println("aceQLConnection.getTransactionIsolation() : " + connection.getTransactionIsolation());

	// Close and reopen
	connection.close();
	connection = ConnectionBuilder.createOnConfig();
	((AceQLConnection) connection).setGzipResult(true);

	System.out.println("aceQLConnection.getTransactionIsolation() : " + connection.getTransactionIsolation());

	connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
	connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

	connection.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
	connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);

	SqlInsertTest sqlInsertTest = new SqlInsertTest(connection, System.out);
	SqlDeleteTest sqlDeleteTest = new SqlDeleteTest(connection, System.out);
	SqlSelectTest sqlSelectTest = new SqlSelectTest(connection, System.out);
	SqlBlobTest sqlBlobTest = new SqlBlobTest(connection, System.out);

	int records = 300;

	sqlDeleteTest.deleteCustomerAll();

	connection.setAutoCommit(false);
	sqlInsertTest.loopInsertCustomer(records);

	if (doInsertOnRegions) {
	    sqlInsertTest.insertInRegions(connection);
	}

	connection.commit();

	sqlSelectTest.selectOrderlogStatement();
	connection.setAutoCommit(true);

	sqlSelectTest.selectCustomerPreparedStatement();

	falseQuery(falseQuery, sqlSelectTest);

	File fileUpload = new File(ConnectionParms.IN_DIRECTORY + File.separator + "username_koala.jpg");
	File fileDownload = new File(ConnectionParms.OUT_DIRECTORY + File.separator + "username_koala.jpg");
	int customerId = 1;
	int itemId = 1;

	blobUpload(sqlDeleteTest, sqlBlobTest, fileUpload, customerId, itemId);
	blobDownload(sqlBlobTest, customerId, itemId, fileDownload);
	checkBlobIntegrity(fileUpload, fileDownload);

	if (doSelectOnRegions) {
	    sqlSelectTest.selectOnRegions();
	}
    }

    /**
     * @param falseQuery
     * @param sqlSelectTest
     */
    private static void falseQuery(boolean falseQuery, SqlSelectTest sqlSelectTest) {
	if (falseQuery) {
	    try {
		sqlSelectTest.selectOnTableNotExists();
	    } catch (Exception e) {
		System.err.println(e);
	    }
	}
    }

    /**
     * @param fileUpload
     * @param fileDownload
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private static void checkBlobIntegrity(File fileUpload, File fileDownload)
	    throws NoSuchAlgorithmException, IOException {
	// Compare the in file and the out file
	String sha1In = Sha1.getSha1(fileUpload);
	String sha1Out = Sha1.getSha1(fileDownload);

	if (sha1In.equals(sha1Out)) {
	    System.out.println();
	    System.out.println("Blob upload & downoad sucess! sha1In & sha1Out match! :" + sha1In);
	} else {
	    System.err.println("sha1In: " + sha1In);
	    System.err.println("sha1Out: " + sha1Out);
	    throw new IOException("fileUpload & fileUpload hash do not match!");
	}
    }

    /**
     * @param sqlBlobTest
     * @param customerId
     * @param itemId
     * @param fileDownload
     * @throws SQLException
     * @throws IOException
     */
    private static void blobDownload(SqlBlobTest sqlBlobTest, int customerId, int itemId, File fileDownload)
	    throws SQLException, IOException {
	connection.setAutoCommit(false); // Must be in Autocommit false with PostgreSQL
	sqlBlobTest.blobDownload(customerId, itemId, fileDownload);
    }

    /**
     * @param sqlDeleteTest
     * @param sqlBlobTest
     * @param fileUpload
     * @param customerId
     * @param itemId
     * @throws SQLException
     * @throws FileNotFoundException
     */
    private static void blobUpload(SqlDeleteTest sqlDeleteTest, SqlBlobTest sqlBlobTest, File fileUpload,
	    int customerId, int itemId) throws SQLException, FileNotFoundException {
	connection.setAutoCommit(true);
	sqlDeleteTest.deleteOrderlogAll();

	connection.setAutoCommit(false); // Must be in Autocommit false with PostgreSQL
	sqlBlobTest.blobUpload(customerId, itemId, fileUpload);
	connection.commit();
    }

}