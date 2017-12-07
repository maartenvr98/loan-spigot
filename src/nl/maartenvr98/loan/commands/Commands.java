package nl.maartenvr98.loan.commands;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import nl.maartenvr98.loan.getloan.Loan;
import nl.maartenvr98.loan.refund.Refund;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.Double.parseDouble;

public class Commands implements CommandExecutor {

    private Plugin plugin;
    private FileConfiguration config;
    private Loan loan;
    private Refund refund;
    private Economy econ = null;

    public Commands(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        this.loan = new Loan(plugin, config);
        this.refund = new Refund(plugin, config);

        if (!setupEconomy() ) {
            plugin.getLogger().info("Disabled due to no Vault dependency found!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
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
                                    case "help":
                                    case "?":
                                        sendAdminHelp(p);
                                    case "reload":
                                        plugin.reloadConfig();
                                        sendLine(p, config.getString("messages.reload"));
                                        break;
                                    case "overview":
                                    case "overzicht":
                                        if(args.length == 2) {
                                            sendLine(p, config.getString("messages.loan-overview-header"));
                                            List<String> loans = (List<String>) config.getList("loans");
                                        }
                                        else {
                                            Player player = plugin.getServer().getPlayer(args[2]);
                                            if(player == null) {
                                                sendLine(p, config.getString("messages.player-not-online"));
                                            }
                                            else {
                                                sendLine(p, config.getString("messages.loan-overview-header"));
                                                List<String> loans = (List<String>) config.getList("loans"+p.getUniqueId()+".history");
                                                for (String loan: loans) {
                                                    String[] items = loan.split(":");
                                                    sendLine(p, config.getString("messages.loan-overview-line-player").replace("{date}", items[0]).replace("{amount}", items[1]));
                                                }
                                            }
                                        }
                                        break;
                                    case "set":
                                        if(args.length == 4) {
                                            Player player = plugin.getServer().getPlayer(args[2]);
                                            if(player == null) {
                                                sendLine(p, config.getString("messages.player-not-online"));
                                            }
                                            else {
                                                if(!isInteger(args[3])) {
                                                    sendLine(p, config.getString("messages.invalid"));
                                                }
                                                else {
                                                    String path = "loans."+player.getUniqueId();
                                                    if(!config.isSet(path)) {
                                                        EconomyResponse response = econ.depositPlayer(player, parseDouble(args[3]));
                                                        if(response.transactionSuccess()) {
                                                            config.set(path+".amount", parseDouble(args[3]));
                                                            config.set(path+".time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                                            plugin.saveConfig();
                                                            sendLine(p, config.getString("messages.loan-set").replace("{player}", player.getName()));
                                                            sendLine(player, config.getString("messages.success").replace("{loan_amount}", args[3]));
                                                        }
                                                        else {
                                                            sendLine(p, response.errorMessage);
                                                        }
                                                    }
                                                    else {
                                                        sendLine(p, config.getString("messages.loan-set"));
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            sendLine(p, config.getString("messages.invalid"));
                                        }
                                        break;
                                    case "remove":
                                        if(args.length == 3) {
                                            Player player = plugin.getServer().getPlayer(args[2]);
                                            if(player == null) {
                                                sendLine(p, config.getString("messages.player-not-online"));
                                            }
                                            else {
                                                config.set("loans."+player.getUniqueId(), null);
                                                plugin.saveConfig();
                                                sendLine(player, config.getString("messages.loan-remitted"));
                                                sendLine(p, config.getString("messages.loan-removed"));
                                            }
                                        }
                                        else {
                                            sendLine(p, config.getString("messages.invalid"));
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
                    case "aanvragen":
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
                    case "afbetalen":
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
                        break;
                    default:
                        loan.get(p);
                }
            }
            else {
                loan.get(p);
            }
        }
        else {
            plugin.getLogger().info("You need to be an actual player");
        }
        return true;
    }

    public void sendHelp(Player p) {
        List<String> help = (List<String>) config.getList("messages.help");
        for (String helpitem: help) {
            sendLine(p, helpitem);
        }
    }

    public void sendAdminHelp(Player p) {
        List<String> help = (List<String>) config.getList("messages.adminhelp");
        for (String helpitem: help) {
            sendLine(p, helpitem);
        }
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



    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
