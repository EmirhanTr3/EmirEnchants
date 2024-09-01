package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Jukebox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("all")
public class TelekinesisEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("telekinesis");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.ENCHANTABLE_MINING.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Telekinesis"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(20, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .anvilCost(4)
                .maxLevel(1)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        PlayerInventory inventory = player.getInventory();
        if (!inventory.getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;

        Collection<ItemStack> drops = event.getBlock().getDrops(inventory.getItemInMainHand(), player);
        event.setDropItems(false);

        for (ItemStack drop : drops) {
            giveOrDrop(player, event.getBlock(), drop);
        }

        if (event.getBlock().getState() instanceof Container container) {
            Inventory blockInventory = container.getInventory();
            List<ItemStack> blockInventoryContents = Arrays.stream(blockInventory.getContents()).filter(item -> item != null).toList();

            for (ItemStack item : blockInventoryContents) {
                giveOrDrop(player, event.getBlock(), item);
            }
        }

        if (event.getBlock().getState() instanceof Jukebox jukebox) {
            if (jukebox.getRecord().getType() != Material.AIR) {
                giveOrDrop(player, event.getBlock(), jukebox.getRecord());
            }
        }
    }

    private void giveOrDrop(Player player, Block block, ItemStack item) {
        PlayerInventory inventory = player.getInventory();

        long inventoryUsedSlotCount = Arrays.stream(inventory.getContents()).filter(item1 -> item1 != null).count();
        long armorUsedSlotCount = Arrays.stream(inventory.getArmorContents()).filter(item1 -> item1 != null).count();

        if ((inventoryUsedSlotCount - armorUsedSlotCount - (inventory.getItemInOffHand().getType() != Material.AIR ? 1 : 0)) < 36) {
            inventory.addItem(item);
        } else {
            block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), item);
        }
    }
}
