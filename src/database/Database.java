package database;

public class Database {
	
	private static int i =0;
	
	public int getNextLACID(){
		return i++;
	}

}
