package Main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.component.ModalImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class Commands extends ListenerAdapter {
    static TextChannel outputChannel;
    static int howMany;
    static ArrayList<TextInput> inputList = new ArrayList<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("addtodo")) {
            //get the number of items to add to the modal
            howMany = event.getOption("howmany").getAsInt();

            //loop the addItem
            for (int x = 0; x != howMany; x++) {

                inputList.add(TextInput.create("test", "Item", TextInputStyle.SHORT)
                        .setPlaceholder("Add a TO-DO item!")
                        .setRequired(true)
                        .setMinLength(1)
                        .setMaxLength(100)
                        .build());
            }

            //build the modal
            Modal modal = Modal.create("addItems", "Add items to the list")
                    .addActionRows(ActionRow.of((ItemComponent) inputList))
                    .build();


            event.replyModal(modal).queue();
        }
    }


    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("addItems")) {
            //get item
            String itemAdded = event.getValue("item").getAsString();

            //setting the channel to print out the embed
            outputChannel = Start.api.getTextChannelById("955474204281675786");

            //build the embed
            EmbedBuilder eb = new EmbedBuilder();
            eb.addField("Item", itemAdded, false);

            // output the embed
            outputChannel.sendMessageEmbeds(eb.build()).queue();

            //reply to the event
            event.reply("added!").queue();

        }
    }
}
