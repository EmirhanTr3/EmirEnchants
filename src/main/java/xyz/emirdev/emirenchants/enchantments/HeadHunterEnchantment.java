package xyz.emirdev.emirenchants.enchantments;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.emirdev.emirenchants.CustomEnchantment;
import xyz.emirdev.emirenchants.Utils;

import java.util.Random;

@SuppressWarnings("all")
public class HeadHunterEnchantment extends CustomEnchantment implements Listener {
    public static TypedKey<Enchantment> key = Utils.createEnchantmentKey("head_hunter");
    private static TagKey<ItemType> items = TagKey.create(RegistryKey.ITEM, ItemTypeTagKeys.SWORDS.key());

    public static void builder(RegistryFreezeEvent event, EnchantmentRegistryEntry.Builder builder) {
        builder
                .description(Utils.deserialize("Head Hunter"))
                .supportedItems(event.getOrCreateTag(items))
                .primaryItems(event.getOrCreateTag(items))
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(10, 5))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 5))
                .anvilCost(4)
                .maxLevel(4)
                .activeSlots(EquipmentSlotGroup.MAINHAND);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getDamageSource().getCausingEntity() instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity) event.getDamageSource().getCausingEntity();
        LivingEntity victim = (LivingEntity) event.getEntity();
        if (!attacker.getEquipment().getItemInMainHand().containsEnchantment(Registry.ENCHANTMENT.get(key))) return;

        int enchantLevel = attacker.getEquipment().getItemInMainHand().getEnchantmentLevel(Registry.ENCHANTMENT.get(key));

        int randomNumber = Utils.getRandomNumber(0, 100);

        if (randomNumber <= 10 * enchantLevel) {
            ItemStack head = switch (victim.getType()) {
                case ZOMBIE -> new ItemStack(Material.ZOMBIE_HEAD);
                case SKELETON -> new ItemStack(Material.SKELETON_SKULL);
                case WITHER_SKELETON -> new ItemStack(Material.WITHER_SKELETON_SKULL);
                case CREEPER -> new ItemStack(Material.CREEPER_HEAD);
                case PIGLIN -> new ItemStack(Material.PIGLIN_HEAD);
                case ENDER_DRAGON -> new ItemStack(Material.DRAGON_HEAD);
                case PLAYER -> {
                    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
                    skullMeta.setOwningPlayer((Player) victim);
                    playerHead.setItemMeta(skullMeta);
                    yield playerHead;
                }

                default -> null;
            };

            if (head == null) return;

            event.getDrops().add(head);
        }

    }
}