package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.Utils;

@SuppressWarnings("all")
public class LevitationEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("levitation");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.ENCHANTABLE_WEAPON.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Levitation"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(10, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                .anvilCost(4)
                .maxLevel(3)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity) event.getDamager();
        LivingEntity victim = (LivingEntity) event.getEntity();
        if (!attacker.getEquipment().getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;

        int enchantLevel = attacker.getEquipment().getItemInMainHand().getEnchantmentLevel(Registry.ENCHANTMENT.get(key));

        if (victim.hasPotionEffect(PotionEffectType.LEVITATION)) {
            victim.removePotionEffect(PotionEffectType.LEVITATION);
        }

        victim.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.LEVITATION,
                        60,
                        enchantLevel - 1
                )
        );
    }
}