package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Container;
import org.bukkit.block.Jukebox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.*;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.Utils;
import xyz.emirdev.emirenchants.tags.ToolsTag;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("all")
public class TelekinesisEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("telekinesis");
    private static TagKey<ItemType> items = ToolsTag.key;

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
                .activeSlots(EquipmentSlotGroup.MAINHAND)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, SmeltingEnchantment.key));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isDropItems()) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        PlayerInventory inventory = player.getInventory();
        if (!inventory.getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;

        Collection<ItemStack> drops = event.getBlock().getDrops(inventory.getItemInMainHand(), player);
        event.setDropItems(false);

        for (ItemStack drop : drops) {
            Utils.giveOrDrop(player, event.getBlock(), drop);
        }

        if (event.getBlock().getState() instanceof Container container) {
            Inventory blockInventory = container.getInventory();
            List<ItemStack> blockInventoryContents = Arrays.stream(blockInventory.getContents()).filter(item -> item != null).toList();

            for (ItemStack item : blockInventoryContents) {
                Utils.giveOrDrop(player, event.getBlock(), item);
            }
        }

        if (event.getBlock().getState() instanceof Jukebox jukebox) {
            if (jukebox.getRecord().getType() != Material.AIR) {
                Utils.giveOrDrop(player, event.getBlock(), jukebox.getRecord());
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        List<ItemStack> drops = event.getDrops();

        if (event.getDamageSource().getCausingEntity() instanceof Player attacker) {
            for (ItemStack drop : drops) {
                Utils.giveOrDrop(attacker, attacker.getLocation(), drop);
            }

            drops.clear();
        }

    }
}
