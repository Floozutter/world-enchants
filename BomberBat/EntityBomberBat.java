import org.bukkit.Bukkit;
import net.minecraft.server.v1_16_R1.EntityBat;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.World;

public class EntityBomberBat extends EntityBat {
	public EntityBomberBat(
		EntityTypes<? extends EntityBomberBat> entitytypes,
		World world
	) {
		super(EntityTypes.BAT, world);
		Bukkit.broadcastMessage("Bomber Bat!");
	}
}
