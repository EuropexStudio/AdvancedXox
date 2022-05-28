

package eu.mixeration.xox.core.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.Collection;
import java.util.Objects;

@Data
@AllArgsConstructor
public class BoardItem {
    private WorldVector location;
    private Board board;
    private BoardPosition position;

    public ItemFrame getItemFrame() {
        if (location == null || location.getWorldName() == null || location.getCoords() == null) {
            return null;
        }

        Location location = this.location.getLocation();

        if (location == null) {
            return null;
        }

        Collection<Entity> entityList = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, 0, 0, 0, entity -> entity instanceof ItemFrame);

        if (entityList.size() != 1) {
            return null;
        }

        return (ItemFrame) entityList.iterator().next();
    }
}
