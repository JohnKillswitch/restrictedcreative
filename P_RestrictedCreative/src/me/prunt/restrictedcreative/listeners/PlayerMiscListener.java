package me.prunt.restrictedcreative.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.prunt.restrictedcreative.Main;
import me.prunt.restrictedcreative.storage.DataHandler;

public class PlayerMiscListener implements Listener {
    private Main main;

    public PlayerMiscListener(Main main) {
	this.main = main;
    }

    private Main getMain() {
	return this.main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
	Player p = e.getPlayer();

	// No need to control gamemode changes in disabled worlds
	if (getMain().getUtils().isDisabledWorld(p.getWorld().getName()))
	    return;

	// Player wants to switch into creative mode
	if (e.getNewGameMode() == GameMode.CREATIVE && p.getGameMode() != GameMode.CREATIVE) {
	    // Player height check
	    if (getMain().getUtils().isHeightOk(p)) {
		getMain().getUtils().sendMessage(p, true, "disabled.region");
		return;
	    }

	    // Prevents opening a container, switching to creative mode, and dumping items
	    if (illegalContainerOpened(p)) {
		e.setCancelled(true);
		getMain().getUtils().sendMessage(p, true, "disabled.general");
		return;
	    }

	    // Put into creative mode
	    getMain().getUtils().setCreative(p, true);
	}

	// Player want's to switch out of creative
	else if (e.getNewGameMode() != GameMode.CREATIVE && p.getGameMode() == GameMode.CREATIVE) {
	    // Remove creative mode
	    getMain().getUtils().setCreative(p, false);
	}
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
	Player p = e.getPlayer();
	String command = e.getMessage();

	/* Old way of handling aliases */
	if (DataHandler.isUsingOldAliases()) {
	    // Loop through command list
	    for (String cmd : getMain().getSettings().getConfig().getConfigurationSection("commands").getKeys(false)) {
		// Loop through alias list
		for (String alias : getMain().getSettings().getStringList("")) {
		    // If the message matches or starts with the alias
		    // (+ space to not catch other commands)
		    if (command.startsWith("/" + alias + " ") || command.equalsIgnoreCase("/" + alias)) {
			main.getCommand(cmd).execute(p, alias, new String[0]);
			e.setCancelled(true);
			return;
		    }
		}
	    }
	}

	// No need to control drops in disabled worlds
	if (getMain().getUtils().isDisabledWorld(p.getWorld().getName()))
	    return;

	// No need to control non-creative players
	if (p.getGameMode() != GameMode.CREATIVE)
	    return;

	// No need to control bypassed players
	if (p.hasPermission("rc.bypass.limit.item.drop")
		|| p.hasPermission("rc.bypass.limit.item.drop." + command.split(" ")[0]))
	    return;

	// Loops through all disabled commands
	for (String regex : getMain().getSettings().getStringList("limit.commands")) {
	    // .substring(1) removes "/" from the command
	    if (command.substring(1).toLowerCase().matches(regex)) {
		e.setCancelled(true);
		getMain().getUtils().sendMessage(p, true, "disabled.general");
		return;
	    }
	}
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
	Player p = e.getPlayer();

	// No need to control world changing in enabled worlds
	if (!getMain().getUtils().isDisabledWorld(p.getWorld().getName()))
	    return;

	// No need to control non-creative players
	if (p.getGameMode() != GameMode.CREATIVE)
	    return;

	// Removes creative mode
	p.setGameMode(DataHandler.getPreviousGameMode(p));
	getMain().getUtils().setCreative(p, false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
	if (!getMain().getSettings().isEnabled("limit.moving.enabled"))
	    return;

	Player p = e.getPlayer();

	// No need to control moving in disabled worlds
	if (getMain().getUtils().isDisabledWorld(p.getWorld().getName()))
	    return;

	// No need to control non-creative players
	if (p.getGameMode() != GameMode.CREATIVE)
	    return;

	if (getMain().getUtils().isHeightOk(p))
	    return;

	p.setGameMode(DataHandler.getPreviousGameMode(p));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
	Player p = e.getEntity();

	// No need to control moving in disabled worlds
	if (getMain().getUtils().isDisabledWorld(p.getWorld().getName()))
	    return;

	// No need to control non-creative players
	if (p.getGameMode() != GameMode.CREATIVE)
	    return;

	// No need to control disabled features
	if (!getMain().getSettings().isEnabled("limit.item.drop"))
	    return;

	// No need to control bypassed players
	if (p.hasPermission("rc.bypass.limit.item.drop"))
	    return;

	// Removes all drops
	e.getDrops().clear();
	e.setDroppedExp(0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
	Player p = e.getPlayer();

	DataHandler.removeInfoWithCommand(p);
	DataHandler.removeAddWithCommand(p);
	DataHandler.removeRemoveWithCommand(p);

	// No need to control inventories in disabled worlds
	if (getMain().getUtils().isDisabledWorld(p.getWorld().getName()))
	    return;

	// No need to control non-creative players
	if (p.getGameMode() != GameMode.CREATIVE)
	    return;

	// No need to control bypassed players
	if (p.hasPermission("rc.bypass.tracking.inventory.enabled"))
	    return;

	p.setGameMode(DataHandler.getPreviousGameMode(p));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent e) {
	Player p = e.getPlayer();

	DataHandler.removeInfoWithCommand(p);
	DataHandler.removeAddWithCommand(p);
	DataHandler.removeRemoveWithCommand(p);

	// No need to control inventories in disabled worlds
	if (getMain().getUtils().isDisabledWorld(p.getWorld().getName()))
	    return;

	// No need to control non-creative players
	if (p.getGameMode() != GameMode.CREATIVE)
	    return;

	// No need to control bypassed players
	if (p.hasPermission("rc.bypass.tracking.inventory.enabled"))
	    return;

	p.setGameMode(DataHandler.getPreviousGameMode(p));
    }

    private boolean illegalContainerOpened(Player p) {
	InventoryType it = p.getOpenInventory().getType();

	return it != InventoryType.PLAYER && it != InventoryType.CRAFTING && it != InventoryType.CREATIVE
		&& !p.hasPermission("rc.bypass.tracking.inventory")
		&& getMain().getSettings().isEnabled("limit.item.drop");
    }
}