package me.sciguymjm.uberenchant;

import org.bukkit.enchantments.Enchantment;

public enum Enchants {

	Flame(Enchantment.ARROW_FIRE, "flame", 1),
	Power(Enchantment.ARROW_DAMAGE, "power", 2),
	Punch(Enchantment.ARROW_KNOCKBACK, "punch", 3),
	Smite(Enchantment.DAMAGE_UNDEAD, "smite", 4),
	Thorns(Enchantment.THORNS, "thorns", 5),
	Fortune(Enchantment.LOOT_BONUS_BLOCKS, "fortune", 6),
	Looting(Enchantment.LOOT_BONUS_MOBS, "looting", 7),
	Infinity(Enchantment.ARROW_INFINITE, "infinity", 8),
	Knockback(Enchantment.KNOCKBACK, "knockback", 9),
	Sharpness(Enchantment.DAMAGE_ALL, "sharpness", 10),
	SilkTouch(Enchantment.SILK_TOUCH, "silktouch", 11),
	FireAspect(Enchantment.FIRE_ASPECT, "fireaspect", 12),
	Protection(Enchantment.PROTECTION_ENVIRONMENTAL, "protection", 13),
	Unbreaking(Enchantment.DURABILITY, "unbreaking", 14),
	Efficiency(Enchantment.DIG_SPEED, "efficiency", 15),
	Respiration(Enchantment.OXYGEN, "respiration", 16),
	AquaAffinity(Enchantment.WATER_WORKER, "affinity", 17),
	FeatherFalling(Enchantment.PROTECTION_FALL, "featherfalling", 18),
	FireProtection(Enchantment.PROTECTION_FIRE, "fireprotection", 19),
	BlastProtection(Enchantment.PROTECTION_EXPLOSIONS, "blastprotection", 20),
	BaneOfArthropods(Enchantment.DAMAGE_ARTHROPODS, "baneofarthropods", 21),
	ProjectileProtection(Enchantment.PROTECTION_PROJECTILE, "projectileprotection", 22),
	Luck(Enchantment.LUCK, "luck", 23),
	Lure(Enchantment.LURE, "lure", 24);
	
	Enchants(Enchantment enchantment, String name, int id) {
		this.enchantment = enchantment;
		this.name = name;
		this.id = id;
	}
	
	Enchantment getEnchant() {
		return this.enchantment;
	}
	
	String getName() {
		return this.name;
	}
	
	int getId() {
		return this.id;
	}
	
	private Enchantment enchantment;
	private String name;
	private int id;
}
