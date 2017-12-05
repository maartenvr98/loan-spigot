package nl.maartenvr98.loan;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled");

        config.addDefault("limit", 1);
        config.addDefault("maxloan", 1000);
        config.addDefault("messages.limit", "&cJe kan niet meer dan 1 lening hebben");
        config.addDefault("messages.maxloan", "&cJe kan niet meer dan 1000 lenen");
        config.addDefault("messages.no-permission", "&cJe hebt geen toegang tot dit command");
        config.addDefault("messages.invalid", "&cInvalid arguments");
        config.options().copyDefaults(true);
        saveConfig();

        getCommand("lening").setExecutor(new Command(this, config));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }

}
