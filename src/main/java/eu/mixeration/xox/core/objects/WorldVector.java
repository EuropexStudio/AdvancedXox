

package eu.mixeration.xox.core.objects;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Objects;

@Data
public class WorldVector {
    private final String worldName;
    private final Vector coords;

    public WorldVector(Location location) {
        this.worldName = Objects.requireNonNull(location.getWorld()).getName();
        this.coords = location.toVector();
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        return new Location(world, coords.getX(), coords.getY(), coords.getZ());
    }

    public Location getBlockLocation() {
        Location location = getLocation();

        if (location == null) {
            return null;
        }

        return location.getBlock().getLocation();
    }

    public WorldVector getBlockLocationVector() {
        Location blockLocation = getBlockLocation();

        if (blockLocation != null) {
            return new WorldVector(blockLocation);
        } else {
            return null;
        }
    }
}