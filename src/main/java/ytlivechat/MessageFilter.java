package ytlivechat;

import java.util.regex.Pattern;

import org.json.JSONArray;



public class MessageFilter {
	
	private final static String regex = "^([Oo0](-[Oo0]){1,2}|[KkQqRrBbNn]?[a-h]?[1-8]?x?[a-h][1-8](\\=[QRBN])?[+#]?){1}$";
	private final static Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
	
	//If chat message is a chess move return adjusted chess  move else return empty string. 
	static String detectMoves(String data) {
		
		data = data.toLowerCase();
	    data = data.strip();
	    data = data.replace("x","");
	    data = data.replace("0","o");
	    data = data.replace("×","");	    
	    data = data.replace("#","");
	    data = data.replace("+","");
	    data = data.replace("?","");
	    data = data.replace("!","");
	    data = data.replace(".","");
	    if(pattern.matcher(data).matches()) {
	    	return data;
	    }
	    return "";
	}

// Returns the keyword if the chat message contains that keyword.
static String findInGivenKeywords(String data, JSONArray keywords) {
	int foundIndex = -1;
	for(int i = 0; i< keywords.length(); i++) {
		if(data.contains(keywords.get(i).toString()))
			foundIndex = i;
	}
	if(foundIndex != -1)
		return keywords.get(foundIndex).toString();
	return "";	
	}
}
