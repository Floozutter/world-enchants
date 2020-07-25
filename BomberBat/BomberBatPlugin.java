import com.mojang.datafixers.types.Type;
 import com.mojang.datafixers.DataFixUtils;
import net.minecraft.server.v1_16_R1.DataConverterRegistry;
import net.minecraft.server.v1_16_R1.DataConverterTypes;
import net.minecraft.server.v1_16_R1.SharedConstants;
import java.util.Map;

import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EnumCreatureType;

import net.minecraft.server.v1_16_R1.EnumMobSpawn;
import net.minecraft.server.v1_16_R1.World;
import net.minecraft.server.v1_16_R1.BlockPosition;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.Location;

import org.bukkit.plugin.java.JavaPlugin;


public final class BomberBatPlugin extends JavaPlugin {
	private static final String NAME = "bomber_bat";
	public static EntityTypes<EntityBomberBat> BOMBER_BAT;
	
	@Override
	public void onLoad() {
		Map<Object, Type<?>> dataFixers = ((Map<Object, Type<?>>)
			DataConverterRegistry
			.a()
			.getSchema(
				DataFixUtils.makeKey(
					SharedConstants
					.getGameVersion()
					.getWorldVersion()
				)
			)
			.findChoiceType(
				DataConverterTypes.ENTITY
			)
			.types()
		);
		dataFixers.put("minecraft:"+NAME, dataFixers.get("minecraft:phantom"));
		
		
		BOMBER_BAT = EntityTypes.Builder.a(
			EntityBomberBat::new,
			EnumCreatureType.MONSTER
		).a(NAME);
	}

	@Override
	public void onEnable() {
		getCommand("spawnbomberbat").setExecutor(new CommandSpawn(BomberBatPlugin::spawn));
	}

	private static EntityBomberBat spawn(Location location) {
		return BOMBER_BAT.spawnCreature(
			((CraftWorld) location.getWorld()).getHandle(),
			null,
			null,
			null,
			new BlockPosition(
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ()
			),
			EnumMobSpawn.COMMAND,
			true,
			false
		);
	}
}
