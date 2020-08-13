import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public final class TNTTrailPlugin extends JavaPlugin {
	private final int TICKS_PER_SPAWN = 200;
	private boolean on = false;
	
	@Override
	public void onEnable() {
		getCommand("toggle").setExecutor(new CommandToggle(this));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(
			this,
			() -> {
				if (this.on) {
					Bukkit.getOnlinePlayers().forEach(player -> {
						player.getWorld().spawn(
							player.getLocation(),
							TNTPrimed.class
						);
					});
				}
			},
			0,
			TICKS_PER_SPAWN
		);
	}
	
	public boolean toggle() {
		this.on = !this.on;
		return this.on;
	}
	
	static private class CommandToggle implements CommandExecutor {
		private final TNTTrailPlugin plugin;
		public CommandToggle(TNTTrailPlugin plugin) {
			this.plugin = plugin;
		}
		@Override
		public boolean onCommand(
			CommandSender sender,
			Command command,
			String label,
			String[] args
		) {
			if (plugin.toggle()) {
				Bukkit.broadcastMessage("TNTTrail toggled on!");
			} else {
				Bukkit.broadcastMessage("TNTTrail toggled off!");
			}
			return true;
		}
	}
}
