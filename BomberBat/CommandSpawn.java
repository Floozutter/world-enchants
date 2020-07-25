import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import java.util.function.Consumer;

public class CommandSpawn implements CommandExecutor {
	private Consumer<Location> spawner;
	
	public CommandSpawn(Consumer<Location> spawner) {
		this.spawner = spawner;
	}
	
	@Override
	public boolean onCommand(
		CommandSender sender,
		Command command,
		String label,
		String[] args
	) {
		if (sender instanceof Player) {
			sender.sendMessage("Attempting to spawn a BomberBat...");
			spawner.accept(
				((Player) sender).getLocation()
			);
		} else {
			sender.sendMessage("Not a Player!");
		}
		return true;
	}
}
