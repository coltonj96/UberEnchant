package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.*;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import org.bukkit.conversations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * For internal use.
 */
public class ListCommand extends UberTabCommand {

    public ListCommand() {
        super("ulist");
    }

    @Override
    public boolean onCmd() {
        if (args.length == 1)
            switch (args[0].toLowerCase()) {
                case "enchants" -> action("enchants", this::converse);
                case "effects" -> action("effects", this::response, EffectUtils.listEffects());
                default -> EnchantmentUtils.help(player, "ulist");
            }
        else
            /*
            //ItemStack item = player.getInventory().getItemInMainHand();
            //if (!item.getType().equals(Material.AIR)) {
                //FileUtils.createConfig("test.yml");
                ItemStack item = FileUtils.get("test.yml", "test", ItemStack.class);
                if (item != null) {
                    UberUtils.addEnchantmentLore(item);
                    player.getInventory().setItemInMainHand(item);
                }
                //player.getInventory().addItem(item);
                //FileUtils.set("test.yml", "test", item);
            //}
             */
            response("&6%1$s", command.getUsage());
        return true;
    }

    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            add(list, "uber.list,enchants", "enchants");
            add(list, "uber.list,effects", "effects");
        }
        return list;
    }

    private void converse() {
        ConversationFactory factory = new ConversationFactory(UberEnchant.instance());
        Conversation chat = factory.withFirstPrompt(new NumericPrompt() {
            private final String HELP = ChatUtils.color(UberLocale.getCF("&8", "actions.list.page.instructions", 1, EnchantmentUtils.getPages()));
            private final String INVALID = ChatUtils.color("&8&l[&5UberEnchant&8&l] " + UberLocale.getC("&c", "actions.list.page.invalid_input"));

            @Override
            protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                context.setSessionData("invalid", false);
                int page = input.intValue();
                if (page < 1)
                    page = 1;
                context.setSessionData("page", page);
                return this;
            }

            @Override
            public String getPromptText(ConversationContext context) {
                Object invalid = context.getSessionData("invalid");
                Object page = context.getSessionData("page");
                if ((invalid != null && (boolean) invalid) || page == null || (int) page == 0)
                    return HELP;
                return EnchantmentUtils.listPage((int) page);
            }

            @Override
            protected String getFailedValidationText(ConversationContext context, String input) {
                context.setSessionData("invalid", true);
                return INVALID;
            }
        })
                .withPrefix(context -> ChatUtils.color("&8&l[&5UberEnchant&8&l] "))
                .withLocalEcho(false)
                .withTimeout(30)
                .withEscapeSequence("exit")
                .addConversationAbandonedListener(event -> {
                    if (event.getCanceller() instanceof InactivityConversationCanceller)
                        localized("&c", "actions.list.page_timed_out");
                    if (event.gracefulExit() || event.getCanceller() instanceof ExactMatchConversationCanceller)
                        localized("&a", "actions.list.exit");
                })
                .buildConversation(player);
        chat.begin();
    }
}
