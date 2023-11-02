/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public final class River
{
    private static final double MIN_BRANCH_ANGLE = 0.4f;
    private static final int MIN_BRANCH_DISTANCE = 2;
    private static final int MIN_RIVER_EDGE_COUNT = 6;

    /**
     * @return The shortest square distance between a point {@code vertex} and the line segment {@code edge}
     */
    private static double distance(Edge edge, Vertex vertex)
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
        final double value = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (value == 0)
        {
            return 0;
        }
        return value > 0 ? 1 : 2;
    }

    public interface Context
    {
        boolean intersectAny(Edge edge);
    }

    public record Vertex(double x, double y, double angle, double length, int distance) {}

    public record Edge(Vertex source, Vertex drain)
    {
        public MidpointFractal fractal(RandomSource random, int bisections)
        {
            return new MidpointFractal(random, bisections, source.x, source.y, drain.x, drain.y);
        }
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
        private final double featherSq;

        public Builder(RandomSource random, double drainX, double drainY, double angle, double length, int depth, double feather)
        {
            this.branchQueue = new LinkedList<>();
            this.random = random;

            this.edges = new ArrayList<>();
            this.root = new Vertex(drainX, drainY, angle, length, 0);
            this.branch = new ArrayList<>();
            this.depth = depth;
            this.featherSq = feather * feather;
        }

        /**
         * Builds the initial branch for a river
         *
         * @return {@code true} if the initial branch reached a sufficient length
         */
        private boolean buildInitialBranch(Context context)
        {
            Vertex prev = root;
            int length = depth + random.nextInt(1 + (int) (depth * 0.3f));
            for (int i = 0; i < length; i++)
            {
                Vertex next = computeNext(prev, prev.length, prev.distance);
                Edge nextEdge = new Edge(next, prev);
                if (context.intersectAny(nextEdge))
                {
                    pruneRiverIfTooShort();
                    return true;
                }

                edges.add(nextEdge);
                prev = next;

                if (prev.distance < depth && prev.distance >= MIN_BRANCH_DISTANCE)
                {
                    branchQueue.offer(nextEdge);
                }
            }
            return false;
        }

        private boolean buildBranch(Context context)
        {
            if (branchQueue.isEmpty())
            {
                pruneRiverIfTooShort();
                return true;
            }

            Edge prevEdge = branchQueue.poll();
            Vertex prev = prevEdge.drain; // branch from the drain
            int prevDist = prev.distance + random.nextInt(3);
            Vertex branchNext = computeNext(prev, prev.length, prevDist);  // First vertex in this branch

            // Compare against the existing trunk of the river. Don't branch in a very similar direction
            double deltaAngle = Math.abs(prevEdge.source.angle - branchNext.angle);
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

                if (prevDist < depth && prevDist >= MIN_BRANCH_DISTANCE)
                {
                    branchQueue.offer(nextEdge);
                }
            }

            edges.addAll(branch); // Commit
            return false;
        }

        private void pruneRiverIfTooShort()
        {
            // Do a check to ensure that the river is of sufficient size and length, and if not, discard it
            if (edges.size() < MIN_RIVER_EDGE_COUNT)
            {
                edges.clear();
                branchQueue.clear();
            }
        }

        @Override
        public boolean intersectAny(Edge edge)
        {
            for (Edge e : edges)
            {
                if (e.source != edge.drain && e.drain != edge.drain && (distance(e, edge.source) < featherSq || intersect(e.source, e.drain, edge.source, edge.drain)))
                {
                    return true;
                }
            }
            return false;
        }

        private Vertex computeNext(Vertex prev, double length, int distance)
        {
            double nextAngle = distance == 0 ?
                prev.angle() : // For distance = 0, this is the mouth of a river, and we want to use the computed 'best' start angle directly
                prev.angle() + (random.nextDouble() * 0.5f + 0.2f) * (random.nextBoolean() ? 1 : -1);
            double nextLength = length * (random.nextDouble() * 0.08f + 0.92f);

            // Extend in the direction of the next angle
            double dx = Mth.cos((float) nextAngle) * nextLength, dy = Mth.sin((float) nextAngle) * nextLength;
            double x = prev.x() + dx, y = prev.y() + dy;

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

        public <E> List<E> build(Function<Edge, E> map)
        {
            // Use a heap, sorted by total river length (edge count), so we prioritize building large rivers, and discarding short ones.
            final PriorityQueue<Builder> working = new PriorityQueue<>(Comparator.comparing(b -> -b.edges.size() - 10 * b.branchQueue.size()));
            for (Builder builder : builders)
            {
                if (builder.buildInitialBranch(this))
                {
                    working.offer(builder);
                }
            }

            while (!working.isEmpty())
            {
                final Builder builder = working.poll();
                if (!builder.buildBranch(this))
                {
                    working.offer(builder);
                }
            }

            return builders.stream()
                .flatMap(e -> e.edges.stream().map(map))
                .toList();
        }

        @Override
        public boolean intersectAny(Edge edge)
        {
            if (!isLegal(edge.drain, edge.source))
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

        protected boolean isLegal(Vertex prev, Vertex vertex)
        {
            return true;
        }
    }
}
