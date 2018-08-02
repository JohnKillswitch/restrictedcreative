package me.prunt.restrictedcreative.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import me.prunt.restrictedcreative.Main;

public class WEListener {
    private Main main;

    public WEListener(Main main) {
	this.main = main;
    }

    @Subscribe
    public void wrapForLogging(EditSessionEvent e) {
	Actor a = e.getActor();

	if (a == null || !a.isPlayer())
	    return;

	Player p = Bukkit.getServer().getPlayer(a.getUniqueId());

	if (main.getUtils().isDisabledWorld(p.getWorld().getName()))
	    return;

	e.setExtent(new WELogger(main, p, e.getExtent()));
    }
}
