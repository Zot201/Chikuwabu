package zotmc.chikuwabu;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class ItemChikuwabu extends ItemFood {
	
	public ItemChikuwabu() {
		super(1, 0.1F, false);
	}
	
	@Override public int getMaxItemUseDuration(ItemStack p_77626_1_) {
		return 8;
	}

}
