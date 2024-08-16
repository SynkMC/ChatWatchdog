package cc.synkdev.chatWatchdog.bungee.managers;

import cc.synkdev.chatWatchdog.bungee.ChatWatchdogBungee;
import cc.synkdev.chatWatchdogAPI.bungee.events.MessageDeleteEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;

public class EventHandler implements Listener {
    ChatWatchdogBungee core = ChatWatchdogBungee.getInstance();
    Lang lang = new Lang();
    WordMapManager wmm = new WordMapManager(core);

    @net.md_5.bungee.event.EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer p = (ProxiedPlayer) event.getSender();
        if (p.hasPermission("chatwatchdog.filter.bypass")) {
            return;
        }
        if (wmm.containsBlockedWords(event)) {
            MessageDeleteEvent mde = new MessageDeleteEvent(event.getMessage(), wmm.getBlockedSequences(event.getMessage()), (ProxiedPlayer) event.getSender());
            core.getProxy().getPluginManager().callEvent(mde);
            event.setCancelled(true);
        }

        /*

        Chat signing broke all that :sad:

        List<String> list = wmm.getBlockedSequences(event.getMessage());
        if (list.isEmpty()) {
            return;
        }

        String message = event.getMessage();
        for (String s : list) {
            String old = s;
            for (int i = 0; i < s.length(); i++) {
                s = s.replace(s.charAt(i), '#');
            }
            message = message.replace(old, s);
        }

        p.sendMessage(core.getPrefix()+ ChatColor.RED+lang.translate("wordsBlocked", list.size()+""));
        UtilsBungee.log("Supposed message: "+message);
        event.setCancelled(true);
        p.chat(message);

         */
    }
}
