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
package com.aceql.client.jdbc.driver.metadata.dto;

import com.aceql.client.jdbc.driver.metadata.JdbcDatabaseMetaData;

public class JdbcDatabaseMetaDataDto {

    private String status = "OK";
    private JdbcDatabaseMetaData jdbcDatabaseMetaData = null;

    public JdbcDatabaseMetaDataDto(JdbcDatabaseMetaData jdbcDatabaseMetaData) {
	super();
	this.jdbcDatabaseMetaData = jdbcDatabaseMetaData;
    }

    public String getStatus() {
        return status;
    }

    public JdbcDatabaseMetaData getJdbcDatabaseMetaData() {
        return jdbcDatabaseMetaData;
    }

    @Override
    public String toString() {
	return "JdbcDatabaseMetaDataDto [status=" + status + ", jdbcDatabaseMetaData=" + jdbcDatabaseMetaData + "]";
    }


}
