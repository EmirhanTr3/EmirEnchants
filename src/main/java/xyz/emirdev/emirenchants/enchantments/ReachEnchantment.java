package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import xyz.emirdev.emirenchants.CustomEnchantment;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("all")
public class ReachEnchantment extends CustomEnchantment {
    public static TypedKey<Enchantment> key = TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("emirenchants", "reach"));
    private static TagKey<ItemType> pickaxesTag = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.PICKAXES.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(text("Reach"))
                .supportedItems(event.getOrCreateTag(pickaxesTag))
                .primaryItems(event.getOrCreateTag(pickaxesTag))
                .weight(100)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .anvilCost(4)
                .maxLevel(3)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }
}
