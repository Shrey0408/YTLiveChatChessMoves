package ytlivechat;

import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

//To test the YT API
public class Main {

	private static final long NANOSEC_PER_SEC = 1000l * 1000 * 1000;
	
	//Enter Search query here or YT link
	private static String searchQuery = "";
//	private static final AmazonApiGatewayManagementApi agw = AmazonApiGatewayManagementApiClientBuilder.standard()
//			.withEndpointConfiguration(
//					new EndpointConfiguration(System.getenv("API_GW_ENDPOINT"), System.getenv("API_GW_REGION")))
//			.build();

	public static void main(String[] args) {

		// String connectionID = event.getRequestContext().getConnectionId();
		// APIGatewayV2WebSocketResponse res = new APIGatewayV2WebSocketResponse();
		// res.setIsBase64Encoded(false);
		try {
			// sendData("Hello Master YT", connectionID);
			
			String videoId = YtApiRequest.getVideoIdBySearchQuery(searchQuery);
			String liveChatId = YtApiRequest.getLiveChatIdByVideoId(videoId);
			String pageToken = "";
			long startTime = System.nanoTime();

			// Fetch Live Chat messages only for 1 minute
			while ((System.nanoTime() - startTime) < 2 * 60 * NANOSEC_PER_SEC) {
				JSONObject liveChatMessages = YtApiRequest.getLiveChatMessages(liveChatId, pageToken, "noFilter", new JSONArray());
				pageToken = liveChatMessages.get("pageToken").toString();
				System.out.println(liveChatMessages.get("messages").toString());
				TimeUnit.SECONDS.sleep(10);
			}
			// res.setStatusCode(200);
			/// res.setBody("OK");

		} catch (Exception e) {
			// res.setStatusCode(400);
			// res.setBody("Bad Request");
			System.out.println(e.toString());
		}
		// return res;
	}

//	static void sendData(String data, String connectionID) {
//		agw.postToConnection(new PostToConnectionRequest().withConnectionId(connectionID)
//				.withData(ByteBuffer.wrap(data.getBytes())));
//	}
}
