package cc.synkdev.chatWatchdog.bukkit.managers;

import cc.synkdev.chatWatchdog.bukkit.ChatWatchdog;
import cc.synkdev.nexusCore.bukkit.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class WordMapManager {
    ChatWatchdog core;
    List<String> tempList = new ArrayList<>();
    File file;
    Lang lang = new Lang();
    public WordMapManager(ChatWatchdog core) {
        this.core = core;
        this.file = new File(core.getDataFolder(), "blocked-words.txt");
    }
    public void load() {
        long time = System.currentTimeMillis();
        tempList.clear();
        initFile();
        if (core.getDefaultList()) loadGlobal();
        readFile();
        core.wordsMap.clear();
        core.wordsMap.addAll(tempList);
        long total = System.currentTimeMillis()-time;
        Utils.log(ChatColor.GREEN+lang.translate("fetched", core.wordsMap.size()+"", String.valueOf(total)));
    }

    private void readFile() {
        List<String> localList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tempList.add(line);
                localList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        core.localWordsMap.clear();
        core.localWordsMap.addAll(localList);
    }

    private void loadGlobal() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/whomwah/language-timothy/master/profanity-list.txt");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                tempList.add(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initFile() {
        if (!core.getDataFolder().exists()) {
            core.getDataFolder().mkdirs();
        }

        file = new File(core.getDataFolder(), "blocked-words.txt");

        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendLocalList(CommandSender sender, int page) {
        sender.sendMessage(core.getPrefix()+ChatColor.GOLD+lang.translate("filterList"));
        TextComponent minus = new TextComponent("[-]");
        minus.setBold(true);
        minus.setColor(net.md_5.bungee.api.ChatColor.RED);

        Boolean first = page == 1;
        Boolean last = page*10 > core.localWordsMap.size();
        int max = Math.toIntExact(Math.round(Math.ceil(core.localWordsMap.size()/10)))+1;

        if (page>max) {
            sender.sendMessage(core.getPrefix()+ChatColor.RED+lang.translate("pageDoesntExist"));
            return;
        }

        TextComponent pageMin = new TextComponent("< ");
        pageMin.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        pageMin.setBold(true);
        pageMin.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cw filter "+(page-1)));

        TextComponent pageSup = new TextComponent(" >");
        pageSup.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        pageSup.setBold(true);
        pageSup.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cw filter "+(page+1)));

        for (int i = (page-1)*10; i < page*10; i++) {
            if (core.localWordsMap.size()>i) {
                TextComponent compo = new TextComponent(core.getPrefix()+ChatColor.GOLD+"- "+core.localWordsMap.get(i)+" ");
                minus.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cw filter remove "+core.localWordsMap.get(i)));
                compo.addExtra(minus);
                sender.spigot().sendMessage(compo);
            }
        }
        TextComponent sq1 = new TextComponent("[ ");
        sq1.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        TextComponent sq2 = new TextComponent(" ]");
        sq2.setColor(net.md_5.bungee.api.ChatColor.GREEN);

        if (!first) sq1.addExtra(pageMin);
        sq1.addExtra(lang.translate("filterListPage", page+"", String.valueOf(max)));
        if (!last) sq1.addExtra(pageSup);
        sq1.addExtra(sq2);
        sender.spigot().sendMessage(sq1);
    }
    public void add(CommandSender sender, String sequence) {
        if (core.wordsMap.contains(sequence)) {
            sender.sendMessage(core.getPrefix()+ChatColor.RED+lang.translate("alreadyBlocked"));
            return;
        }

        core.wordsMap.add(sequence);
        core.localWordsMap.add(sequence);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String s : core.localWordsMap) {
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sender.sendMessage(core.getPrefix()+ChatColor.GREEN+lang.translate("wasAdded", sequence));
    }
    public void remove(CommandSender sender, String sequence) {
        if (!core.wordsMap.contains(sequence)) {
            sender.sendMessage(core.getPrefix()+ChatColor.RED+lang.translate("notBlocked"));
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            File temp = new File(file.getParent(), "temp-"+System.currentTimeMillis()+".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equalsIgnoreCase(sequence)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.close();
            reader.close();
            file.delete();
            Files.move(temp.toPath(), file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        core.wordsMap.remove(sequence);
        core.localWordsMap.remove(sequence);
        sender.sendMessage(core.getPrefix()+ChatColor.GREEN+lang.translate("wasRemoved", sequence));
    }
    public Boolean containsBlockedWords(AsyncPlayerChatEvent event) {
        for (String s : core.wordsMap) {
            if (event.getMessage().contains(s)) {
                event.getPlayer().sendMessage(core.getPrefix()+ChatColor.RED+lang.translate("messageBlocked", s));
                return true;
            }
        }
        return false;
    }
    public List<String> getBlockedSequences(String s) {
        List<String> strings = new ArrayList<>();
        for (String ss : core.wordsMap) {
            if (s.contains(ss)) strings.add(ss);
        }
        return strings;
    }
}
