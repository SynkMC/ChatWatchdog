package cc.synkdev.chatWatchdog.bukkit.managers;

import cc.synkdev.chatWatchdog.bukkit.ChatWatchdog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class EventHandler implements Listener {
    ChatWatchdog core = ChatWatchdog.getInstance();
    Lang lang = new Lang();
    WordMapManager wmm = new WordMapManager(core);
    @org.bukkit.event.EventHandler (priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("chatwatchdog.filter.bypass")) {
            return;
        }
        if (core.getDelete()) {
            if (wmm.containsBlockedWords(event)) {
                event.setCancelled(true);
                return;
            }
        }

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

        p.sendMessage(core.getPrefix() + ChatColor.RED + lang.translate("wordsBlocked", list.size() + ""));
        event.setMessage(message);
    }
}
