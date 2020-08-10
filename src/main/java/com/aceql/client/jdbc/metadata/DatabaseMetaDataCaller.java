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
package com.aceql.client.jdbc.metadata;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.aceql.client.jdbc.AceQLConnection;
import com.aceql.client.jdbc.AceQLConnectionWrapper;
import com.aceql.client.jdbc.AceQLException;
import com.aceql.client.jdbc.http.AceQLHttpApi;
import com.aceql.client.metadata.util.GsonWsUtil;

/**
 * Utility class to call remote DatabaseMetaData methods.
 *
 * @author Nicolas de Pomereu
 *
 */
public class DatabaseMetaDataCaller {

    private AceQLHttpApi aceQLHttpApi;

    /**
     * Constructor.
     *
     * @param aceQLConnection
     */
    public DatabaseMetaDataCaller(AceQLConnection aceQLConnection) {
	AceQLConnectionWrapper aceQLConnectionWrapper = new AceQLConnectionWrapper(aceQLConnection);
	this.aceQLHttpApi = aceQLConnectionWrapper.getAceQLHttpApi();
    }

    public Object callMetaDataMethod(String methodName, Object... params) throws SQLException {

	try {

	    Objects.requireNonNull(methodName, "methodName cannot be null!");

	    String returnType = null;
	    try {
		returnType = getMethodReturnType(methodName);
	    } catch (Exception e) {
		throw new SQLException(e);
	    }

	    // Build the params types & values
	    DatabaseMetaDataParamsBuilder databaseMetaDataParamsBuilder = new DatabaseMetaDataParamsBuilder(methodName,
		    params);
	    databaseMetaDataParamsBuilder.build();

	    List<String> paramTypes = databaseMetaDataParamsBuilder.getParamTypes();
	    List<String> paramValues = databaseMetaDataParamsBuilder.getParamValues();

	    // Build the DTO
	    DatabaseMetaDataMethodCallDTO databaseMetaDataMethodCallDTO = new DatabaseMetaDataMethodCallDTO(methodName,
		    paramTypes, paramValues);
	    // Transform to Json
	    String jsonDatabaseMetaDataMethodCallDTO = GsonWsUtil.getJSonString(databaseMetaDataMethodCallDTO);
	    // Build the Http Call and get the result as a file

	    InputStream in = aceQLHttpApi.callDatabaseMetaDataMethod(jsonDatabaseMetaDataMethodCallDTO);

	    // Get the result and return a Boolean or ResultSet (ResultSetHttp
	    // in fact)
	    if (returnType.endsWith("boolean")) {
		String booleanStr = null; // TODO: implement call to server
		return Boolean.parseBoolean(booleanStr);
	    } else if (returnType.endsWith("ResultSet")) {
		// Ok, build the result set from the file:
		ResultSet rs = null; // TODO: implement call to server
		return rs;
	    } else {
		throw new IllegalArgumentException(
			"Unsupported return type for DatabaseMetaData." + methodName + ": " + returnType);
	    }

	} catch (Exception e) {
	    if (e instanceof AceQLException) {
		throw (AceQLException) e;
	    } else {
		throw new AceQLException(e.getMessage(), 0, e, null, aceQLHttpApi.getHttpStatusCode());
	    }
	}

    }


    /**
     * Get the return type of a DatabaseMetaData method
     *
     * @param methodName the DatabaseMetaData method
     * @return the return type
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static String getMethodReturnType(String methodName)
	    throws ClassNotFoundException, SecurityException, NoSuchMethodException {

	Class<?> c = Class.forName("java.sql.DatabaseMetaData");
	Method[] allMethods = c.getDeclaredMethods();

	for (Method m : allMethods) {
	    if (m.getName().endsWith(methodName)) {
		String returnType = m.getReturnType().toString();

		if (returnType.startsWith("class ")) {
		    returnType = StringUtils.substringAfter(returnType, "class ");
		}
		if (returnType.startsWith("interface ")) {
		    returnType = StringUtils.substringAfter(returnType, "interface ");
		}

		return returnType;
	    }
	}

	throw new NoSuchMethodException("DatabaseMetaData does not contain this method: " + methodName);
    }

}
