package org.java.lessons.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		System.out.println("vuoi filtrare la ricerca? (y/n)");
		String wannaFilter = in.nextLine();
		
		boolean filter = (wannaFilter.equals("y")? true:false);
		String word = null;
		
		if(filter) {
			
			System.out.println("per che parola vuoi filtrare?");
			
			word = in.nextLine();
		}
		
		String url = "jdbc:mysql://localhost:3306/db-nations";
		String user = "root";
		String password = "root";
		
		try( Connection con = DriverManager.getConnection(url, user, password)){
			
			String query = "SELECT countries.name as country, countries.country_id, regions.name as region, continents.name as continent "
						 + "FROM `countries` "
						 + "JOIN regions "
						 + 		"ON countries.region_id = regions.region_id "
						 + "JOIN continents "
						 + 		"ON continents.continent_id = regions.continent_id "
						 + (filter ? "WHERE countries.name LIKE ? " : "WHERE 1" )
						 + "ORDER BY countries.name;";
			
//			System.out.println(query);
			
			try( PreparedStatement ps = con.prepareStatement(query)){
				
				if(filter) {
					
					ps.setString(1, "%" + word + "%");
				}
				
				try(ResultSet rs = ps.executeQuery()) {
					
					while(rs.next()) {
						
						String country = rs.getString(1);
						int countryId = rs.getInt(2);
						String region = rs.getString(3);
						String continent = rs.getString(4);
						
						System.out.println(country + " | " + countryId + " | "  + region + " | "  + continent +"\n------------------------");
					}
					
				}
				
			}
		}
		
		catch(Exception e) {
			System.out.println("error:" + e.getMessage());
		}
		
		finally {
			in.close();
		}
		
	}

}



