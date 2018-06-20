package net.wesjd.pvpacademy.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RandomTeleporter {

    private final LoadingCache<Pair<Location, Location>, List<Pair<Integer, Integer>>> locationCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .maximumSize(5)
            .build(new CacheLoader<Pair<Location, Location>, List<Pair<Integer, Integer>>>() {
                @Override
                public List<Pair<Integer, Integer>> load(Pair<Location, Location> bounds) throws Exception {
                    final List<Pair<Integer, Integer>> ret = new ArrayList<>();
                    final Location bounds1 = bounds.getLeft();
                    final Location bounds2 = bounds.getRight();
                    for(int x=Math.min(bounds1.getBlockX(), bounds2.getBlockX()); x < Math.max(bounds1.getBlockX(), bounds2.getBlockX()); x++) {
                        for(int z=Math.min(bounds1.getBlockZ(), bounds2.getBlockZ()); z < Math.max(bounds1.getBlockZ(), bounds2.getBlockZ()); z++) {
                            ret.add(ImmutablePair.of(x, z));
                        }
                    }
                    return ret;
                }
            });

    public Location getRandomLocation(Pair<Location, Location> bounds) throws ExecutionException {
        final List<Pair<Integer, Integer>> pairs = locationCache.get(bounds);
        final Pair<Integer, Integer> randomPair = pairs.get(ThreadLocalRandom.current().nextInt(pairs.size()));
        Block safe = bounds.getLeft().getWorld().getHighestBlockAt(randomPair.getLeft(), randomPair.getRight());
        while(safe.isEmpty()) {
            final Block below = safe.getRelative(BlockFace.DOWN);
            if(!below.isEmpty() && below.getType().isSolid() && below.getType().isTransparent()) break;
            safe = below;
        }
        if(safe.isLiquid()) return getRandomLocation(bounds);
        return safe.getLocation().add(0, .5, 0);
    }

    public void teleportRandomly(Player player, Pair<Location, Location> bounds) throws ExecutionException {
        player.teleport(getRandomLocation(bounds));
    }

}