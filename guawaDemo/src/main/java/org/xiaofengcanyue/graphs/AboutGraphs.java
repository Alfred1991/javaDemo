package org.xiaofengcanyue.graphs;

import com.google.common.graph.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * graph包含一组nodes(aka vertice)和一组edges(aka links or arcs)，只有一个edge的nodes被称为endpoints。
 * edge可以是有方向的，它从source指向target(aka destination)。
 * edge也可以是无方向的，它适合描述对称的关系(coauthored a paper with,distance between,sibling of)。
 * self-loop是一个连接自身的edge。
 * 如果两个edges以相同的方向连接相同的两个nodes，那么它们是parallel。
 * 如果两个edges以不同的方向连接相同的两个nodes，那么它们是antiparallel。
 *
 * common.graph提供：
 * directed graphs
 * undirected graphs
 * nodes and/or edges with associated values(weights,labels,etc.)
 * graphs that do/don`t allow self-loops
 * graphs that do/don`t allow parallel edges(graphs with parallel edges are sometimes called multigraphs)
 * graphs whose nodes/edges are insertion-ordered,sorted,or unordered
 *
 * relationship的存储方式包括：matrices,adjacency lists,adjacency maps等，可以根据具体情况进行调整。
 *
 * 目前common.graph不支持下面的图变体：
 * trees,forests
 * graphs with elements of the same kind(nodes or edges) that have different types(eg:bipartite/k-partite graphs,multimodal graphs)
 * hypergraphs
 *
 * 存在三种顶级graph interfaces：
 * Graph
 * ValueGraph的edge可以关联值
 * Network以edge为中心，就像graph以node为中心。Network支持parallel edges
 */
public class AboutGraphs {

    /**
     * builder一般提供两类选项：
     *   1、constraints，例如：
     *     whether the graph is directed
     *     whether this graph allows self-loops
     *     whether this graph`s edges are sorted
     *   2、optimization hints，例如：
     *     初始容量
     */
    public static void CreateAnInstance(){
        // Creating mutable graphs
        MutableGraph<Integer> graph = GraphBuilder.undirected().build();
    }


    /**
     * graph中的elements需要被认为是内部数据结构的keys。
     * 因此graph的elements的类型需要实现equals和hashCode方法。
     * graph的elements需满足下列特性：
     *   1、Uniqueness，若A.equals(B) == true则A和B中最多只有其中之一能称为graph的element。
     *   2、hashCode()需要和equals()保持一致，就像Object.hashCode()。
     *   3、如果nodes是有序的（例如GraphBuilder.orderNodes()），那么排序方式(通过Comparator和Comparable)也需要和equals保持一致。
     * 如果graph elements是可变的，那么需要满足：
     *   1、可变状态不会影响equals()/hashCode()方法的返回值。
     *   2、不要创建很多equals的elements，并期望它们相互转换。
     * 当需要保存可变数据时，最好创建一个node到可变数据的map。
     *
     */
    public static void Example(Graph graph,ValueGraph valueGraph,Network network,Object node,Object u,Object v){
        graph.nodes().contains(node);

        // This is the preferred syntax since 23.0 for all graph types.
        graph.hasEdgeConnecting(u, v);

// These are equivalent (to each other and to the above expression).
        graph.successors(u).contains(v);
        graph.predecessors(v).contains(u);

// This is equivalent to the expressions above if the graph is undirected.
        graph.adjacentNodes(u).contains(v);

// This works only for Networks.
        network.edgesConnecting(u, v).isEmpty();

// This works only if "network" has at most a single edge connecting u to v.
        network.edgeConnecting(u, v).isPresent();  // Java 8 only
        network.edgeConnectingOrNull(u, v);

// These work only for ValueGraphs.
        valueGraph.edgeValue(u, v).isPresent();  // Java 8 only
        valueGraph.edgeValueOrDefault(u, v, null);

    }

    // Return all nodes reachable by traversing 2 edges starting from "node"
    // (ignoring edge direction and edge weights, if any, and not including "node").
    public static <N> Set<N> getTwoHopNeighbors(Graph<N> graph, N node) {
        Set<N> twoHopNeighbors = new HashSet<>();
        for (N neighbor : graph.adjacentNodes(node)) {
            twoHopNeighbors.addAll(graph.adjacentNodes(neighbor));
        }
        twoHopNeighbors.remove(node);
        return twoHopNeighbors;
    }

}
