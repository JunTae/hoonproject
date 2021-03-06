package socialUp.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.ibatis.session.SqlSession;
import socialUp.common.mybatis.MyBatisManager;

public class DBHandler {

	public static void initDriver(String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.out.println("==============================================================");
			System.out.println("FATAL : JDBC Driver Loading fail....");
			System.out.println("ERROR : " + e.toString());
			System.out.println("==============================================================");
		}
	}
	
	public static void initSqlMap(String sqlMapConfigList) {
		try {
			
			// 리소스 파일의 기본값을 변경해서 해당 파일이 존재하는지 초기화를 한번 해봄으로써 확인 해본다.
			MyBatisManager.setDefaultRsourceName(sqlMapConfigList);
			SqlSession temp =  MyBatisManager.getInstanceSqlSession("");
			temp.close();

		} catch (Exception e) {
			System.out.println("==============================================================");
			System.out.println(" FATAL : SqlMapLoader.init() ");
			System.out.println(" File : " + sqlMapConfigList);
			System.out.println(" Error : " + e.toString());
			System.out.println("==============================================================");
		}
	}

	public static void closeDBResource(Statement stmt){
		if(stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}

	public static void closeDBResource(ResultSet rs){
		if (rs != null) {
			try {
				rs.close();
			} catch(Exception e) {
				System.out.println(e.toString());
			}
		}
	}

	public static void closeDBResource(ResultSet rs, Statement stmt) {
		closeDBResource(rs);
		closeDBResource(stmt);
	}

	public static void closeDBResource(Connection conn){
		if(conn != null) {
			try {
				conn.close();
			} catch(Exception e) {
				System.out.println(e.toString());
			}
		}
	}

	public static Connection getConnection(String url, String id, String passwd) throws Exception {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, id, passwd);
			conn.setAutoCommit(false);
		}  catch (Exception e) {
			System.out.println("==============================================================");
			System.out.println("ERR getConnection: [URL : "+url+"] [ID : "+id+"] [PW : "+passwd+"]"+e.getMessage());
			System.out.println("==============================================================");
			throw e;
		}
		return conn;
	}

} // end of class
