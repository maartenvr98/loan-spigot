package nl.maartenvr98.loan.refund;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Refund {

    private Plugin plugin;
    private FileConfiguration config;

    public Refund(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

}