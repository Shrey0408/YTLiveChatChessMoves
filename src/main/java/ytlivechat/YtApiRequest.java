package ytlivechat;

import org.json.JSONArray;
import org.json.JSONObject;

public class YtApiRequest {

	// Accepts search keyword and returns the Video id out of 5 results based on
	// channel which is currently Live
	static String getVideoIdBySearchQuery(String searchquery) throws Exception {

		String searchParameter = searchquery.strip().replaceAll(" ", "%20");
		JSONObject searchResults = FetchData.getHTML(
				"https://youtube.googleapis.com/youtube/v3/search?part=snippet&q=" + searchParameter + "&type=video&maxResults=10");
		JSONArray itemsArray = searchResults.getJSONArray("items");
		String videoID = "";
		for (int itemIndex = 0; itemIndex < 5; itemIndex++) {
			if (itemsArray.getJSONObject(itemIndex).getJSONObject("snippet").get("liveBroadcastContent").toString()
					.equalsIgnoreCase("live")) {
				videoID = itemsArray.getJSONObject(itemIndex).getJSONObject("id").get("videoId").toString();
				break;
			}
		}
		return videoID;
	}

	// To get live chat id based on video id
	static String getLiveChatIdByVideoId(String videoId) throws Exception {

		JSONObject returnedObject = FetchData
				.getHTML("https://youtube.googleapis.com/youtube/v3/videos?part=liveStreamingDetails&id=" + videoId);
		JSONArray itemsArray = returnedObject.getJSONArray("items");
		return itemsArray.getJSONObject(0).getJSONObject("liveStreamingDetails").get("activeLiveChatId").toString();
	}

	// Returns Array of Live chat messages(author and Message) and next Page Token
	static JSONObject getLiveChatMessages(String liveChatId, String pageToken, String filterType,
			JSONArray keywords)
			throws Exception {
		JSONArray messageAndAuthorArray = new JSONArray();
		JSONObject messageandAuthor;
		String message, author;
		JSONObject returnedObject = FetchData
				.getHTML("https://youtube.googleapis.com/youtube/v3/liveChat/messages?liveChatId=" + liveChatId
						+ "&part=snippet&part=authorDetails&pageToken=" + pageToken);
		pageToken = returnedObject.get("nextPageToken").toString();
		JSONArray itemsArray = returnedObject.getJSONArray("items");

		for(int i = 0; i < itemsArray.length(); i++){
			message = itemsArray.getJSONObject(i).getJSONObject("snippet").get("displayMessage").toString();
			author = itemsArray.getJSONObject(i).getJSONObject("authorDetails").get("displayName").toString();
			messageandAuthor = new JSONObject();
			//If detectChessMoves flag set to true then put messages in array only if it is in correct  chess Notation 
			if(filterType.equals("chessMoves")) {
				message = MessageFilter.detectMoves(message);
			}else if(filterType.equals("givenKeywords")) {
				message = MessageFilter.findInGivenKeywords(message, keywords);
			}
			if(message == "")
				continue;
			messageandAuthor.put("message", message);
			messageandAuthor.put("author", author);
			messageAndAuthorArray.put(messageandAuthor);
		}
		return new JSONObject().put("pageToken", pageToken).put("messages", messageAndAuthorArray);
	}
}
