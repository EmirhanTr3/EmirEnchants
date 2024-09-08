package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.Utils;

import java.util.Collection;

@SuppressWarnings("all")
public class ReplantEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("replant");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.HOES.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Replant"))
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
        if (!player.getInventory().getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;

        if (event.getBlock().getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() >= ageable.getMaximumAge()) {
                Material seedMaterial = switch (event.getBlock().getType()) {
                    case WHEAT -> Material.WHEAT_SEEDS;
                    case CARROTS -> Material.CARROT;
                    case BEETROOTS -> Material.BEETROOT_SEEDS;
                    case POTATOES -> Material.POTATO;
                    case NETHER_WART -> Material.NETHER_WART;

                    default -> null;
                };
                if (seedMaterial == null) return;
                ItemStack seed = new ItemStack(seedMaterial);
                if (!player.getInventory().containsAtLeast(seed, 1)) return;

                player.getInventory().removeItem(seed);
                event.setCancelled(true);
                Collection<ItemStack> drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand(), player);

                for (ItemStack item : drops) {
                    event.getBlock().getWorld().dropItem(
                            event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                            item
                    );
                }

                event.getBlock().setType(event.getBlock().getType());
            }
        }
    }
}
