package bomberbat;
import bomberbat.Swellable;

import net.minecraft.server.v1_16_R1.PathfinderGoal;
import net.minecraft.server.v1_16_R1.EntityLiving;
import java.util.EnumSet;


public class PathfinderGoalSwellCustom extends PathfinderGoal {
	private final Swellable swellable;
	private EntityLiving target;

	public PathfinderGoalSwellCustom(Swellable swellable) {
		this.swellable = swellable;
		this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
	}

	@Override
	public boolean a() {  // canUse
		EntityLiving entityliving = this.swellable.getGoalTarget();
		return (this.swellable.getSwellDir() > 0) || (
			(entityliving != null) && (this.swellable.h(entityliving) < 9.0D)
		);
	}

	@Override
	public void c() {  // start
		this.swellable.getNavigation().o();
		this.target = this.swellable.getGoalTarget();
	}

	@Override
	public void d() {  // stop
		this.target = null;
	}

	@Override
	public void e() {  // tick
		if (this.target == null) {
			this.swellable.setSwellDir(-1);
		} else if (this.swellable.h(this.target) > 49.0D) {
			this.swellable.setSwellDir(-1);
		} else if (!this.swellable.getEntitySenses().a(this.target)) {
			this.swellable.setSwellDir(-1);
		} else {
			this.swellable.setSwellDir(1);
		}
	}
}
