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
        getCommand("loan").setExecutor(new Commands(this, config));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }

    public void setupConfig() {
        config.addDefault("maxloan", 10000);
        config.addDefault("messages.reload", "&aConfig reloaed");
        config.addDefault("messages.limit", "&cYou cannot have more than 1 loan");
        config.addDefault("messages.maxloan", "&cYou cannot borrow more than 10000");
        config.addDefault("messages.no-permission", "&cYou don't have access to this command");
        config.addDefault("messages.invalid", "&cInvalid arguments");
        config.addDefault("messages.success", "&aLoan received of {loan_amount}");
        config.addDefault("messages.no-loan", "&aYou do not have a loan");
        config.addDefault("messages.loan", "&aYou have a loan of {money} and you have paid {money_refund}. You need to pay {money_left} to pay off your loan");
        config.addDefault("messages.paid", "&aYou have paid {amount}");
        config.addDefault("messages.fullpaid", "&Congrats. You have paid off your loan!");
        config.addDefault("messages.has-loan", "&cThis player does not have a loan");
        config.addDefault("messages.player-not-online", "&cThis player isn't online");
        config.addDefault("messages.loan-loan-set", "&aloan gived to {player}");
        config.addDefault("messages.loan-remitted", "&aYour loan is remitted");
        config.addDefault("messages.loan-removed", "&aLoan is removed");
        config.addDefault("messages.no-loan-player", "&aThis player does not have loans");

        List<String> help = new ArrayList();
        help.add("&a/loan help");
        help.add("&a/loan get <bedrag>");
        help.add("&a/loan pay <bedrag>");
        config.addDefault("messages.help", help);

        List<String> adminhelp = new ArrayList();
        adminhelp.add("&a/loan admin help");
        adminhelp.add("&a/loan admin view <speler> (optional)");
        adminhelp.add("&a/loan admin set <speler> <bedrag>");
        adminhelp.add("&a/loan admin remove <speler>");
        config.addDefault("messages.adminhelp", adminhelp);


        config.addDefault("messages.loan-overview-header", "&7------&aAll loans&7------");
        config.addDefault("messages.loan-overview-line", "&a{player} has a loan of {amount}");
        config.addDefault("messages.loan-overview-line-player", "&a{date}: {amount}");

        config.options().copyDefaults(true);
        saveConfig();
    }
}
