import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class LichessBot
{

    public static void main(String[] args) {
        // Insert your bot's token here
        String token = System.getenv("token");

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!version")) {
                event.getChannel().sendMessage("1.2");
            }
        });

        // Add a listener which create lichess game and posts url
        api.addListener( new LichessListener() );

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}