package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.EmirEnchants;
import xyz.emirdev.emirenchants.Utils;


@SuppressWarnings("all")
public class ReachEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("emirenchants", "reach"));
    private static TagKey<ItemType> pickaxesTag = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.PICKAXES.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Reach"))
                .supportedItems(event.getOrCreateTag(pickaxesTag))
                .primaryItems(event.getOrCreateTag(pickaxesTag))
                .weight(100)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .anvilCost(4)
                .maxLevel(3)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        EmirEnchants.getPluginLogger().info(event.getPlayer().getName());
//    }
}
