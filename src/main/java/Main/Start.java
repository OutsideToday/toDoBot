package Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.security.auth.login.LoginException;
import java.io.FileReader;

public class Start {
    static JDA api;

    public static void main(String[] args) throws LoginException, InterruptedException {
        // grab the credentials
        JSONParser parser = new JSONParser();
        String nekot = null;
        try{
            Object obj = parser.parse(new FileReader("./src/main/java/Main/credentials.json"));
            JSONObject jsonObject = (JSONObject) obj;
            nekot = (String) jsonObject.get("tokenAF");

        }catch (Exception e){
            e.printStackTrace();
        }
        // notAnekot is the token needed. its missing the first 4 characters because discord scrapes the web for tokens
        final String notAnekoT = nekot;

        api = JDABuilder.createDefault("OTU1" + notAnekoT).build();

        //wait for the bot to finish building
        api.awaitReady();

        // set the activity
        api.getPresence().setActivity(Activity.playing("TO-DO"));

        // Adding Listeners!
        api.addEventListener(new Commands());
        api.upsertCommand("addtodo", "Add an item to the discord TO-DO list").queue();

    }
}
