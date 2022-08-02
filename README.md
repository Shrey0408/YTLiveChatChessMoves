# YTLiveChatChessMoves

-> Developed to get response from audience where they can answer next best move in a chess game asked by chess live YT streamers.

-> Streamers can see the poll resilt of how many users has suggested which chess move and also find out who were the first people to answer correctly.

-> Many times live streamers want to interact with their audiences. To get overall response they can use this poll to check how users are responding.

-> Real Time Youtube live chat poll for chess Moves or by given filter

-> Completely serverless architectecture using AWS Lambda and AWS API Gateway.

-> Youtube Data API used to extract live chat messages and Websocket for real time polling.

# Instructions to get poll from Live chat messages of Youtube live Streams : 

1. Enter the Youtube live stream video title in "Search by Video title" field.
2. Select Filter using the dropdown-
   No Filter :  All the live chat messages written by all the users are included in poll result.
   Chess Moves : Filter out only the live messages including valid chess moves written using valid chess notations.(Ex- kb4, Bc2, a2 etc)
   Enter Keywords : Users can enter upto 10 keywords. Chat messages are filtered using the given keywords. Only the entered keywords will be displayed in poll results.  
   
   <img width="1278" alt="Result4" src="https://user-images.githubusercontent.com/53952338/182420462-2499a099-0608-43a4-888b-c255627cebd4.png">
   
   
<img width="1265" alt="ResultYTChessMove" src="https://user-images.githubusercontent.com/53952338/182420540-ba215e58-7c64-46b4-a3a5-144fe03c1d3e.png">

