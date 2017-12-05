package nl.maartenvr98.loan.commands;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import nl.maartenvr98.loan.getloan.Loan;
import nl.maartenvr98.loan.getloan.Refund;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import static java.lang.Double.parseDouble;

public class Commands implements CommandExecutor {

    private Plugin plugin;
    private FileConfiguration config;
    private Economy econ = null;
    private Loan loan;
    private Refund refund;

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
            if(s.equalsIgnoreCase("lening")) {
                switch (args[0]) {
                    case "help":
                    case "?":
                        sendHelp(p);
                        break;
                    case "about":
                        p.sendMessage("§aLoan plugin by maartenvr98 <https://www.spigotmc.org/members/maartenvr98.88681/>");
                        break;
                    case "admin":
                        if(!p.hasPermission("loan.admin")) {
                            p.sendMessage(config.getString("messages.no-permission"));
                            return false;
                        }
                        break;
                    default:
                        if(!isInteger(args[0])) {
                            p.sendMessage(config.getString("messages.invalid"));
                        }
                        String path = "loans."+p.getUniqueId();
                        if(!config.isSet(path)) {
                            if(parseDouble(args[0]) > config.getDouble("maxloan")) {
                                p.sendMessage(config.getString("messages.maxloan"));
                            }
                            else {
                                EconomyResponse response = econ.depositPlayer(p, parseDouble(args[0]));
                                if(response.transactionSuccess()) {
                                    config.set(path, parseDouble(args[0]));
                                    plugin.saveConfig();
                                }
                                else {
                                    p.sendMessage(response.errorMessage);
                                }
                            }
                        }
                        else {
                            p.sendMessage(config.getString("messages.limit"));
                        }
                }
            }
        }
        else {
            plugin.getLogger().info("You need to be an actual player");
        }
        return false;
    }

    public void sendHelp(Player p) {

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
