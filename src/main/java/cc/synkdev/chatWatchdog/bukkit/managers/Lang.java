package cc.synkdev.chatWatchdog.bukkit.managers;

import cc.synkdev.chatWatchdog.bukkit.ChatWatchdog;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Lang {
    public Lang() {
        init();
    }

    public FileConfiguration config;
    ChatWatchdog core = ChatWatchdog.getInstance();
    public File file = new File(core.getDataFolder(), "lang.yml");

    public void init() {
        if (!core.getDataFolder().exists()) {
            core.getDataFolder().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);

        config.addDefault("fetched", "Fetch finished! Fetched %s1% words in %s2% ms!");
        config.addDefault("noPerm", "You don't have permission to use this command!");
        config.addDefault("reloaded", "The plugin has been reloaded!");
        config.addDefault("filterList", "List of manually blocked words:");
        config.addDefault("filterListPage", "Page %s1%/%s2%");
        config.addDefault("wrongPage", "Wrong page number");
        config.addDefault("alreadyBlocked", "This word sequence is already blocked!");
        config.addDefault("notBlocked", "This word sequence is not blocked!");
        config.addDefault("wordsBlocked", "%s1% word sequence(s) were blocked in your last message!");
        config.addDefault("messageBlocked", "Your last message was blocked by the chat filter!");
        config.addDefault("pageDoesntExist", "This page doesn't exist!");
        config.addDefault("wasAdded", "%s1% was added to the list!");
        config.addDefault("wasRemoved", "%s1% was removed from the list!");
        config.addDefault("help-1", "List of commands:");
        config.addDefault("help-2", "- /cw reload - reloads the configuration and word lists");
        config.addDefault("help-3", "- /cw filter - shows the list of manually blocked words");
        config.addDefault("help-4", "- /cw filter add <word sequence> - adds a sequence to the list of manually blocked words");
        config.addDefault("help-5", "- /cw filter remove <word sequence> - removes a sequence from the list of manually blocked words");

        config.options().copyDefaults(true);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String translate(String s) {
        return removeEnds(config.getString(s));
    }

    public String translate(String s, String s1) {
        String ret = translate(s);
        ret = ret.replace("%s1%", s1);
        return ret;
    }

    public String translate(String s, String s1, String s2) {
        String ret = translate(s, s1);
        ret = ret.replace("%s2%", s2);
        return ret;
    }

    public String translate(String s, String s1, String s2, String s3) {
        String ret = translate(s, s1, s2);
        ret = ret.replace("%s3%", s3);
        return ret;
    }

    public String removeEnds(String s) {
        return s.split("\"")[0];
    }
    public String help() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GOLD+translate("help-1")).append("\n").append(ChatColor.GOLD+translate("help-2")).append("\n").append(ChatColor.GOLD+translate("help-3")).append("\n").append(ChatColor.GOLD+translate("help-4")).append("\n").append(ChatColor.GOLD+translate("help-5"));
        return sb.toString();
    }

}
