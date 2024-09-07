package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.EmirEnchants;
import xyz.emirdev.emirenchants.Utils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class LumberjackEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("lumberjack");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.AXES.key());

    private static List<Player> ignoredEventPlayers = new ArrayList<>();

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Lumberjack"))
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
        if (!player.getInventory().getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;
        if (ignoredEventPlayers.contains(player)) return;
        if (!event.getBlock().getType().toString().endsWith("LOG")) return;

        List<Block> tempList = new ArrayList<>();
        List<Block> blockList = new ArrayList<>();
        tempList.add(event.getBlock());
        blockList.add(event.getBlock());

        ignoredEventPlayers.add(player);

        while (!tempList.isEmpty()) {
            if (blockList.size() > 10) break;
            Block block = tempList.getFirst();
            for (BlockFace face : BlockFace.values()) {
                Block relative = block.getRelative(face);
                if (relative.getType() == event.getBlock().getType() && !blockList.contains(relative)) {
                    tempList.add(relative);
                    blockList.add(relative);
                }
            }
            tempList.removeFirst();
        }

        for (Block block : blockList) {
            event.getPlayer().breakBlock(block);
        }

        Bukkit.getServer().getScheduler().runTaskLater(EmirEnchants.getInstance(), () -> {
            ignoredEventPlayers.remove(player);
        }, 1);

    }
}
