package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Jukebox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.EmirEnchants;
import xyz.emirdev.emirenchants.Utils;

import java.util.*;

@SuppressWarnings("all")
public class DrillEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("drill");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.PICKAXES.key());

    private static List<Player> ignoredEventPlayers = new ArrayList<>();

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Drill"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .anvilCost(4)
                .maxLevel(1)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (!player.getInventory().getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;
        if (ignoredEventPlayers.contains(player)) return;

        BlockFace blockFace = event.getPlayer().rayTraceBlocks(10).getHitBlockFace();
        if (blockFace == null) return;

        Location corner1 = null, corner2 = null;

        switch (event.getPlayer().rayTraceBlocks(10).getHitBlockFace()) {
            case DOWN, UP -> {
                corner1 = event.getBlock().getLocation().add(1, 0, 1);
                corner2 = event.getBlock().getLocation().add(-1, 0, -1);
                break;
            }
            case WEST, EAST -> {
                corner1 = event.getBlock().getLocation().add(0, 1, 1);
                corner2 = event.getBlock().getLocation().add(0, -1, -1);
                break;
            }
            case SOUTH, NORTH -> {
                corner1 = event.getBlock().getLocation().add(1, 1, 0);
                corner2 = event.getBlock().getLocation().add(-1, -1, 0);
                break;
            }
        }

        if (corner1 == null || corner2 == null) return;

        ignoredEventPlayers.add(player);

        Material blockType = event.getBlock().getType();

        int x1 = corner1.getBlockX(), y1 = corner1.getBlockY(), z1 = corner1.getBlockZ();
        int x2 = corner2.getBlockX(), y2 = corner2.getBlockY(), z2 = corner2.getBlockZ();
        int lowestX = Math.min(x1, x2);
        int lowestY = Math.min(y1, y2);
        int lowestZ = Math.min(z1, z2);
        int highestX = lowestX == x1 ? x2 : x1;
        int highestY = lowestX == y1 ? y2 : y1;
        int highestZ = lowestX == z1 ? z2 : z1;
        for (int x = lowestX; x <= highestX; x++) {
            for (int y = lowestY; y <= highestY; y++) {
                for (int z = lowestZ; z <= highestZ; z++) {
                    Block block = event.getBlock().getWorld().getBlockAt(x, y, z);
                    if (block.getType() != blockType) continue;
                    event.getPlayer().breakBlock(block);
                }
            }
        }

        Bukkit.getServer().getScheduler().runTaskLater(EmirEnchants.getInstance(), () -> {
            ignoredEventPlayers.remove(player);
        }, 1);

    }
}
