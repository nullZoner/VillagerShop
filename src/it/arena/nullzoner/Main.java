package it.arena.nullzoner;

import it.arena.nullzoner.command.CommandVShop;
import it.arena.nullzoner.listeners.ListenerVillagerShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    public Economy economy = null;

    @Override
    public void onEnable() {
        instance=this;

        getCommand("vshop").setExecutor(new CommandVShop());
        getServer().getPluginManager().registerEvents(new ListenerVillagerShop(), this);

        saveDefaultConfig();

        setupEconomy();
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers())
            player.closeInventory();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
