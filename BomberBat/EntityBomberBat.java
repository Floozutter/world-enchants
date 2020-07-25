import net.minecraft.server.v1_16_R1.EntityPhantom;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.World;

public class EntityBomberBat extends EntityPhantom {
	public EntityBomberBat(
		EntityTypes<? extends EntityBomberBat> entitytypes,
		World world
	) {
		super(entitytypes, world);
	}
}
