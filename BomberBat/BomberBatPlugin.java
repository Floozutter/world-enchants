import net.minecraft.server.v1_16_R1.MinecraftKey;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.server.v1_16_R1.DataConverterRegistry;
import net.minecraft.server.v1_16_R1.DataConverterTypes;
import net.minecraft.server.v1_16_R1.SharedConstants;
import java.util.Map;

import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EnumCreatureType;
import net.minecraft.server.v1_16_R1.IRegistry;

import net.minecraft.server.v1_16_R1.BlockPosition;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;


public final class BomberBatPlugin extends JavaPlugin {
	private static final MinecraftKey KEY = MinecraftKey.a("bomber_bat");
	public static EntityTypes<EntityBomberBat> BOMBER_BAT;
	
	@Override
	@SuppressWarnings("unchecked")
	public void onLoad() {
		Map<Object, Type<?>> dataFixers = (Map<Object, Type<?>>) (
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
		dataFixers.put(
			KEY.toString(),
			dataFixers.get("minecraft:phantom")
		);
		
		BOMBER_BAT = EntityTypes.Builder.a(
			EntityBomberBat::new,
			EnumCreatureType.MONSTER
		).a(KEY.getKey());
		IRegistry.a(IRegistry.ENTITY_TYPE, KEY.getKey(), BOMBER_BAT);
	}

	@Override
	public void onEnable() {
		getCommand("spawnbomberbat").setExecutor(
			new CommandSpawn(BomberBatPlugin::spawn)
		);
	}

	public static EntityBomberBat spawn(Location location) {
		return (EntityBomberBat) BOMBER_BAT.spawnCreature(
			((CraftWorld) location.getWorld()).getHandle(),
			null,
			null,
			null,
			new BlockPosition(
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ()
			),
			null,
			true,
			false
		);
	}
}
