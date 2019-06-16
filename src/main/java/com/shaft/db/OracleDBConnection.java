package com.shaft.db;

import java.sql.*;

/**
 * 
 * @author abdelsalam
 *
 */
public class OracleDBConnection {

	public OracleDBConnection() {
		// Register driver
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("As per sara request");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return Connection session
	 */
	public Connection openConnection() {
		try {
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@72.55.136.25:1523:XE", "system",
					"oracle");
			return connection;
		} catch (SQLException e) {
			System.out.println("Please make sure oracle connection is configured correctly");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param connection connection retrieved from openConnection function
	 */
	public void closeConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param connection
	 * @param query      query to be executed
	 * @return
	 */
	public String executeSelectStatement(Connection connection, String query) {
		String X = null;
		try {
			// Create Statement
			Statement stmt = connection.createStatement();
			// Execute query
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
			X = rs.getString("name");
			}
			
			return X;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void executeUpdateStatement(Connection connection, String updateQuery) {
		try {
			PreparedStatement updateSales = connection.prepareStatement(updateQuery);
			updateSales.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
