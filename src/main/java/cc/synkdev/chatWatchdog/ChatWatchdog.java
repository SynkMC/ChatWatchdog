package cc.synkdev.chatWatchdog;

import cc.synkdev.bstats.bukkit.Metrics;
import cc.synkdev.chatWatchdog.commands.CWCmd;
import cc.synkdev.chatWatchdog.managers.EventHandler;
import cc.synkdev.chatWatchdog.managers.WordMapManager;
import cc.synkdev.synkLibs.Lang;
import cc.synkdev.synkLibs.SynkLibs;
import cc.synkdev.synkLibs.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ChatWatchdog extends JavaPlugin {
    public Lang synkLibsLang;
    private WordMapManager wmm;
    @Getter private static ChatWatchdog instance;
    public cc.synkdev.chatWatchdog.managers.Lang lang;
    @Getter private Boolean delete = false;
    @Getter private Boolean defaultList = true;
    public List<String> wordsMap = new ArrayList<>();
    public List<String> localWordsMap = new ArrayList<>();
    @Getter private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6ChatWatchdog&8] » &r");

    @Override
    public void onEnable() {
        instance = this;
        synkLibsLang = new Lang(this);
        lang = new cc.synkdev.chatWatchdog.managers.Lang();
        wmm = new WordMapManager(this);
        initConfig();
        wmm.load();
        new Metrics(this, 23020);
        SynkLibs.getInstance().setPluginPrefix(prefix);
        new Utils().checkUpdate("ChatWatchdog", "1.0", "https://modrinth.com/plugin/chatwatchdog");

        Bukkit.getPluginManager().registerEvents(new EventHandler(), this);
        getCommand("chatwatchdog").setExecutor(new CWCmd());
        getCommand("chatwatchdog").setTabCompleter(new CWCmd());
    }

    public void initConfig() {
        getConfig().options().setHeader(new ArrayList<>(Arrays.asList("If you set this to true, instead of swear words being censored, the whole message will be deleted")));
        getConfig().addDefault("delete-messages", false);
        getConfig().addDefault("use-default-list", true);
        getConfig().options().setFooter(new ArrayList<>(Arrays.asList("If you set the above to false, instead of using the default list of swear words (https://raw.githubusercontent.com/whomwah/language-timothy/master/profanity-list.txt), we'll only use the ones you setup yourself.")));
        getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();
    }

    public void loadConfig() {
        delete = getConfig().getBoolean("delete-messages");
        defaultList = getConfig().getBoolean("use-default-list");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
