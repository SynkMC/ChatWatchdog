package cc.synkdev.chatWatchdog.bungee;

import cc.synkdev.chatWatchdog.bungee.commands.CWCmd;
import cc.synkdev.chatWatchdog.bungee.managers.EventHandler;
import cc.synkdev.chatWatchdog.bungee.managers.Lang;
import cc.synkdev.chatWatchdog.bungee.managers.WordMapManager;
import cc.synkdev.synkLibs.bungee.SynkLibsBungee;
import cc.synkdev.synkLibs.bungee.UtilsBungee;
import cc.synkdev.synkLibs.components.SynkPlugin;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ChatWatchdogBungee extends Plugin implements SynkPlugin {
    private WordMapManager wmm;
    @Getter private static ChatWatchdogBungee instance;
    public Lang lang;
    @Getter private Boolean defaultList = true;
    public List<String> wordsMap = new ArrayList<>();
    public List<String> localWordsMap = new ArrayList<>();
    @Getter private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6ChatWatchdog&8] » &r");
    private Configuration config;
    private File configFile = new File(this.getDataFolder(), "config.yml");

    @Override
    public void onEnable() {
        instance = this;
        lang = new Lang();
        wmm = new WordMapManager(this);
        initConfig();
        SynkLibsBungee.setSpl(this);
        wmm.load();
        new Metrics(this, 23041);
        UtilsBungee.checkUpdate(this, this);

        getProxy().getPluginManager().registerListener(this, new EventHandler());
        getProxy().getPluginManager().registerCommand(this, new CWCmd());
    }

    public void initConfig() {
        File tempFile = new File(this.getDataFolder(), "temp-"+System.currentTimeMillis()+".yml");
        try {
            if (!configFile.getParentFile().exists()) configFile.getParentFile().mkdirs();
            if (!configFile.exists()) configFile.createNewFile();
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            URL url = new URL("https://synkdev.cc/storage/config-cwd-bungee.php");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                for (String s : line.split("<br>")) {
                    String id = s.split(":")[1];
                    if (config.contains(id)) {
                        writer.write("# "+s.split(":")[0]);
                        writer.newLine();
                        writer.write(id + ": " + config.getString(id));
                        writer.newLine();
                    } else {
                        writer.write("# "+s.split(":")[0]);
                        writer.newLine();
                        writer.write(id+": "+s.split(":")[2]);
                        writer.newLine();
                    }
                }
            }
            reader.close();
            writer.close();
            tempFile.renameTo(configFile);
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadConfig() {
        defaultList = config.getBoolean("use-default-list");
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
