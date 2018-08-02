package me.prunt.restrictedcreative.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import me.prunt.restrictedcreative.Main;
import me.prunt.restrictedcreative.utils.Utils;

public class DataHandler {
    public static List<String> addToDatabase = new ArrayList<>();
    public static List<String> removeFromDatabase = new ArrayList<>();

    private static List<String> trackedLocs = new ArrayList<>();

    private static boolean usingOldAliases = false;
    private static boolean usingSQLite = false;

    private static List<Player> addWithCommand = new ArrayList<>();
    private static List<Player> removeWithCommand = new ArrayList<>();
    private static List<Player> infoWithCommand = new ArrayList<>();

    private static int totalCount = 0;

    public static boolean isTracked(Block b) {
	if (b == null)
	    return false;

	for (MetadataValue mdv : b.getMetadata("GMC")) {
	    if (mdv.getOwningPlugin() == Main.getInstance()) {
		if (mdv.asBoolean())
		    return true;
	    }
	}

	return false;
    }

    public static void setAsTracked(Block b) {
	if (b == null || isTracked(b))
	    return;

	b.setMetadata("GMC", Main.getFMV());
	addToDatabase.add(Utils.getBlockString(b));
	removeFromDatabase.remove(Utils.getBlockString(b));
    }

    public static void removeTracking(Block b) {
	if (b == null || !isTracked(b))
	    return;

	b.removeMetadata("GMC", Main.getInstance());
	addToDatabase.remove(Utils.getBlockString(b));
	removeFromDatabase.add(Utils.getBlockString(b));
    }

    public static void breakBlock(Block b, Player p) {
	breakBlock(b, p, false);
    }

    public static void breakBlock(Block b, Player p, boolean update) {
	// TODO update
	// BlocksHub
	// if (Utils.isInstalled("BlocksHub")) {
	// IBlocksHubApi blockshub = ((IBlocksHubApiProvider)
	// Bukkit.getServer().getPluginManager()
	// .getPlugin("BlocksHub")).getApi();
	//
	// Vector location = new Vector(b.getX(), b.getY(), b.getZ());
	// IPlayer player = p == null ? blockshub.getPlayer("RestrictedCreative")
	// : blockshub.getPlayer(p.getUniqueId());
	// IWorld world = blockshub.getWorld(b.getWorld().getUID());
	// BlockData oldBlock = new BlockData(b.getTypeId(), b.getData());
	// BlockData newBlock = BlockData.AIR;
	//
	// blockshub.logBlock(location, player, world, oldBlock, newBlock);
	// }

	b.setType(Material.AIR, update);
	removeTracking(b);
    }

    public static boolean isTracked(Entity e) {
	if (e == null)
	    return false;

	return e.getScoreboardTags().contains("GMC");
    }

    public static void setAsTracked(Entity e) {
	if (e == null)
	    return;

	e.addScoreboardTag("GMC");
    }

    public static void removeTracking(Entity e) {
	if (e == null)
	    return;

	e.removeScoreboardTag("GMC");
    }

    public static boolean hasTrackedItem(ItemFrame frame) {
	if (frame == null)
	    return false;

	return frame.getScoreboardTags().contains("GMC_IF");
    }

    public static void setAsTrackedItem(ItemFrame frame) {
	if (frame == null)
	    return;

	frame.addScoreboardTag("GMC_IF");
    }

    public static void removeItemTracking(ItemFrame frame) {
	if (frame == null)
	    return;

	frame.removeScoreboardTag("GMC_IF");
    }

    public static void removeItem(ItemFrame frame) {
	if (frame == null)
	    return;

	frame.setItem(new ItemStack(Material.AIR));
	removeItemTracking(frame);
    }

    public static boolean isTrackedSlot(ArmorStand stand, EquipmentSlot slot) {
	if (stand == null || slot == null)
	    return false;

	return stand.getScoreboardTags().contains("GMC_AS_" + slot);
    }

    public static void setAsTrackedSlot(ArmorStand stand, EquipmentSlot slot) {
	if (stand == null)
	    return;

	stand.addScoreboardTag("GMC_AS_" + slot);
    }

    public static void removeSlotTracking(ArmorStand stand, EquipmentSlot slot) {
	if (stand == null)
	    return;

	stand.removeScoreboardTag("GMC_AS_" + slot);
    }

    public static boolean isTrackedLoc(Location loc) {
	return trackedLocs.contains(Utils.getLocString(loc));
    }

    public static void addToTrackedLocs(Location loc) {
	trackedLocs.add(Utils.getLocString(loc));
    }

    public static void removeFromTrackedLocs(Location loc) {
	if (isTrackedLoc(loc))
	    trackedLocs.remove(Utils.getLocString(loc));
    }

    private static void setTotalCount(int totalCount) {
	DataHandler.totalCount = totalCount;
    }

    public static String getTotalCount() {
	return String.valueOf(totalCount);
    }

    public static boolean isUsingOldAliases() {
	return usingOldAliases;
    }

    public static void setUsingOldAliases(boolean usingOldAliases) {
	DataHandler.usingOldAliases = usingOldAliases;
    }

    public static boolean isUsingSQLite() {
	return usingSQLite;
    }

    public static void setUsingSQLite(boolean usingSQLite) {
	DataHandler.usingSQLite = usingSQLite;
    }

    public static List<Player> getAddWithCommand() {
	return addWithCommand;
    }

    public static List<Player> getRemoveWithCommand() {
	return removeWithCommand;
    }

    public static List<Player> getInfoWithCommand() {
	return infoWithCommand;
    }

    public static boolean isAddWithCommand(Player p) {
	return getAddWithCommand().contains(p);
    }

    public static boolean isRemoveWithCommand(Player p) {
	return getRemoveWithCommand().contains(p);
    }

    public static boolean isInfoWithCommand(Player p) {
	return getInfoWithCommand().contains(p);
    }

    public static void removeAddWithCommand(Player p) {
	if (isAddWithCommand(p))
	    getAddWithCommand().remove(p);
    }

    public static void removeRemoveWithCommand(Player p) {
	if (isRemoveWithCommand(p))
	    getRemoveWithCommand().remove(p);
    }

    public static void removeInfoWithCommand(Player p) {
	if (isInfoWithCommand(p))
	    getInfoWithCommand().remove(p);
    }

    public static void setAddWithCommand(Player p) {
	getAddWithCommand().add(p);
    }

    public static void setRemoveWithCommand(Player p) {
	getRemoveWithCommand().add(p);
    }

    public static void setInfoWithCommand(Player p) {
	getInfoWithCommand().add(p);
    }

    public static void loadFromDatabase(Main main) {
	Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		long start = System.currentTimeMillis();

		main.getUtils().sendMessage(Bukkit.getConsoleSender(), true, "database.load");

		// Gets all blocks from database
		ResultSet rs = main.getDB().executeQuery("SELECT * FROM " + main.getDB().getTableName());

		// Back to sync processing
		Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
		    @Override
		    public void run() {
			int count = 0;

			try {
			    while (rs.next()) {
				count++;

				String str = rs.getString("block");
				Block b = Utils.getBlock(str);

				if (b == null || b.isEmpty()) {
				    removeFromDatabase.add(Utils.getBlockString(b));
				} else {
				    setAsTracked(b);
				}
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}

			setTotalCount(count);

			Utils.sendMessage(Bukkit.getConsoleSender(), main.getUtils().getMessage(true, "database.loaded")
				.replaceAll("%blocks%", getTotalCount()));

			String took = String.valueOf(System.currentTimeMillis() - start);

			Utils.sendMessage(Bukkit.getConsoleSender(),
				main.getUtils().getMessage(true, "database.done").replaceAll("%mills%", took));
		    }
		});
	    }
	});
    }

    public static void startDataSync(Main main) {
	int interval = main.getSettings().getInt("general.saving.interval");

	Bukkit.getServer().getScheduler().runTaskTimer(main, new Runnable() {
	    @Override
	    public void run() {
		final List<String> fadd = new ArrayList<>(addToDatabase);
		final List<String> fdel = new ArrayList<>(removeFromDatabase);

		Bukkit.getScheduler().runTaskAsynchronously(main, new SyncData(main, fadd, fdel));
	    }
	}, interval, interval);
    }
}
