package com.shaft.db;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.shaft.validation.Assertions;

import java.sql.*;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class TempDBTest {

	// Objects
	OracleDBConnection OracleDBConnection;

	// Variables
	Connection connection;

	@Test()
	@Description()
	@Severity(SeverityLevel.NORMAL)
	public void testConnection_insertNewRow_selectAndAssert() {
		// Insert new row
		OracleDBConnection.executeUpdateStatement(connection, "Insert into APEX_040200.abdelsalam values (3,'AutomationTest1')");
		
		// Select the new row and assert on it
		String Name = OracleDBConnection.executeSelectStatement(connection, "select * from APEX_040200.abdelsalam where name = 'AutomationTest1'");
		Assertions.assertEquals("AutomationTest1", Name.toString(), 1, true);
	}

	@BeforeClass
	public void beforeClass() {
		OracleDBConnection = new OracleDBConnection();
		connection = OracleDBConnection.openConnection();
	}

	@AfterClass
	public void afterClass() {
		OracleDBConnection.closeConnection(connection);
	}
}
