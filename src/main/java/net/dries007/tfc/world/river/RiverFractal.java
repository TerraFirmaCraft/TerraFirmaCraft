/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;

public class RiverFractal
{
    private static final float MIN_BRANCH_ANGLE = 0.4f;

    public static RiverFractal build(RandomSource random, float drainX, float drainY, float angle, float length, int depth, float feather)
    {
        return new Builder(random, drainX, drainY, angle, length, depth, feather)
            .buildUntilCompletion()
            .finish();
    }

    /**
     * @return The shortest square distance between a point (px, py) and the line segment {@code edge}
     */
    public static float distance(Edge edge, float px, float py)
    {
        return RiverHelpers.distancePointToLineSq(edge.source.x, edge.source.y, edge.drain.x, edge.drain.y, px, py);
    }

    /**
     * @return The shortest square distance between a point {@code vertex} and the line segment {@code edge}
     */
    private static float distance(Edge edge, Vertex vertex)
    {
        return RiverHelpers.distancePointToLineSq(edge.source.x, edge.source.y, edge.drain.x, edge.drain.y, vertex.x, vertex.y);
    }

    /**
     * @return {@code true} if the lines described by (p1, q1), and (p2, q2) intersect.
     */
    private static boolean intersect(Vertex p1, Vertex q1, Vertex p2, Vertex q2)
    {
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        return (o1 != o2 && o3 != o4)
            || (o1 == 0 && intersectCollinear(p1, p2, q1))
            || (o2 == 0 && intersectCollinear(p1, q2, q1))
            || (o3 == 0 && intersectCollinear(p2, p1, q2))
            || (o4 == 0 && intersectCollinear(p2, q1, q2));

    }

    /**
     * @return {@code true} if, given three collinear points (p, q, r), that q intersects the line segment described by (p, r).
     */
    private static boolean intersectCollinear(Vertex p, Vertex q, Vertex r)
    {
        return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
    }

    /**
     * @return The orientation of three points (p, q, r) on a plane. 0 = collinear, 1 = clockwise, 2 = anticlockwise.
     */
    private static int orientation(Vertex p, Vertex q, Vertex r)
    {
        final float value = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (value == 0)
        {
            return 0;
        }
        return value > 0 ? 1 : 2;
    }

    private final List<Edge> edges;
    private final List<MidpointFractal> fractals;

    public RiverFractal(List<Edge> edges, RandomSource random)
    {
        this.edges = edges;
        this.fractals = edges.stream().map(e -> e.fractal(random, 4)).toList();
    }

    @Override
    public int hashCode()
    {
        return edges.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiverFractal other = (RiverFractal) o;
        return Objects.equals(edges, other.edges);
    }

    public List<Edge> getEdges()
    {
        return edges;
    }

    public List<MidpointFractal> getFractals()
    {
        return fractals;
    }

    public interface Context
    {
        boolean intersectAny(Edge edge);
    }

    /**
     * A step based builder for rivers
     */
    public static class Builder implements Context
    {
        private final Queue<Edge> branchQueue;
        private final RandomSource random;

        private final List<Edge> edges;
        private final Vertex root;
        private final List<Edge> branch;
        private final int depth;
        private final float featherSq;

        public Builder(RandomSource random, float drainX, float drainY, float angle, float length, int depth, float feather)
        {
            this.branchQueue = new LinkedList<>();
            this.random = random;

            this.edges = new ArrayList<>();
            this.root = new Vertex(drainX, drainY, angle, length, 0);
            this.branch = new ArrayList<>();
            this.depth = depth;
            this.featherSq = feather * feather;
        }

        public Builder buildUntilCompletion()
        {
            buildInitialBranch(this);
            while (!buildBranch(this)) ;
            return this;
        }

        /**
         * Builds the initial branch for a river
         *
         * @return {@code true} if the initial branch reached a sufficient length
         */
        public boolean buildInitialBranch(Context context)
        {
            Vertex prev = root;
            int length = depth + random.nextInt(1 + (int) (depth * 0.3f));
            for (int i = 0; i < length; i++)
            {
                Vertex next = computeNext(prev, prev.length, prev.distance);
                Edge nextEdge = new Edge(next, prev);
                if (context.intersectAny(nextEdge))
                {
                    // System.out.println("Stopped initial branch at " + i);
                    return i > 3; // minimum length
                }

                edges.add(nextEdge);
                prev = next;

                if (prev.distance < depth)
                {
                    branchQueue.offer(nextEdge);
                }
            }
            // System.out.println("Finished initial branch");
            return true;
        }

        public boolean buildBranch(Context context)
        {
            if (branchQueue.isEmpty())
            {
                return true; // Finished
            }

            Edge prevEdge = branchQueue.poll();
            Vertex prev = prevEdge.drain; // branch from the drain
            int prevDist = prev.distance + random.nextInt(3);
            Vertex branchNext = computeNext(prev, prev.length, prevDist);  // First vertex in this branch

            // Compare against the existing trunk of the river. Don't branch in a very similar direction
            float deltaAngle = Math.abs(prevEdge.source.angle - branchNext.angle);
            if (deltaAngle < MIN_BRANCH_ANGLE || (2 * Math.PI - deltaAngle < MIN_BRANCH_ANGLE))
            {
                return false;
            }

            // Generate a branch from this drain -> branchNext
            branch.clear();
            Edge first = new Edge(branchNext, prev);
            if (context.intersectAny(first))
            {
                return false; // First element of the branch is invalid.
            }
            branch.add(first);

            prev = branchNext;
            for (int i = 0; i < depth - prev.distance + random.nextInt(1 + (int) (depth * 0.3f)); i++)
            {
                // Next vertex
                Vertex next = computeNext(prev, prev.length, prevDist);

                Edge nextEdge = new Edge(next, prev);
                if (context.intersectAny(nextEdge))
                {
                    break;
                }

                branch.add(nextEdge);
                prev = next;
                prevDist = next.distance;

                if (prevDist < depth)
                {
                    branchQueue.offer(nextEdge);
                }
            }

            edges.addAll(branch); // Commit
            return false;
        }

        public RiverFractal finish()
        {
            return new RiverFractal(edges, random);
        }

        @Override
        public boolean intersectAny(Edge edge)
        {
            for (Edge e : edges)
            {
                if (e.source != edge.drain && e.drain != edge.drain && (distance(e, edge.source) < featherSq || RiverFractal.intersect(e.source, e.drain, edge.source, edge.drain)))
                {
                    return true;
                }
            }
            return false;
        }

        private Vertex computeNext(Vertex prev, float length, int distance)
        {
            float nextAngle = prev.angle() + (random.nextFloat() * 0.5f + 0.2f) * (random.nextBoolean() ? 1 : -1); // (random.nextFloat() * 1.4f - 0.7f);
            float nextLength = length * (random.nextFloat() * 0.08f + 0.92f);

            // Extend in the direction of the next angle
            float dx = Mth.cos(nextAngle) * nextLength, dy = Mth.sin(nextAngle) * nextLength;
            float x = prev.x() + dx, y = prev.y() + dy;

            return new Vertex(x, y, nextAngle, nextLength, distance + 1);
        }
    }

    public static class MultiParallelBuilder implements Context
    {
        private final List<Builder> builders;

        public MultiParallelBuilder()
        {
            this.builders = new ArrayList<>();
        }

        public Context add(Builder builder)
        {
            builders.add(builder);
            return this;
        }

        public List<RiverFractal> build()
        {
            // First, build each initial branch. If this is invalid, the river is removed as there's no point even trying to build branches.
            builders.removeIf(builder -> !builder.buildInitialBranch(this));

            // Copy the list into a temporary one, and repeatedly call removeIf() until all elements have been built (removed).
            List<Builder> working = new ArrayList<>(builders);
            while (!working.isEmpty())
            {
                working.removeIf(builder -> builder.buildBranch(this));
            }

            // Map each to a completed fractal.
            return builders.stream().map(Builder::finish).collect(Collectors.toList());
        }

        @Override
        public boolean intersectAny(Edge edge)
        {
            if (!isLegal(edge.source))
            {
                return true;
            }
            for (Builder builder : builders)
            {
                if (builder.intersectAny(edge))
                {
                    return true;
                }
            }
            return false;
        }

        protected boolean isLegal(Vertex vertex)
        {
            return true;
        }
    }

    public record Vertex(float x, float y, float angle, float length, int distance) {}

    public record Edge(Vertex source, Vertex drain)
    {
        public MidpointFractal fractal(RandomSource random, int bisections)
        {
            return new MidpointFractal(random, bisections, source.x, source.y, drain.x, drain.y);
        }
    }
}
