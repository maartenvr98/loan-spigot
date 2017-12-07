package nl.maartenvr98.loan.refund;

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
import java.util.List;

public class Refund {

    private Plugin plugin;
    private FileConfiguration config;
    private Economy econ = null;

    public Refund(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;

        if (!setupEconomy() ) {
            plugin.getLogger().info("Disabled due to no Vault dependency found!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void pay(Player p, Double amount) {
        String path = "loans."+p.getUniqueId();
        if(config.isSet(path)) {
            Double current = config.getDouble(path+".amount");
            if(current < amount) {
                amount = current;
            }
            Double new_amount = current - amount;
            if(new_amount == 0) {
                EconomyResponse response = econ.withdrawPlayer(p, amount);
                if(response.transactionSuccess()) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.fullpaid")));
                    config.set(path+".paid", true);
                    config.set(path+".total", 0);
                    config.set(path+".amount", 0);
                    config.set(path+".time", "");
                    config.set(path+".refunds", new ArrayList());

                    List<String> history = (List<String>) config.getList(path+".history");
                    history.add(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ":" + amount);
                    config.set(path+".history", history);

                    plugin.saveConfig();
                }
                else {
                    p.sendMessage(response.errorMessage);
                }
            }
            else {
                EconomyResponse response = econ.withdrawPlayer(p, amount);
                if(response.transactionSuccess()) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.paid").replace("{amount}", String.valueOf(amount))));
                    config.set(path+".amount", new_amount);
                    List<String> refunds = (List<String>) config.getList(path+".refunds");
                    refunds.add(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + " - " + amount);
                    plugin.saveConfig();
                }
                else {
                    p.sendMessage(response.errorMessage);
                }
            }
        }
        else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-loan")));
        }
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