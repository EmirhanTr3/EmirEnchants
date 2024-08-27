package xyz.emirdev.emirenchants;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("all")
public class Bootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        TypedKey<Enchantment> reachEnchant = TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("emirenchants", "reach"));
        TagKey<ItemType> enchantableMiningTag = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.PICKAXES.key());

        final LifecycleEventManager<BootstrapContext> manager = context.getLifecycleManager();
        manager.registerEventHandler(RegistryEvents.ENCHANTMENT.freeze(), event -> {
            event.registry().register(reachEnchant, b -> b
                    .description(text("Reach"))
                    .supportedItems(event.getOrCreateTag(enchantableMiningTag))
                    .primaryItems(event.getOrCreateTag(enchantableMiningTag))
                    .weight(100)
                    .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                    .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                    .anvilCost(4)
                    .maxLevel(3)
                    .activeSlots(EquipmentSlotGroup.MAINHAND)
            );
        });
        context.getLogger().info("Registered reach enchantment.");
    }
}
