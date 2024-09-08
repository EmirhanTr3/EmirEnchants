package xyz.emirdev.emirenchants;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.security.SecureRandom;
import java.util.*;

public class Utils {
    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static TypedKey<Enchantment> createEnchantmentKey(String key) {
        return TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("emirenchants", key));
    }

    public static void giveOrDrop(Player player, Block block, Collection<ItemStack> items) {
        for (ItemStack item : items) {
            giveOrDrop(player, block, item);
        }
    }

    public static void giveOrDrop(Player player, Block block, ItemStack item) {
        PlayerInventory inventory = player.getInventory();

        long inventoryUsedSlotCount = Arrays.stream(inventory.getContents()).filter(Objects::nonNull).count();
        long armorUsedSlotCount = Arrays.stream(inventory.getArmorContents()).filter(Objects::nonNull).count();

        if ((inventoryUsedSlotCount - armorUsedSlotCount - (inventory.getItemInOffHand().getType() != Material.AIR ? 1 : 0)) < 36) {
            inventory.addItem(item);
        } else {
            block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), item);
        }
    }

    public static int getRandomNumber(int min, int max) {
        return new SecureRandom().nextInt(max - min + 1) + min;
    }

    public static List<Location> getLocationsBetween(Location loc1, Location loc2) {
        List<Location> locations = new ArrayList<>();

        int x1 = loc1.getBlockX(), y1 = loc1.getBlockY(), z1 = loc1.getBlockZ();
        int x2 = loc2.getBlockX(), y2 = loc2.getBlockY(), z2 = loc2.getBlockZ();
        int lowestX = Math.min(x1, x2);
        int lowestY = Math.min(y1, y2);
        int lowestZ = Math.min(z1, z2);
        int highestX = lowestX == x1 ? x2 : x1;
        int highestY = lowestX == y1 ? y2 : y1;
        int highestZ = lowestX == z1 ? z2 : z1;
        for (int x = lowestX; x <= highestX; x++) {
            for (int y = lowestY; y <= highestY; y++) {
                for (int z = lowestZ; z <= highestZ; z++) {
                    locations.add(new Location(loc1.getWorld(), x, y, z));
                }
            }
        }

        return locations;
    }

    public static Vector getLocationDifference(Location loc1, Location loc2) {
        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.max(loc1.getX(), loc2.getX());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        double diffX = maxX - minX;
        double diffY = maxY - minY;
        double diffZ = maxZ - minZ;

        return new Vector(diffX, diffY, diffZ);
    }
}