package Main;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
            System.out.println(randomIDs);
            //load items into embed
            for (int x = 0; x != randomIDs.size(); x++) {
                //grabbing the items
                String itemAdded = Objects.requireNonNull(event.getValue(randomIDs.get(x))).getAsString();
                //adding the fields
                eb.addField(":white_square_button: " + emojis.get(x), itemAdded, false);
            }

            //setting the channel to print out the embed
            outputChannel = event.getTextChannel();

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

        //grab the message associated with the reaction.
        Message message = event.retrieveMessage().complete();

        //check to make sure it wasn't the bots own reaction
        if (event.getReaction().isSelf()) return;

        if (message.getAuthor().getIdLong() == 955490875008512020L) {
            //deletes reaction that are on bots embed
            event.getReaction().removeReaction(event.getUser()).queue();

            //get the reaction as a string
            String reactCode = event.getReactionEmote().getAsReactionCode();

            //make arrays for the titles.
            String[] checked = {
                    ":white_check_mark: :one:",
                    ":white_check_mark: :two:",
                    ":white_check_mark: :three:",
                    ":white_check_mark: :four:",
                    ":white_check_mark: :five:"};

            String[] unchecked = {
                    ":white_square_button: :one:",
                    ":white_square_button: :two:",
                    ":white_square_button: :three:",
                    ":white_square_button: :four:",
                    ":white_square_button: :five:"};

            int field_index = -1;

            //store current field to field_index
            switch (reactCode) {
                case "\u0031\u20e3":
                    field_index = 0;
                    break;
                case "\u0032\u20e3":
                    field_index = 1;
                    break;
                case "\u0033\u20e3":
                    field_index = 2;
                    break;
                case "\u0034\u20e3":
                    field_index = 3;
                    break;
                case "\u0035\u20e3":
                    field_index = 4;
                    break;
                default:
                    return;
            }


            // copy the embed associated with the reaction and get all fields
            EmbedBuilder newBuilder = new EmbedBuilder(message.getEmbeds().get(0));
            List<MessageEmbed.Field> fields = newBuilder.getFields();

            //change the check to uncheck or visa versa
            if (fields.get(field_index).getName().equals(unchecked[field_index])) {
                fields.set(field_index, new MessageEmbed.Field(checked[field_index], fields.get(field_index).getValue(), false));
            } else {
                fields.set(field_index, new MessageEmbed.Field(unchecked[field_index], fields.get(field_index).getValue(), false));
            }

            //send out the edited embed
            message.editMessageEmbeds(newBuilder.build()).queue();

            //check how many checks there are
            int count = 0;
            for (int x = 0; x != fields.size(); x++){
                if(fields.get(x).getName().equals(checked[x]))
                    count++;
            }
            if (count == fields.size()){
                newBuilder.setFooter("COMPLETE!");
                message.editMessageEmbeds(newBuilder.build()).queue();
                message.clearReactions().queue();
            }


        }
    }
}
