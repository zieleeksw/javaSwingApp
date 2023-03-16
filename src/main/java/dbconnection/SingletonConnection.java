package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingletonConnection {
    private Connection connection;
    private  static SingletonConnection instance = new SingletonConnection();

    private SingletonConnection(){
        try{
            connection  = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/MyFitApp",
                    "root","");
            if(connection == null)
                System.out.println("Connection failed");
            else
                System.out.println("Connection successful");
        } catch (SQLException e){
            e.printStackTrace();

        }
    }

    public static SingletonConnection getInstance(){
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
