package nl.maartenvr98.loan;

import nl.maartenvr98.loan.commands.Commands;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    public FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled");
        setupConfig();
        getCommand("lening").setExecutor(new Commands(this, config));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }

    public void setupConfig() {
        config.addDefault("limit", 1);
        config.addDefault("maxloan", 1000);
        config.addDefault("messages.reload", "&aConfig herladen");
        config.addDefault("messages.limit", "&cJe kan niet meer dan 1 lening hebben");
        config.addDefault("messages.maxloan", "&cJe kan niet meer dan 1000 lenen");
        config.addDefault("messages.no-permission", "&cJe hebt geen toegang tot dit command");
        config.addDefault("messages.invalid", "&cInvalid arguments");
        config.addDefault("messages.success", "&aLening van {loan_amount} ontvangen");
        config.addDefault("messages.no-loan", "&aJe hebt geen lening");
        config.addDefault("messages.loan", "&aJe hebt een lening van {money} en je hebt al {money_refund} afbetaald en je moet nog {money_left} betalen");
        config.addDefault("messages.paid", "&aJe hebt {amount} betaald");
        config.addDefault("messages.fullpaid", "&aGefeliciteerd. Je hebt lening afbetaald!");
        config.addDefault("messages.has-loan", "&cDeze speler heeft al een lening");
        config.addDefault("messages.player-not-online", "&cDeze speler is niet online");
        config.addDefault("messages.loan-loan-set", "&aLening gegeven aan {player}");
        config.addDefault("messages.loan-remitted", "&aJe lening is kwijtgescholden");
        config.addDefault("messages.loan-removed", "&aLening is verwijderd");
        config.addDefault("messages.no-loan-player", "&aDeze speler heeft geen leningen");

        List<String> help = new ArrayList();
        help.add("&a/lening help");
        help.add("&a/lening aanvragen <bedrag>");
        help.add("&a/lening afbetalen <bedrag>");
        config.addDefault("messages.help", help);

        List<String> adminhelp = new ArrayList();
        adminhelp.add("&a/lening admin help");
        adminhelp.add("&a/lening admin overzicht <speler> (optionee;)");
        adminhelp.add("&a/lening admin set <speler> <bedrag>");
        adminhelp.add("&a/lening admin remove <speler>");
        config.addDefault("messages.adminhelp", adminhelp);


        config.addDefault("messages.loan-overview-header", "&7------&aAlle leningen&7------");
        config.addDefault("messages.loan-overview-line", "&a{player} heeft een lening van {amount}");
        config.addDefault("messages.loan-overview-line-player", "&a{date}: {amount}");

        config.options().copyDefaults(true);
        saveConfig();
    }
}
