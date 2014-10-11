package zotmc.chikuwabu;

import static cpw.mods.fml.common.eventhandler.EventPriority.LOW;
import static zotmc.chikuwabu.data.ModData.Chikuwabus.MC_STRING;
import static zotmc.chikuwabu.data.ModData.Chikuwabus.MODID;
import static zotmc.chikuwabu.data.ModData.Chikuwabus.NAME;
import static zotmc.chikuwabu.data.ModData.Chikuwabus.VERSION;

import java.util.Random;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;
import zotmc.chikuwabu.data.ModData;
import zotmc.chikuwabu.util.Utils;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.versioning.ArtifactVersion;

@Mod(modid = MODID, name = NAME, version = VERSION)
public class Chikuwabu {
	
	private final Random rand = new Random();
	private Item chikuwabu;
	
	@EventHandler public void onConstruct(FMLConstructionEvent event) {
		Set<ArtifactVersion> missing = Utils.checkRequirements(ModData.class, MC_STRING);
		if (!missing.isEmpty())
			throw new MissingModsException(missing);
	}
	
	@EventHandler public void preInit(FMLPreInitializationEvent event) {
		ModData.init(event.getModMetadata());
		
		chikuwabu = new ItemChikuwabu()
			.setAlwaysEdible()
			.setCreativeTab(CreativeTabs.tabFood)
			.setTextureName("chikuwabu:chikuwabu")
			.setUnlocalizedName("chikuwabu.chikuwabu");
		GameRegistry.registerItem(chikuwabu, "chikuwabu");
		OreDictionary.registerOre("chikuwabu", chikuwabu);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent(priority = LOW)
	public void onPlayerInteract(PlayerEvent.BreakSpeed event) {
		if (event.block != Blocks.cactus)
			return;
		int fortune = EnchantmentHelper.getFortuneModifier(event.entityPlayer);
		
		if (fortune > 0) {
			World world = event.entity.worldObj;
			event.setCanceled(true);
			
			float f = event.newSpeed;
			int attempts = (int) f;
			if (rand.nextFloat() < f - attempts)
				attempts++;
			
			
			if (!world.isRemote && attempts > 0) {
				event.entityPlayer.getHeldItem().attemptDamageItem(attempts, rand);
				int amount = attempts * Math.max(0, rand.nextInt(2 + fortune) - 1);
				
				if (rand.nextDouble() >= Math.pow(1 - 0.007, attempts)) {
					world.setBlockToAir(event.x, event.y, event.z);
					amount++;
				}
				
				if (amount > 0) {
					double x = event.x + 0.5, y = event.y + 0.5, z = event.z + 0.5;
					double dx = event.entityPlayer.posX - x, dz = event.entityPlayer.posZ - z;
					double r = Math.sqrt(dx * dx + dz * dz);
					if (r != 0) {
						x += dx / r;
						z += dz / r;
					}
					
					world.spawnEntityInWorld(new EntityItem(world, x, y, z, new ItemStack(chikuwabu, amount)));
				}
			}
		}
	}
	
}
