package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.PlayerInventory;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.EmirEnchants;
import xyz.emirdev.emirenchants.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("all")
public class HarvestingEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("harvesting");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.HOES.key());

    private static List<Player> ignoredEventPlayers = new ArrayList<>();

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Harvesting"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(10, 4))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 4))
                .anvilCost(4)
                .maxLevel(5)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (!inventory.getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;
        if (ignoredEventPlayers.contains(player)) return;

        int enchantLevel = inventory.getItemInMainHand().getEnchantmentLevel(Registry.ENCHANTMENT.get(key));

        if (!(event.getBlock().getBlockData() instanceof Ageable)) return;
        event.setCancelled(true);

        Location loc1 = event.getBlock().getLocation().add(enchantLevel, 0, enchantLevel);
        Location loc2 = event.getBlock().getLocation().add(-enchantLevel, 0, -enchantLevel);

        ignoredEventPlayers.add(player);
        Material blockType = event.getBlock().getType();

        for (Location location : Utils.getLocationsBetween(loc1, loc2)) {
            Block block = event.getBlock().getWorld().getBlockAt(location);
            if (!(block.getBlockData() instanceof Ageable) || block.getType() != blockType) continue;
            event.getPlayer().breakBlock(block);
        }

        Bukkit.getServer().getScheduler().runTaskLater(EmirEnchants.getInstance(), () -> {
            ignoredEventPlayers.remove(player);
        }, 1);
    }
}
