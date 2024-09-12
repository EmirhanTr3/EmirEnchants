package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.EmirEnchants;
import xyz.emirdev.emirenchants.Utils;

@SuppressWarnings("all")
public class BlockReachEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("block_reach");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.ENCHANTABLE_MINING.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Block Reach"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(4)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .anvilCost(4)
                .maxLevel(3)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

    @EventHandler
    public void onToolChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack prevItem = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        NamespacedKey modifierKey = new NamespacedKey("emirenchants", "block_reach");
        AttributeInstance attribute = player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE);

        if (prevItem != null && prevItem.containsEnchantment(Registry.ENCHANTMENT.get(key))) {
            AttributeModifier modifier = new AttributeModifier(
                    modifierKey,
                    prevItem.getEnchantmentLevel(Registry.ENCHANTMENT.get(key)),
                    AttributeModifier.Operation.ADD_NUMBER
            );
            attribute.removeModifier(modifier);
        }

        if (newItem != null && newItem.containsEnchantment(Registry.ENCHANTMENT.get(key))) {
            if (attribute.getModifier(modifierKey) == null) {
                AttributeModifier modifier = new AttributeModifier(
                        modifierKey,
                        newItem.getEnchantmentLevel(Registry.ENCHANTMENT.get(key)),
                        AttributeModifier.Operation.ADD_NUMBER
                );
                attribute.addModifier(modifier);
            }
        }

    }

    public static void run() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(EmirEnchants.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                checkItem(player, player.getEquipment().getItemInMainHand());
            }
        }, 0, 5);
    }

    public static void checkItem(Player player, ItemStack item) {
        NamespacedKey modifierKey = new NamespacedKey("emirenchants", "block_reach");
        AttributeInstance attribute = player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE);
        AttributeModifier modifier = new AttributeModifier(
                modifierKey,
                item.getEnchantmentLevel(Registry.ENCHANTMENT.get(key)),
                AttributeModifier.Operation.ADD_NUMBER
        );

        if (item != null && item.containsEnchantment(Registry.ENCHANTMENT.get(key))) {
            if (attribute.getModifier(modifierKey) == null) {
                attribute.addModifier(modifier);
            }
        } else if (attribute.getModifier(modifierKey) != null) {
            attribute.removeModifier(modifier);
        }
    }
}
