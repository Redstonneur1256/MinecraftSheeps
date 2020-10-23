package fr.redstonneur1256.minecraftsheeps;

import fr.redstonneur1256.redutilities.Pools;
import fr.redstonneur1256.redutilities.graphics.Palette;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Redstonneur1256
 * <p>
 * Simple class to render image using sheep colors
 */
public class SheepRenderer {

    private static Palette<Palette.Container<DyeColor>> palette;

    static {
        palette = new Palette<Palette.Container<DyeColor>>()
                .useCache(true);

        for(DyeColor dye : DyeColor.values()) {
            palette.addColor(new Palette.Container<>(dye, dye.getColor().asRGB()));
        }
    }

    private World world;
    private Rectangle bounds;

    public SheepRenderer(World world, int x, int z, int dx, int dz) {
        this.world = world;
        this.bounds = new Rectangle(x, z, dx, dz);
    }

    /**
     * Render the image on sheeps in the specified location
     *
     * @param image the image to draw
     */
    public void update(BufferedImage image) {
        List<SheepLocation> sheeps = world.getEntities().stream()
                .filter(entity -> entity.getType() == EntityType.SHEEP)
                .map(Sheep.class::cast)
                .map(this::toLocation)
                .filter(SheepLocation::isInBounds)
                .collect(Collectors.toList());

        for(SheepLocation sheep : sheeps) {
            int pixelX = (int) (sheep.getX() * image.getWidth());
            int pixelY = (int) (sheep.getY() * image.getHeight());

            int rgb = image.getRGB(pixelX, pixelY);

            sheep.getSheep().setColor(palette.matchColor(rgb).getValue());

            Pools.release(sheep);
        }
    }

    private SheepLocation toLocation(Sheep sheep) {
        Location location = sheep.getLocation();

        double x = (location.getX() - bounds.getX()) / bounds.getWidth();
        double y = (location.getZ() - bounds.getY()) / bounds.getHeight();

        SheepLocation sheepLocation = Pools.get(SheepLocation.class, SheepLocation::new);
        sheepLocation.set(sheep, x, y);
        return sheepLocation;
    }

    public static class SheepLocation {

        private double x, y;
        private Sheep sheep;

        public void set(Sheep sheep, double x, double y) {
            this.sheep = sheep;
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Sheep getSheep() {
            return sheep;
        }

        public boolean isInBounds() {
            return x >= 0 && x <= 1 && y >= 0 && y <= 1;
        }

    }

}
