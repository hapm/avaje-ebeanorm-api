/**
 * Copyright (C) 2006  Robin Bygrave
 * 
 * This file is part of Ebean.
 * 
 * Ebean is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *  
 * Ebean is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Ebean; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA  
 */
package com.avaje.ebean.config.dbplatform;

import java.sql.Types;

import javax.sql.DataSource;

import com.avaje.ebean.BackgroundExecutor;
import com.avaje.ebean.config.GlobalProperties;

/**
 * H2 specific platform.
 */
public class HsqldbPlatform extends DatabasePlatform {

    public HsqldbPlatform(){
        super();
        this.name = "hsqldb";
        this.dbEncrypt = new H2DbEncrypt();
        
        // only support getGeneratedKeys with non-batch JDBC 
        // so generally use SEQUENCE instead of IDENTITY for H2
        boolean useIdentity = GlobalProperties.getBoolean("ebean.hsqldb.useIdentity", true);
        
        IdType idType = useIdentity ? IdType.IDENTITY : IdType.SEQUENCE;
        this.dbIdentity.setIdType(idType);        
        
        this.dbIdentity.setSupportsGetGeneratedKeys(true);
        this.dbIdentity.setSupportsSequence(true);
        this.dbIdentity.setSupportsIdentity(true);

        this.openQuote = "\"";
        this.closeQuote = "\"";
        
        // H2 data types match default JDBC types
        // so no changes to dbTypeMap required
        dbTypeMap.put(Types.INTEGER, new DbType("integer",false));
        
        this.dbDdlSyntax.setDropIfExists("if exists");
        this.dbDdlSyntax.setDisableReferentialIntegrity("SET DATABASE REFERENTIAL INTEGRITY FALSE");
        this.dbDdlSyntax.setEnableReferentialIntegrity("SET DATABASE REFERENTIAL INTEGRITY TRUE");
        this.dbDdlSyntax.setForeignKeySuffix("on delete restrict on update restrict");
        this.dbDdlSyntax.setIdentity("GENERATED BY DEFAULT AS IDENTITY (START WITH 1) ");
    }

    /**
     * Return a H2 specific sequence IdGenerator that supports batch fetching
     * sequence values.
     */
	@Override
	public IdGenerator createSequenceIdGenerator(BackgroundExecutor be,
			DataSource ds, String seqName, int batchSize) {
		
		return new H2SequenceIdGenerator(be, ds, seqName, batchSize);
	}    
    
}
