package com.terraboxstudios.replay.obj;

import org.bukkit.inventory.ItemStack;

public class CustomInventory {

	private final ItemStack[] contents, armourContents, health;
	private final int heldSlot;
	
	public CustomInventory(ItemStack[] contents, ItemStack[] armourContents, ItemStack[] health, int heldSlot) {
		this.contents = contents;
		this.armourContents = armourContents;
		this.health = health;
		this.heldSlot = heldSlot;
	}

	public int getHeldSlot() {
		return heldSlot;
	}
	
	public ItemStack[] getHealth() {
		return health;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public ItemStack[] getArmourContents() {
		return armourContents;
	}
	
}
