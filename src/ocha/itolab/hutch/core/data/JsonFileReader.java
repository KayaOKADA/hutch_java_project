package ocha.itolab.hutch.core.data;

import java.io.*;
import org.json.*;

public class JsonFileReader {
    static StringBuilder builder = new StringBuilder();
    
    static String loadFile(String filepath) {
        // try to find file
        FileReader file_reader = null;
        BufferedReader buf_reader = null;
        StringWriter str_writer = null;
        
        try {
            file_reader = new FileReader(filepath);
            buf_reader = new BufferedReader(file_reader);
            str_writer = new StringWriter();
        } catch (FileNotFoundException e) {
            System.err.println("Failed to find file");
            return null;
        }
        String json_txt;
        String line_text;
        // try to read every lines in the file
        try {
            while ((line_text = buf_reader.readLine()) != null) {
                str_writer.append(line_text);
            }
            json_txt = str_writer.toString();
        } catch (IOException e) {
            System.err.println("Failed to load json file");
            return null;
        }
        return json_txt;
    }
    
    
    
    public static DataSet generateDataSet(String filepath) {
        String json_txt = loadFile(filepath);
        DataSet ds = new DataSet();
        
        if(json_txt == null) {
            System.err.println("Exit with Error");
            System.exit(1);
        }
        
        // generate data set
        try {
            
            JSONObject obj1 = new JSONObject(json_txt);
            JSONArray array2 = (JSONArray)obj1.get("data");
            OneLineString ols = null;
            
            // for each linestring
            for(int j = 0; j < array2.length(); j++) {
                JSONObject obj2 = (JSONObject)array2.get(j);
                long time = obj2.getLong("timestamp");
                JSONArray array3 = (JSONArray)obj2.get("people");
                
                // for each position
                for(int k = 0; k < array3.length(); k++) {
                	ols = ds.addOneLineString();
                    JSONObject obj3 = (JSONObject)array3.get(k);
                    String gender = obj3.getString("gender");
                    String age = obj3.getString("age");
                    JSONObject obj4 = (JSONObject)obj3.get("position");
                    Double x = obj4.getDouble("x");
                    Double y = obj4.getDouble("y");
                    ols.addOneOriginalPosition(x.doubleValue(), -1.0 * y.doubleValue(), time, gender, age);
                }
            }
            
        } catch(JSONException e) {
            e.printStackTrace();
        }
        
        ds.postprocess();
        return ds;
    }
    
}
