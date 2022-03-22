package Main;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Commands extends ListenerAdapter {
    static TextChannel outputChannel;
    static int howMany;
    static ArrayList<ItemComponent> inputList = new ArrayList<>();
    static ArrayList<String> randomIDs = new ArrayList<>();
    static final List<String> emojis = Arrays.asList(":one:", ":two:", ":three:", ":four:", ":five:");

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("addtodo")) {
            //get the number of items to add to the modal
            howMany = event.getOption("howmany").getAsInt();

            if (howMany > 5) {
                event.reply("Can only be max of 5!").queue();
                return;
            }

            //loop the addItem
            for (int x = 0; x != howMany; x++) {

                String currentID = UUID.randomUUID().toString();
                inputList.add(TextInput.create(currentID, "Item", TextInputStyle.SHORT)
                        .setPlaceholder("Add a TO-DO item!")
                        .setRequired(true)
                        .setMinLength(1)
                        .setMaxLength(100)
                        .build());
                randomIDs.add(currentID);
            }

            //build the modal
            Modal.Builder builder = Modal.create("addItems", "add items to teh list");
            for (int c = 0; c != inputList.size(); c++) {
                builder.addActionRow(inputList.get(c));
            }

            Modal modal = builder.build();

            inputList.clear();

            event.replyModal(modal).queue();
        }
    }


    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("addItems")) {
            //start building the embed
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.GREEN);
            eb.setAuthor(event.getUser().getName());
            //load items into embed
            for (int x = 0; x != randomIDs.size(); x++) {
                //grabbing the items
                String itemAdded = event.getValue(randomIDs.get(x)).getAsString();
                //adding the fields
                eb.addField("Item", itemAdded, false);
            }

            //setting the channel to print out the embed
            outputChannel = Start.api.getTextChannelById("955474204281675786");

            // output the embed
            List<String> bullshitList = emojis.subList(0, randomIDs.size());

            // abosolute pile of fucking shit lambda bullshit. (adds however many emotes for however many entries)
            outputChannel.sendMessageEmbeds(eb.build())
                    .queue(message ->
                            bullshitList.forEach((i) ->
                                    message.addReaction(EmojiParser.parseToUnicode(i)).queue()));

            //reply to the event
            event.reply("added!").setEphemeral(true).queue();

            //clear the random UUID list
            randomIDs.clear();
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {

        Message message = event.retrieveMessage().complete();
        //check to make sure it wasnt the bots own reaction
        if (event.getReaction().isSelf()) {
            return;
        }

        // do this if its a check mark = 1
        if (event.getReactionEmote().getAsReactionCode().equals(EmojiParser.parseToUnicode(":one:")) && message.getAuthor().isBot()) {

            // make the reaction a button
            event.getReaction().removeReaction(event.getUser()).queue();

            // copy the embed associated with the reaction
            EmbedBuilder newBuilder = new EmbedBuilder(message.getEmbeds().get(0));

            /*
            newBuilder.getFields()
            EmbedBuilder editEmbed = new EmbedBuilder(event.retrieveMessage());
            event.getChannel().editMessageEmbedsById(event.getMessageIdLong()).setEmbeds(doneEmbed.build()).queue();
            */
        }
    }
}
