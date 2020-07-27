package bomberbat;

import net.minecraft.server.v1_16_R1.NavigationAbstract;
import net.minecraft.server.v1_16_R1.EntitySenses;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.Entity;


public interface Swellable {
	EntityLiving getGoalTarget();
	NavigationAbstract getNavigation();
	EntitySenses getEntitySenses();
	double h(Entity entity);
	
	void setSwellDir(int i);
	int getSwellDir();
}
