package me.prunt.restrictedcreative.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import me.prunt.restrictedcreative.Main;
import me.prunt.restrictedcreative.storage.DataHandler;

public class EntityDamageListener implements Listener {
    private Main main;

    public EntityDamageListener(Main main) {
	this.main = main;
    }

    private Main getMain() {
	return this.main;
    }

    /*
     * Called when an entity is damaged by an entity
     */
    // HIGHEST required for WorldGuard and similar plugins
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
	Entity en = e.getEntity();

	// No need to control entities in disabled worlds
	if (getMain().getUtils().isDisabledWorld(en.getWorld().getName()))
	    return;

	// Item frame
	if (en instanceof ItemFrame) {
	    ItemFrame frame = (ItemFrame) en;

	    // The item isn't going to pop off
	    if (frame.getItem() == null || frame.getItem().getType() == Material.AIR)
		return;

	    // Item frame doesn't contain creative items
	    if (!DataHandler.hasTrackedItem(frame))
		return;

	    DataHandler.removeItem(frame);
	    return;
	}

	// PVP and PVE
	if (e.getDamager() instanceof Player) {
	    Player p = (Player) e.getDamager();

	    // No need to control non-creative players
	    if (p.getGameMode() != GameMode.CREATIVE)
		return;

	    // PVE
	    if (en instanceof LivingEntity) {
		// No need to control disabled features
		if (!getMain().getSettings().isEnabled("limit.combat.pve"))
		    return;

		// No need to control bypassed players
		if (p.hasPermission("rc.bypass.limit.combat.pve")
			|| p.hasPermission("rc.bypass.limit.compat.pve." + en.getType()))
		    return;

		e.setCancelled(true);
		getMain().getUtils().sendMessage(p, true, "disabled.general");
		return;
	    }

	    // PVP
	    else if (en instanceof Player) {
		// No need to control disabled features
		if (!getMain().getSettings().isEnabled("limit.combat.pvp"))
		    return;

		// No need to control bypassed players
		if (p.hasPermission("rc.bypass.limit.combat.pvp"))
		    return;

		e.setCancelled(true);
		getMain().getUtils().sendMessage(p, true, "disabled.general");
		return;
	    }
	}

	// No need to control non-tracked entities
	if (!DataHandler.isTracked(en))
	    return;

	// Remove armor stands etc.
	en.remove();
	e.setCancelled(true);
    }

    /*
     * Raised when a vehicle is destroyed, which could be caused by either a player
     * or the environment. This is not raised if the boat is simply 'removed' due to
     * other means.
     */
    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestory(VehicleDestroyEvent e) {
	Entity en = e.getVehicle();

	// No need to control entities in disabled worlds
	if (getMain().getUtils().isDisabledWorld(en.getWorld().getName()))
	    return;

	// No need to control non-tracked entities
	if (!DataHandler.isTracked(en))
	    return;

	// Remove boats, carts etc.
	en.remove();
	e.setCancelled(true);
    }

    /*
     * Triggered when a hanging entity is removed
     */
    // HIGHEST required for WorldGuard and similar plugins
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHangingBreak(HangingBreakEvent e) {
	Entity en = e.getEntity();

	// No need to control entities in disabled worlds
	if (getMain().getUtils().isDisabledWorld(en.getWorld().getName()))
	    return;

	if (!DataHandler.isTracked(en)) {
	    if (en instanceof ItemFrame) {
		if (DataHandler.hasTrackedItem((ItemFrame) en))
		    en.remove();
	    }

	    return;
	}

	en.remove();
	e.setCancelled(true);
    }
}