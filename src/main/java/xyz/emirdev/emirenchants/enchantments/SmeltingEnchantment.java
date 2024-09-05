package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.*;
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
import xyz.emirdev.emirenchants.Utils;

import java.util.Collection;

@SuppressWarnings("all")
public class SmeltingEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("smelting");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.PICKAXES.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Smelting"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .anvilCost(4)
                .maxLevel(1)
                .activeSlots(EquipmentSlotGroup.MAINHAND)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TelekinesisEnchantment.key));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        PlayerInventory inventory = player.getInventory();
        if (!inventory.getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;
        if (inventory.getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) return;

        Collection<ItemStack> drops = event.getBlock().getDrops(inventory.getItemInMainHand(), player);
        event.setDropItems(false);

        for (ItemStack drop : drops) {
            ItemStack item =
                switch (drop.getType()) {
                    case RAW_IRON -> new ItemStack(Material.IRON_INGOT, drop.getAmount());
                    case RAW_GOLD -> new ItemStack(Material.GOLD_INGOT, drop.getAmount());
                    case RAW_COPPER -> new ItemStack(Material.COPPER_INGOT, drop.getAmount());
                    case ANCIENT_DEBRIS -> new ItemStack(Material.NETHERITE_SCRAP, drop.getAmount());
                    default -> drop;
                };

            event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), item);
        }

    }
}