import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LichessListener implements MessageCreateListener
{
    @Override
    public void onMessageCreate(MessageCreateEvent event)
    {
        Message message = event.getMessage();
        if ( message.getContent().startsWith("!chess") )
        {
            String challengeMessage = createChallenge( message.getContent() );
            event.getChannel().sendMessage( challengeMessage );
        }
    }

    public static String createChallenge(String input )
    {
        String cleanInput = input.replaceFirst("!chess", "").trim();
        String returnMessage = "Failed to parse input - use !chess clockLimit/clockIncrement(eg !chess 5/0)";

        if( cleanInput.equals("") )
        {
            try
            {
                returnMessage = openChallengeRequest( 60, 0);
                return returnMessage;
            } catch (Exception e)
            {
                e.printStackTrace();
                return "Lichess API connection failed - wait a few seconds and try again";
            }
        }

        try {
            String[] parts = cleanInput.split("/");
            if (parts.length != 2)
                return returnMessage;

            if( !Utils.isNumeric( parts[0] ) || !Utils.isNumeric( parts[1] ))
                return "Failed to parse input - use numbers for clockLimit and clockIncrement or leave blank for default settings(1+0)";

            double clockLimitInMinutes = Double.parseDouble( parts[0]);
            double clockIncrementInSeconds = Double.parseDouble( parts[1]);
            returnMessage = openChallengeRequest( (int) (clockLimitInMinutes * 60), (int)clockIncrementInSeconds);

            System.out.println(returnMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnMessage;
    }

    public static String openChallengeRequest(int clockLimitInSeconds, int clockIncrementInSeconds ) throws Exception
    {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("clock.limit", String.valueOf( clockLimitInSeconds)));
        params.add(new BasicNameValuePair("clock.increment", String.valueOf( clockIncrementInSeconds)));
        //params.add(new BasicNameValuePair("variant", "racingKings"));

        JSONObject obj = executeLichessApiRequest( "challenge/open", params);

        try{
            JSONObject challenge = obj.getJSONObject("challenge");
            String url = challenge.getString( "url" );
            String time = challenge.getJSONObject("timeControl").getString("show");
            return  url + " (" + time + ")";
        }
        catch ( Exception e)
        {
            return "Invalid time interval. Lichess only supports clockLimits 0/0.25/0.5/0.75/1/1.5/2-180 and increment 1-180";
        }
    }

    public static JSONObject executeLichessApiRequest( String extension, List<NameValuePair> params) throws IOException
    {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://lichess.org/api/" + extension);
        httpPost.setEntity(new UrlEncodedFormEntity(params));


        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        client.close();

        return new JSONObject( result );
    }
}

