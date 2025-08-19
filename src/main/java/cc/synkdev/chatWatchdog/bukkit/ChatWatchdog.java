package cc.synkdev.chatWatchdog.bukkit;

import cc.synkdev.bstats.bukkit.Metrics;
import cc.synkdev.chatWatchdog.bukkit.commands.CWCmd;
import cc.synkdev.chatWatchdog.bukkit.managers.EventHandler;
import cc.synkdev.chatWatchdog.bukkit.managers.WordMapManager;
import cc.synkdev.synkLibs.components.SynkPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ChatWatchdog extends JavaPlugin implements SynkPlugin {
    private WordMapManager wmm;
    @Getter private static ChatWatchdog instance;
    public cc.synkdev.chatWatchdog.bukkit.managers.Lang lang;
    @Getter private Boolean delete = false;
    @Getter private Boolean defaultList = true;
    public List<String> wordsMap = new ArrayList<>();
    public List<String> localWordsMap = new ArrayList<>();
    public FileConfiguration config;
    @Getter private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6ChatWatchdog&8] » &r");

    @Override
    public void onEnable() {
        instance = this;
        lang = new cc.synkdev.chatWatchdog.bukkit.managers.Lang();
        wmm = new WordMapManager(this);
        updateConfig();
        loadConfig();
        wmm.load();
        new Metrics(this, 23020);

        Bukkit.getPluginManager().registerEvents(new EventHandler(), this);
        getCommand("chatwatchdog").setExecutor(new CWCmd());
        getCommand("chatwatchdog").setTabCompleter(new CWCmd());
    }

    private void updateConfig() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdirs();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            if (!configFile.exists()) {
                try {
                    Files.copy(getResource("config.yml"), configFile.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                File temp = new File(getDataFolder(), "temp-config-" + System.currentTimeMillis() + ".yml");
                try {
                    Files.copy(getResource("config.yml"), temp.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                FileConfiguration tempConfig = YamlConfiguration.loadConfiguration(temp);
                config = YamlConfiguration.loadConfiguration(configFile);
                boolean changed = false;
                for (String key : tempConfig.getKeys(true)) {
                    if (!config.contains(key)) {
                        config.set(key, tempConfig.get(key));
                        changed = true;
                    }
                }

                if (changed) {
                    config.save(configFile);
                }

                temp.delete();
            }
            config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
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
        return "1.5";
    }

    @Override
    public String dlLink() {
        return "https://modrinth.com/plugin/chatwatchdog";
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public String lang() {
        return null;
    }

    @Override
    public Map<String, String> langMap() {
        return null;
    }
}
