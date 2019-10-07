package edu.jsu.mcis.cs425.Lab4;

import com.mysql.cj.protocol.Resultset;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.util.Date;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Rates {
    
    public static final String RATE_FILENAME = "rates.csv";
    
    public static List<String[]> getRates(String path) {
        
        StringBuilder s = new StringBuilder();
        List<String[]> data = null;
        String line;
        
        try {
            
            /* Open Rates File; Attach BufferedReader */

            BufferedReader reader = new BufferedReader(new FileReader(path));
            
            /* Get File Data */
            
            while((line = reader.readLine()) != null) {
                s.append(line).append('\n');
            }
            
            reader.close();
            
            /* Attach CSVReader; Parse File Data to List */
            
            CSVReader csvreader = new CSVReader(new StringReader(s.toString()));
            data = csvreader.readAll();
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return List */
        
        return data;
        
    }
    
    public static String getRatesAsTable(List<String[]> csv) {
        
        StringBuilder s = new StringBuilder();
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create HTML Table */
            
            s.append("<table>");
            
            while (iterator.hasNext()) {
                
                /* Create Row */
            
                row = iterator.next();
                s.append("<tr>");
                
                for (int i = 0; i < row.length; ++i) {
                    s.append("<td>").append(row[i]).append("</td>");
                }
                
                /* Close Row */
                
                s.append("</tr>");
            
            }
            
            /* Close Table */
            
            s.append("</table>");
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return Table */
        
        return (s.toString());
        
    }
    
    public static String getRatesAsJson(List<String[]> csv) {
          
        String results = "";
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create JSON Containers */
            
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();            
            
            /* 
             * Add rate data to "rates" container and add "date" and "base"
             * values to "json" container.  See the "getRatesAsTable()" method
             * for an example of how to get the CSV data from the list, and
             * don't forget to skip the header row!
             *
             * *** INSERT YOUR CODE HERE ***
             */
            
            //Row counter to skip first row
            int counter = 0;
            
            //Parse CSV Data
            while(iterator.hasNext()){
                
                //Skip the first row
                    if(counter == 0){
                        
                        iterator.next();
                        
                        counter += 1;
                    }
                    else{
                        //Isolate the current row
                        row = iterator.next();

                        //Skip the header row
                        if(!"Code".equals(row[1])){

                            rates.put(row[1], row[2]);

                        }
                    }
            }
            
            //Make Date
            Date date = new Date();
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String cleanDate = formatter.format(date);
            
            //Put JSON info in
            json.put("rates", rates);
            json.put("date",cleanDate);
            
            /* Parse top-level container to a JSON string */
            
            results = JSONValue.toJSONString(json);
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return JSON string */
        
        return (results.trim());
        
    }
    
    public static String getRatesAsJson(String code) throws NamingException, SQLException{
        
        //Initialize Variables for Connection
        Context envContext = null, initContext = null;
        DataSource ds = null;
        Connection conn = null;
        
        try{
            //Aquire Connection
            envContext = new InitialContext();
            initContext  = (Context)envContext.lookup("java:/comp/env");
            ds = (DataSource)initContext.lookup("jdbc/db_pool");
            conn = ds.getConnection();
        }
        
        catch(SQLException e){}
        
        
        
        //Prepare Query
        String query;
 
        //Check if a code is provided
        if(code == null){
            query = "SELECT code,rate,date FROM rates";
        }
        else{
            query = "SELECT code,rate,date FROM rates WHERE code =\'" + code+"\'";
        }
        
        PreparedStatement pstmt = conn.prepareStatement(query);
        
        //Execute Query
        boolean hasresults = pstmt.execute();
        
        //Collect Data
        ResultSet resultset = pstmt.getResultSet();
        
        
        //Initialize VarIables for results
        String codes = null;
        String rate = null;
        String date = null;
        JSONObject json = new JSONObject();
        JSONObject rates = new JSONObject();
                
                ;
        //if there is data in the resultset collect it
        if(hasresults){
            while(resultset.next()){
                codes = resultset.getString("code");
                rate = resultset.getString("rate");
                date = resultset.getString("date");
                rates.put(codes, rate);
            }
        }

        //Package Data
        
        json.put("rates", rates);
        json.put("date",date);
        json.put("base","USD");
        
        String results = JSONValue.toJSONString(json);
        
        //Clean up
        pstmt.close();
        conn.close();
        
        
        return results.trim();
    }

}