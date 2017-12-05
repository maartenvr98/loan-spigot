package nl.maartenvr98.loan.commands;

import nl.maartenvr98.loan.getloan.Loan;
import nl.maartenvr98.loan.refund.Refund;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static java.lang.Double.parseDouble;

public class Commands implements CommandExecutor {

    private Plugin plugin;
    private FileConfiguration config;
    private Loan loan;
    private Refund refund;

    public Commands(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        this.loan = new Loan(plugin, config);
        this.refund = new Refund(plugin, config);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length >= 1) {
                switch (args[0]) {
                    case "help":
                    case "?":
                        sendHelp(p);
                        break;
                    case "over":
                    case "about":
                        p.sendMessage("§aLoan plugin by maartenvr98\nhttps://www.spigotmc.org/members/maartenvr98.88681/");
                        break;
                    case "admin":
                        if(p.hasPermission("loan.admin") || p.isOp()) {
                            if(args.length == 1) {
                                sendAdminHelp(p);
                            }
                            else {
                                switch (args[1]) {
                                    case "reload":
                                        plugin.reloadConfig();
                                        break;
                                    case "set":

                                        break;
                                    case "remove":
                                        if(args.length == 3) {
                                            Player player = plugin.getServer().getPlayer(args[2]);
                                            if(player == null) {
                                                sendLine(p, config.getString("messages.player-not-online"));
                                            }
                                            config.set("loans."+player.getUniqueId(), null);
                                            plugin.saveConfig();
                                            sendLine(player, config.getString("messages.loan-remitted"));
                                            sendLine(p, config.getString("messages.loan-removed"));
                                        }
                                        break;
                                    default:
                                        sendAdminHelp(p);
                                }
                            }
                        }
                        else {
                            sendLine(p, config.getString("messages.no-permission"));
                            return true;
                        }
                        break;
                    case "get":
                        if(args.length == 1) {
                            sendLine(p, config.getString("messages.invalid"));
                            return true;
                        }
                        else {
                            if(!isInteger(args[1])) {
                                sendLine(p, config.getString("messages.invalid"));
                            }
                            else {
                                loan.create(p, parseDouble(args[1]));
                            }
                        }
                        break;
                    case "refund":
                        if(args.length == 1) {
                            sendLine(p, config.getString("messages.invalid"));
                            return true;
                        }
                        else {
                            if(!isInteger(args[1])) {
                                sendLine(p, config.getString("messages.invalid"));
                            }
                            else {
                                refund.pay(p, parseDouble(args[1]));
                            }
                        }
                    default:
                        sendLine(p, config.getString("messages.invalid"));
                }
            }
            else {
                sendLine(p, config.getString("messages.invalid"));
            }
        }
        else {
            plugin.getLogger().info("You need to be an actual player");
        }
        return true;
    }

    public void sendHelp(Player p) {

    }

    public void sendAdminHelp(Player p) {

    }

    public void sendLine(Player p, String text) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
