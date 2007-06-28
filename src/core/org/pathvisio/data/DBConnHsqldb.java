// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import org.pathvisio.debug.StopWatch;
import org.pathvisio.gui.swt.Engine;

/**
   DBConnector implementation using the hsqldb driver
*/
public class DBConnHsqldb extends DBConnector
{
	static final String DB_FILE_EXT = "properties";
	static final String[] DB_EXT = new String[] { "*." + DB_FILE_EXT };
	static final String[] DB_EXT_NAMES = new String[] { "Hsqldb Database" };
	
	public Connection createConnection(String dbName) throws Exception {
		return createConnection(dbName, PROP_NONE);
	}
	
	public Connection createConnection(String dbName, int props) throws Exception {
		boolean recreate = (props & PROP_RECREATE) != 0;
		if(recreate) {
			File dbFile = dbName2File(dbName);
			if(dbFile.exists()) dbFile.delete();
		}
		
		dbName = file2DbName(dbName);
		
		Class.forName("org.hsqldb.jdbcDriver");
		Properties prop = new Properties();
		prop.setProperty("user","sa");
		prop.setProperty("password","");
		prop.setProperty("hsqldb.default_table_type", "cached");
		prop.setProperty("ifexists", Boolean.toString(!recreate));
		
		StopWatch timer = new StopWatch();
		timer.start();
		Connection con = DriverManager.getConnection("jdbc:hsqldb:file:" + dbName, prop);
		Engine.log.info("Connecting with hsqldb to " + dbName + ":\t" + timer.stop());
		return con;
	}

	public void closeConnection(Connection con) throws SQLException {
		closeConnection(con, PROP_NONE);
	}
	
	public void closeConnection(Connection con, int props) throws SQLException {
		boolean compact = (props & PROP_FINALIZE) != 0;
		if(con != null) {
			Statement sh = con.createStatement();
			sh.executeQuery("SHUTDOWN" + (compact ? " COMPACT" : ""));
			sh.close();
			con.close();
		}
	}
	
	File dbName2File(String dbName) {
		return new File(dbName + '.' + DB_FILE_EXT);
	}
	
	String file2DbName(String fileName) {
		String end = '.' + DB_FILE_EXT;
		return fileName.endsWith(end) ? 
				fileName.substring(0, fileName.length() -  end.length()) : fileName;
	}
	
	public void setDatabaseReadonly(String dbName, boolean readonly) {
		 setPropertyReadOnly(dbName, readonly);
	}
	
	void setPropertyReadOnly(String dbName, boolean readonly) {
    	Properties prop = new Properties();
		try {
			File propertyFile = dbName2File(dbName);
			prop.load(new FileInputStream(propertyFile));
			prop.setProperty("hsqldb.files_readonly", Boolean.toString(readonly));
			prop.store(new FileOutputStream(propertyFile), "HSQL Database Engine");
			} catch (Exception e) {
				Engine.log.error("Unable to set database properties to readonly", e);
			}
	}

	Connection newDbCon;
	public Connection createNewDatabaseConnection(String dbName) throws Exception {
		newDbCon = createConnection(dbName, PROP_RECREATE);
		return newDbCon;
	}

	public String finalizeNewDatabase(String dbName) throws Exception {
		if(newDbCon != null) closeConnection(newDbCon, PROP_FINALIZE);
		setPropertyReadOnly(dbName, true);
		return dbName;
	}

	public String openChooseDbDialog(Shell shell) {
		FileDialog fd = createFileDialog(shell, SWT.OPEN, DB_EXT, DB_EXT_NAMES);
		return fd.open();
	}

	public String openNewDbDialog(Shell shell, String defaultName) {
		FileDialog fd = createFileDialog(shell, SWT.SAVE, DB_EXT, DB_EXT_NAMES);
		if(defaultName != null) fd.setFileName(defaultName);
		return fd.open();
	}
}
