package cc.synkdev.chatWatchdog.bukkit.managers;

import cc.synkdev.chatWatchdog.bukkit.ChatWatchdog;
import cc.synkdev.chatWatchdogAPI.bukkit.events.ChatCensorEvent;
import cc.synkdev.chatWatchdogAPI.bukkit.events.MesageDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class EventHandler implements Listener {
    ChatWatchdog core = ChatWatchdog.getInstance();
    Lang lang = new Lang();
    WordMapManager wmm = new WordMapManager(core);
    @org.bukkit.event.EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("chatwatchdog.filter.bypass")) {
            return;
        }
        if (core.getDelete()) {
            if (wmm.containsBlockedWords(event)) {
                MesageDeleteEvent mde = new MesageDeleteEvent(event.getPlayer(), event.getMessage(), wmm.getBlockedSequences(event.getMessage()));
                Bukkit.getPluginManager().callEvent(mde);
                if (!mde.isCancelled()) event.setCancelled(true);
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

        ChatCensorEvent cce = new ChatCensorEvent(event.getPlayer(), event.getMessage(), message, list);
        Bukkit.getPluginManager().callEvent(cce);

        if (!cce.isCancelled()) {
            p.sendMessage(core.getPrefix() + ChatColor.RED + lang.translate("wordsBlocked", list.size() + ""));
            event.setMessage(message);
        }
    }
}
