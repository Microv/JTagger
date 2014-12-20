package wrappers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class AcoustIDWrapper {

	private static final String FINGERPRINT_LABEL = "FINGERPRINT=";
	private static final String DURATION_LABER = "DURATION=";
	private Process p;
	private BufferedReader reader;
	private String duration;
	public String getDuration() {
		return duration;
	}


	public String getFingerprint() {
		return fingerprint;
	}

	

	private String fingerprint;
	
	public AcoustIDWrapper() throws IOException, InterruptedException {
		
	}
	
	public void genAudioFingerPrintInfo(String filePath, int length) throws IOException, InterruptedException {
		
		String[] shCommand = { "/bin/sh", "-c", "JTagger/fpcalc/fpcalc "
				+ filePath};
		p = Runtime.getRuntime().exec(shCommand);
		p.waitFor();
	 
	    reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	 
	    String result = "";
	    String line = "";
	    while ((line = reader.readLine())!= null) {
	    	result += line;
	    	//System.out.println(line);
	    }
	    
	    int durationStart = result.indexOf(DURATION_LABER) + DURATION_LABER.length();
	    int fingerPrintStart = result.indexOf(FINGERPRINT_LABEL) + FINGERPRINT_LABEL.length();
	    duration = result.substring(durationStart, fingerPrintStart - FINGERPRINT_LABEL.length());
	    
	    fingerprint = result.substring(fingerPrintStart);
	    
	}
	
	public ArrayList<String> getMusicBrainzID() throws IOException, JSONException {
		
		String urlString = 
				"http://api.acoustid.org/v2/lookup?client=8XaBELgH&meta=recordingids&duration="
		+duration+"&fingerprint="+fingerprint;
		URL url = new URL(urlString);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		
		StringBuffer buffer = new StringBuffer();
	    int read;
	    char[] chars = new char[1024];
	    while ((read = reader.read(chars)) != -1)
	           buffer.append(chars, 0, read);
	    
	    JSONObject json = new JSONObject(buffer.toString());
	    JSONArray results = (JSONArray) json.get("results");
	    ArrayList<String> ids = new ArrayList<String>();
	    for(int i = 0; i < results.length(); i++) {
	    	JSONArray idArray = (JSONArray)results.getJSONObject(i).get("recordings");
	    	for(int j = 0; j < idArray.length(); j++) {
	    		JSONObject idObject = idArray.getJSONObject(j);
	    		ids.add(idObject.get("id").toString());
	    	}
	    		
	    }
	    return ids;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		
		String filePath = "/home/michele/Scrivania/chromaprint-fpcalc-1.1-linux-x86_64/file.mp3";
	
		AcoustIDWrapper aiw = new AcoustIDWrapper();
		
		aiw.genAudioFingerPrintInfo(filePath, 50);
		
		System.out.println("DURATION= "+aiw.getDuration());
		
		System.out.println("FINGERPRINT= "+aiw.getFingerprint());
		
		ArrayList<String> ids = aiw.getMusicBrainzID();
		for(String s : ids)
			System.out.println(s);
		
	}

}
