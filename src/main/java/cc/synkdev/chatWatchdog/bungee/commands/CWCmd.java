package cc.synkdev.chatWatchdog.bungee.commands;

import cc.synkdev.chatWatchdog.bungee.ChatWatchdogBungee;
import cc.synkdev.chatWatchdog.bungee.managers.Lang;
import cc.synkdev.chatWatchdog.bungee.managers.WordMapManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class CWCmd extends Command implements TabExecutor {
    public CWCmd() {
        super("chatwatchdog");
    }
    CommandSender sender;
    ChatWatchdogBungee core = ChatWatchdogBungee.getInstance();
    WordMapManager wmm = new WordMapManager(core);
    Lang lang = new Lang();

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> argss = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("chatwatchdog.command.reload")) argss.add("reload");
            if (sender.hasPermission("chatwatchdog.command.filter.list")) argss.add("filter");
        } else if (args.length == 2) {
            switch (args[0]) {
                case "filter":
                    if (sender.hasPermission("chatwatchdog.filter.edit")) {
                        argss.add("remove");
                        argss.add("add");
                    }
                    break;
            }
        }
        return argss;
    }
    private Boolean checkPerm(String s, Boolean msg) {
        if (!msg) {
            return sender.hasPermission(s);
        }

        if (sender.hasPermission(s)) return true;
        sender.sendMessage(core.getPrefix()+lang.translate("noPerm"));
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.sender = sender;
        if (args.length==0) {
            sender.sendMessage(core.getPrefix()+lang.help());
        } else if (args.length == 1) {
            switch (args[0]) {
                case "reload":
                    core.loadConfig();
                    wmm.load();
                    lang.load();
                    sender.sendMessage(core.getPrefix()+lang.translate("reloaded"));
                    break;
                case "filter":
                    if (checkPerm("chatwatchdog.filter.list", true)) wmm.sendLocalList(sender, 1);
                    break;
                default:
                    sender.sendMessage(core.getPrefix()+lang.help());
                    break;
            }
        } else if (args.length > 1) {
            switch (args[0]) {
                case "filter":
                    if (args[1].equalsIgnoreCase("add")) {
                        if (checkPerm("chatwatchdog.filter.edit", true)) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length-1; i++) {
                                if (args[i] != null) sb.append(args[i]).append(" ");
                            }
                            sb.append(args[args.length-1]);
                            wmm.add(sender, sb.toString());
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (checkPerm("chatwatchdog.filter.edit", true)) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length - 1; i++) {
                                if (args[i] != null) sb.append(args[i]).append(" ");
                            }
                            sb.append(args[args.length - 1]);
                            wmm.remove(sender, sb.toString());
                        }
                    } else {
                        int page;
                        try {
                            page = Integer.parseInt(args[1]);
                            if (checkPerm("chatwatchdog.filter.list", true)) wmm.sendLocalList(sender, page);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(core.getPrefix()+ ChatColor.RED+lang.translate("wrongPage"));
                        }
                    }
                    break;
                default:
                    sender.sendMessage(core.getPrefix()+lang.help());
                    break;
            }
        }
    }
}
