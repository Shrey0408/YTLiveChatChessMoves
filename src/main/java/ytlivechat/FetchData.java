package ytlivechat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class FetchData {

	// To get JSONObject from given URL using GET method
	static JSONObject getHTML(String urlToGet) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToGet + "&key=" + System.getenv("YT_API_KEY"));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		}
		JSONObject jsonObj = new JSONObject(result.toString());
		return jsonObj;
	}

}
