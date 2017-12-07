package nl.maartenvr98.loan.getloan;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Double.parseDouble;

public class Loan {

    private Plugin plugin;
    private FileConfiguration config;
    private Integer maxloan;
    private Economy econ = null;

    public Loan(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        maxloan = 1;

        if (!setupEconomy() ) {
            plugin.getLogger().info("Disabled due to no Vault dependency found!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private boolean check(Player p) {
        if(config.isSet("loans."+p.getUniqueId())) {
            if(config.getBoolean("loans."+p.getUniqueId()+".paid")) {
                return true;
            }
            return false;
        }
        return true;
    }

    public void create(Player p, Double amount) {
        String path = "loans."+p.getUniqueId();
        if(check(p)) {
            if(amount > maxloan) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.maxloan")));
            }
            else {
                EconomyResponse response = econ.depositPlayer(p, amount);
                if(response.transactionSuccess()) {
                    config.set(path+".paid", false);
                    config.set(path+".total", amount);
                    config.set(path+".amount", amount);
                    config.set(path+".time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    if(!config.isSet(path+".refunds")) {
                        config.set(path+".refunds", new ArrayList());
                    }
                    if(!config.isSet(path+".history")) {
                        config.set(path+".history", new ArrayList());
                    }
                    plugin.saveConfig();
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.success").replace("{loan_amount}", String.valueOf(amount))));
                }
                else {
                    p.sendMessage(response.errorMessage);
                }
            }
        }
        else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.limit")));
        }
    }

    public void get(Player p) {
        String path = "loans."+p.getUniqueId();
        if(!config.isSet(path)) {
            sendLine(p, config.getString("messages.no-loan"));
        }
        else {
            String money = config.getString("loans."+p.getUniqueId()+".total");
            String money_left = config.getString("loans."+p.getUniqueId()+".amount");
            Double money_refund = parseDouble(money) - parseDouble(money_left);
            sendLine(p, config.getString("messages.loan").replace("{money}", money).replace("{money_left}", money_left).replace("{money_refund}", String.valueOf(money_refund)));
        }
    }

    public void sendLine(Player p, String text) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
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
