package help;

import java.sql.SQLException;

import database.Database;

public class EmptyDatabase {
	
	public static void main(String[] args) {
		Database database;
		try {
			database = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
			database.emptyTables();
			database.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
