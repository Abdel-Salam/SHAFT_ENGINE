package com.shaft.db;

import java.sql.*;

/**
 * 
 * @author abdelsalam
 *
 */
public class PostgreSQLDBConnection {

	public PostgreSQLDBConnection() {
		// Register driver
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return Connection session
	 */
	public Connection openConnection() {
		try {
			String dbURL = "jdbc:postgresql://72.55.136.25:5432/demo";
			String user = "dev";
			String pass = "dev_incorta";
			Connection connection = DriverManager.getConnection(dbURL, user, pass);
			return connection;
		} catch (SQLException e) {
			System.out.println("Please make sure SQLServer connection is configured correctly");
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
			PreparedStatement update = connection.prepareStatement(updateQuery);
			update.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
