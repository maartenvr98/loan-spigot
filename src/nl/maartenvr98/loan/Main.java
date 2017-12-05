package nl.maartenvr98.loan;

import net.milkbowl.vault.economy.Economy;
import nl.maartenvr98.loan.commands.Commands;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public FileConfiguration config = getConfig();
    public Economy econ = null;

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

        getCommand("lening").setExecutor(new Commands(this, config));

        if (!setupEconomy() ) {
            this.getLogger().info("Disabled due to no Vault dependency found!");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }

    public Economy getEconomy() {
        return econ;
    }

    public boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
