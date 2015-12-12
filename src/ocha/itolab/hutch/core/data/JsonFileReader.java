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
			JSONArray array2 = (JSONArray)obj1.get("linestrings");

			// for each linestring
			for(int j = 0; j < array2.length(); j++) {
				JSONObject obj2 = (JSONObject)array2.get(j);
				JSONArray array3 = (JSONArray)obj2.get("linestring");
				OneLineString ols = ds.addOneLineString();
				
				// for each position
				for(int k = 0; k < array3.length(); k++) {
					JSONObject obj3 = (JSONObject)array3.get(k);
					Double x = (Double)obj3.get("x");
					Double y = (Double)obj3.get("y");
					ols.addOneOriginalPosition(x.doubleValue(), y.doubleValue(), (long)0);
				}
			}
					
	    } catch(JSONException e) {
	    	e.printStackTrace();
	    }
		
		ds.postprocess();
		return ds;
	}
	
}
