package xyz.emirdev.emirenchants.enchantments;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
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
public class HealthEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("health");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.ENCHANTABLE_ARMOR.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Health"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(4)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .anvilCost(4)
                .maxLevel(3)
                .activeSlots(EquipmentSlotGroup.ARMOR);
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack prevItem = event.getOldItem();
        ItemStack newItem = event.getNewItem();

        switch (event.getSlotType()) {
            case HEAD -> checkArmor(player, "health_head", prevItem, newItem);
            case CHEST -> checkArmor(player, "health_chest", prevItem, newItem);
            case LEGS -> checkArmor(player, "health_legs", prevItem, newItem);
            case FEET -> checkArmor(player, "health_feet", prevItem, newItem);
        }

    }

    public void checkArmor(Player player, String mKey, ItemStack prevItem, ItemStack newItem) {
        NamespacedKey modifierKey = new NamespacedKey("emirenchants", mKey);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

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
}
