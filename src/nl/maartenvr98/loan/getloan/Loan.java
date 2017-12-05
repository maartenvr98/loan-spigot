package nl.maartenvr98.loan.getloan;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.xml.bind.DatatypeConverter.parseString;

public class Loan {

    private Plugin plugin;
    private FileConfiguration config;
    private Double maxloan;
    private Economy econ = null;

    public Loan(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        maxloan = config.getDouble("maxloan");

        if (!setupEconomy() ) {
            plugin.getLogger().info("Disabled due to no Vault dependency found!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void create(Player p, Double amount) {
        String path = "loans."+p.getUniqueId();
        if(!config.isSet(path)) {
            if(amount > maxloan) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.maxloan")));
            }
            else {
                EconomyResponse response = econ.depositPlayer(p, amount);
                if(response.transactionSuccess()) {
                    config.set(path+".amount", amount);
                    config.set(path+".time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
