import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class MyFirstBot {

    public static void main(String[] args) {
        // Insert your bot's token here
        String token = "Nzg3Njk1NTQ4MjY5NTkyNjA2.X9YspQ.D-GPzifXEvv2T13-EvDyfhN1as0";

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });

        // Add a listener which create lichess game and posts url
        api.addListener( new LichessListener() );

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}