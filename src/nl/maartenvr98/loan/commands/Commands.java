package nl.maartenvr98.loan.commands;

import nl.maartenvr98.loan.getloan.Loan;
import nl.maartenvr98.loan.refund.Refund;
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
                        else {
                            loan.create(p, parseDouble(args[0]));
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
}
