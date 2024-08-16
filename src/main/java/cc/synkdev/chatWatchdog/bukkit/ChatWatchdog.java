package cc.synkdev.chatWatchdog.bukkit;

import cc.synkdev.bstats.bukkit.Metrics;
import cc.synkdev.chatWatchdog.bukkit.commands.CWCmd;
import cc.synkdev.chatWatchdog.bukkit.managers.EventHandler;
import cc.synkdev.chatWatchdog.bukkit.managers.WordMapManager;
import cc.synkdev.synkLibs.bukkit.Utils;
import cc.synkdev.synkLibs.components.SynkPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ChatWatchdog extends JavaPlugin implements SynkPlugin {
    private WordMapManager wmm;
    @Getter private static ChatWatchdog instance;
    public cc.synkdev.chatWatchdog.bukkit.managers.Lang lang;
    @Getter private Boolean delete = false;
    @Getter private Boolean defaultList = true;
    public List<String> wordsMap = new ArrayList<>();
    public List<String> localWordsMap = new ArrayList<>();
    @Getter private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6ChatWatchdog&8] » &r");

    @Override
    public void onEnable() {
        instance = this;
        lang = new cc.synkdev.chatWatchdog.bukkit.managers.Lang();
        wmm = new WordMapManager(this);
        initConfig();
        wmm.load();
        new Metrics(this, 23020);
        Utils.checkUpdate(this, this);

        Bukkit.getPluginManager().registerEvents(new EventHandler(), this);
        getCommand("chatwatchdog").setExecutor(new CWCmd());
        getCommand("chatwatchdog").setTabCompleter(new CWCmd());
    }

    public void initConfig() {
        getConfig().options().header("If you set this to true, instead of swear words being censored, the whole message will be deleted");
        getConfig().addDefault("delete-messages", false);
        getConfig().addDefault("use-default-list", true);
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

    @Override
    public String name() {
        return "ChatWatchdog";
    }

    @Override
    public String ver() {
        return "1.2";
    }

    @Override
    public String dlLink() {
        return "https://modrinth.com/plugin/chatwatchdog";
    }

    @Override
    public String prefix() {
        return prefix;
    }
}
