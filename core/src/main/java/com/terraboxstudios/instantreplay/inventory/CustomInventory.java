package com.terraboxstudios.instantreplay.inventory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class CustomInventory {

	private final ItemStack[] contents, armourContents, hands;
	private final int health;

}
