package ytlivechat;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.apigatewaymanagementapi.AmazonApiGatewayManagementApi;
import com.amazonaws.services.apigatewaymanagementapi.AmazonApiGatewayManagementApiClientBuilder;
import com.amazonaws.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;

public class Handler implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse> {

	private static final long NANOSEC_PER_SEC = 1000l * 1000 * 1000;

	private static final AmazonApiGatewayManagementApi agw = AmazonApiGatewayManagementApiClientBuilder.standard()
			.withEndpointConfiguration(
					new EndpointConfiguration(System.getenv("API_GW_ENDPOINT"), System.getenv("API_GW_REGION")))
			.build();

	@Override
	public APIGatewayV2WebSocketResponse handleRequest(APIGatewayV2WebSocketEvent event, Context context) {
		LambdaLogger logger = context.getLogger();
		String connectionID = event.getRequestContext().getConnectionId();
		APIGatewayV2WebSocketResponse res = new APIGatewayV2WebSocketResponse();
		res.setIsBase64Encoded(false);
		logger.log("Event :" + event.toString());
		try {

//			Body-->
//			body='{
//			  "filterType": "<<noFilter, chessMoves, givenKeywords>>",
//			  "searchQuery": "<<Search Query for Youtube Video>>",
//			  "keywords": [<<Array of search keywords if filterType is givenKeywords>>]
//			}'
			JSONObject body = new JSONObject(event.getBody());
			String searchQuery = body.get("searchQuery").toString();
			String filterType = body.get("filterType").toString();
			JSONArray keywords = body.getJSONArray("keywords");
			String videoId = YtApiRequest.getVideoIdBySearchQuery(searchQuery);
			String liveChatId = YtApiRequest.getLiveChatIdByVideoId(videoId);
			String pageToken = "";
			long startTime = System.nanoTime();

			// Fetch Live Chat messages only for 1.5 minutes at 6 sec interval
			while ((System.nanoTime() - startTime) < 1.5 * 60 * NANOSEC_PER_SEC) {
				JSONObject liveChatMessages = YtApiRequest.getLiveChatMessages(liveChatId, pageToken, filterType, keywords);
				pageToken = liveChatMessages.get("pageToken").toString();
				JSONArray messages = liveChatMessages.getJSONArray("messages");
				JSONObject liveChatMessagesToClient =  new JSONObject();
				liveChatMessagesToClient.put("type", "liveChatMessages");
				liveChatMessagesToClient.put("messages", messages);
				if (messages.length() != 0) {
					sendData(liveChatMessagesToClient.toString(), connectionID);
				}
				TimeUnit.SECONDS.sleep(6);
			}
			res.setStatusCode(200);
			res.setBody("OK");

		} catch (Exception e) {
			res.setStatusCode(400);
			res.setBody("Bad Request");
			logger.log(e.toString());
		}
		return res;
	}

	static void sendData(String data, String connectionID) {
		agw.postToConnection(new PostToConnectionRequest().withConnectionId(connectionID)
				.withData(ByteBuffer.wrap(data.getBytes())));
	}
}
