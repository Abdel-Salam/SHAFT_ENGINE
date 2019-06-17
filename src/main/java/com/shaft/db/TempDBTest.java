package com.shaft.db;

import java.sql.Connection;

import org.testng.annotations.Test;

import com.shaft.validation.Assertions;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class TempDBTest {

	// Objects
	OracleDBConnection OracleDBConnection;
	SQLServerDBConnection SQLServerDBConnection;
	PostgreSQLDBConnection PostgreSQLDBConnection;
	// Variables
	Connection connection;

	@Test
	@Description
	@Severity(SeverityLevel.NORMAL)
	public void testOracleConnection_insertNewRow_selectAndAssert() {
		// Open connection
		OracleDBConnection = new OracleDBConnection();
		connection = OracleDBConnection.openConnection();

		// Insert new row
		OracleDBConnection.executeUpdateStatement(connection,
				"Insert into APEX_040200.abdelsalam values (3,'AutomationTest2')");

		// Select the new row and assert on it
		String Name = OracleDBConnection.executeSelectStatement(connection,
				"select * from APEX_040200.abdelsalam where name = 'AutomationTest2'");
		Assertions.assertEquals("AutomationTest2", Name.toString(), 1, true);

		// Close connection
		OracleDBConnection.closeConnection(connection);
	}

	@Test()
	@Description()
	@Severity(SeverityLevel.NORMAL)
	public void testSQLServerConnection_insertNewRow_selectAndAssert() {
		// Open connection
		SQLServerDBConnection = new SQLServerDBConnection();
		connection = SQLServerDBConnection.openConnection();

		// Insert new row
		SQLServerDBConnection.executeUpdateStatement(connection,
				"Insert into abdelsalam.dbo.automation_table values (2,'AutomationTest3')");

		// Select the new row and assert on it
		String Name = SQLServerDBConnection.executeSelectStatement(connection,
				"select * from abdelsalam.dbo.automation_table where name = 'AutomationTest3'");
		Assertions.assertEquals("AutomationTest3", Name.toString().trim(), 1, true);

		// Close connection
		SQLServerDBConnection.closeConnection(connection);
	}

	@Test()
	@Description()
	@Severity(SeverityLevel.NORMAL)
	public void testPostgreSQLConnection_insertNewRow_selectAndAssert() {
		// Open connection
		PostgreSQLDBConnection = new PostgreSQLDBConnection();
		connection = PostgreSQLDBConnection.openConnection();

		// Insert new row
		PostgreSQLDBConnection.executeUpdateStatement(connection,
				"INSERT INTO account (id, name) VALUES (1, 'ahmed')");

		// Select the new row and assert on it
		String Name = PostgreSQLDBConnection.executeSelectStatement(connection,
				"select * from account");
		Assertions.assertEquals("ahmed", Name.toString().trim(), 1, true);

		// Close connection
		PostgreSQLDBConnection.closeConnection(connection);
	}
}
