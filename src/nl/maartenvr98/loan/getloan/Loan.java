package nl.maartenvr98.loan.getloan;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Loan {

    private Plugin plugin;
    private FileConfiguration config;
    private Double maxloan;

    public Loan(Plugin plugin, FileConfiguration config) {
        plugin = plugin;
        config = config;
        maxloan = config.getDouble("maxloan");
    }

    private void create(Player p, Double amount) {

    }
}
