package org.java.lessons.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		System.out.println("vuoi filtrare la ricerca? (y/n)");
		String wannaFilter = in.nextLine();
		
		Set<Integer> idSet = new HashSet<>();
		
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
						 + "FROM countries "
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
						
						idSet.add(countryId);
						
						System.out.println(country + " | " + countryId + " | "  + region + " | "  + continent +"\n------------------------");
					}
					
				}
				
			}
			
			
		}
		
		catch(Exception e) {
			System.out.println("error:" + e.getMessage());
		}
		
		try( Connection con = DriverManager.getConnection(url, user, password)){
		
			System.out.println("for extra info give me an id or else write 0 to cancel");
			String extra = in.nextLine();
			int intExtra = Integer.valueOf(extra);
			
			if(intExtra == 0) {
				System.out.println("fine");
			}
			
			else if(intExtra > 0 && idSet.contains(intExtra)) {
				
				String infoQuery = "SELECT countries.name , languages.language , country_stats.year, country_stats.population, country_stats.gdp "
								 + "FROM countries "
								 + "JOIN country_stats "
								 +	"ON countries.country_id = country_stats.country_id "
								 +	"JOIN country_languages "
								 +		"ON countries.country_id = country_languages.country_id "
								 +	"JOIN languages "
								 +		"ON country_languages.language_id = languages.language_id "
								 +	"WHERE countries.country_id = ? "
								 +	"GROUP BY country_stats.year, languages.language "
								 +	"HAVING country_stats.year = ( "
									    
								 +	    	"SELECT  MAX(country_stats.year) "
								 +	    	"FROM countries "
								 +			"JOIN country_stats "
								 +	        "    ON countries.country_id = country_stats.country_id "
								 +	        "JOIN country_languages "
								 +	        "    ON countries.country_id = country_languages.country_id "
								 +	        "JOIN languages "
								 +	       "    ON country_languages.language_id = languages.language_id " 
								 +	        "WHERE countries.country_id = ? "
								 +	        "GROUP BY country_stats.year "
								 +	    	"ORDER BY country_stats.year DESC "
								 +	    	"LIMIT 1 "
									    	
								 +	") "
								 + "ORDER BY countries.name , country_stats.year DESC ;" ;
				
//				System.out.println(infoQuery);
				
				try( PreparedStatement ps2 = con.prepareStatement(infoQuery)){
					
					ps2.setInt(1, intExtra);
					ps2.setInt(2, intExtra);
					try(ResultSet rs = ps2.executeQuery()) {
						
						String country=null;
						List <String> languages = new ArrayList<>();
						Integer year=null;
						Long population = 0l;
						Long gdp = 0l;
						while(rs.next()) {
							
							country = rs.getString(1);
							languages.add(rs.getString(2));
							year =rs.getInt(3);
							population = rs.getLong(4);
							gdp = rs.getLong(5);
							
						}
						
						System.out.println("Details for : " + country
										  +"\nlanguages: " + languages
										  + "\nMost recent Stats : "
										  + "\nStats Year: "+ year
										  + "\npopulation: " + population
										  + "\ngdp: " + gdp);
						
					}
					
				}
			
			}
			
			else {
				System.out.println("invalid parameter");
			}
			
		}
		catch (Exception e2) {
			System.out.println("error 2 :" + e2.getMessage());
		}
		
	}
	

}



