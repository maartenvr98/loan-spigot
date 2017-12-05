package nl.maartenvr98.loan.getloan;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Refund {

    private Plugin plugin;
    private FileConfiguration config;

    public Refund(Plugin plugin, FileConfiguration config) {
        plugin = plugin;
        config = config;
    }

}