package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author balugaq
 */
@NullMarked
public class PackSorter {
    public static List<Pack> sortPacks(List<Pack> packs) {
        Map<String, Pack> packMap = packs.stream()
                .collect(Collectors.toMap(p -> p.getPackID().getId(), pack -> pack, (a, b) -> a));
        Set<String> allPackNames = packMap.keySet();

        Map<String, Set<String>> hardDependencyGraph = buildHardDependencyGraph(packs, packMap);
        Set<String> validPacks = processHardDependencies(hardDependencyGraph, allPackNames);

        if (validPacks.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Set<String>> softDependencyGraph = buildSoftDependencyGraph(packs, validPacks);
        return processSoftDependencies(softDependencyGraph, validPacks, packMap);
    }

    private static Map<String, Set<String>> buildHardDependencyGraph(List<Pack> packs, Map<String, Pack> packMap) {
        Map<String, Set<String>> graph = new HashMap<>();

        for (Pack pack : packs) {
            String packName = pack.getPackID().getId();
            List<PackDesc> pd = pack.getPackDependencies();
            if (pd != null) {
                for (String dep : pd.stream().map(PackDesc::getId).toList()) {
                    if (packMap.containsKey(dep)) {
                        graph.computeIfAbsent(dep, k -> new HashSet<>()).add(packName);
                    }
                }
            }
        }

        return graph;
    }

    private static Set<String> processHardDependencies(Map<String, Set<String>> hardDependencyGraph, Set<String> allPacks) {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String pack : allPacks) {
            inDegree.put(pack, 0);
        }

        for (Set<String> dependents : hardDependencyGraph.values()) {
            for (String dependent : dependents) {
                inDegree.put(dependent, inDegree.get(dependent) + 1);
            }
        }

        List<String> topoOrder = topologicalSort(hardDependencyGraph, inDegree, allPacks);

        return new HashSet<>(topoOrder);
    }

    private static List<String> topologicalSort(Map<String, Set<String>> graph, Map<String, Integer> inDegree, Set<String> allNodes) {
        Queue<String> queue = new LinkedList<>();
        List<String> result = new ArrayList<>();
        Map<String, Integer> tempInDegree = new HashMap<>(inDegree);
        for (String node : allNodes) {
            if (tempInDegree.getOrDefault(node, 0) == 0) {
                queue.add(node);
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            if (graph.containsKey(current)) {
                for (String dependent : graph.get(current)) {
                    tempInDegree.put(dependent, tempInDegree.get(dependent) - 1);
                    if (tempInDegree.get(dependent) == 0) {
                        queue.add(dependent);
                    }
                }
            }
        }

        if (result.size() != allNodes.size()) {
            Set<String> cyclicNodes = new HashSet<>(allNodes);
            result.forEach(cyclicNodes::remove);

            Map<String, Set<String>> cyclicSubgraph = new HashMap<>();
            for (String node : cyclicNodes) {
                cyclicSubgraph.put(node, new HashSet<>());
            }
            for (String from : graph.keySet()) {
                if (cyclicNodes.contains(from)) {
                    for (String to : graph.get(from)) {
                        if (cyclicNodes.contains(to)) {
                            cyclicSubgraph.get(from).add(to);
                        }
                    }
                }
            }

            findAndLogCycles(cyclicSubgraph, DependencyType.HARD);
        }

        return result;
    }

    private static Map<String, Set<String>> buildSoftDependencyGraph(List<Pack> packs, Set<String> validPacks) {
        Map<String, Set<String>> graph = new HashMap<>();

        for (Pack pack : packs) {
            String packName = pack.getPackID().getId();
            if (!validPacks.contains(packName)) {
                continue;
            }

            List<PackDesc> psd = pack.getPackSoftDependencies();
            if (psd != null) {
                for (String softDep : psd.stream().map(PackDesc::getId).toList()) {
                    if (validPacks.contains(softDep)) {
                        graph.computeIfAbsent(softDep, k -> new HashSet<>()).add(packName);
                    }
                }
            }

            List<PackDesc> plb = pack.getPackLoadBefores();
            if (plb != null) {
                for (String loadBefore : plb.stream().map(PackDesc::getId).toList()) {
                    if (validPacks.contains(loadBefore)) {
                        graph.computeIfAbsent(packName, k -> new HashSet<>()).add(loadBefore);
                    }
                }
            }
        }

        return graph;
    }

    private static List<Pack> processSoftDependencies(Map<String, Set<String>> softDependencyGraph,
                                                      Set<String> validPacks,
                                                      Map<String, Pack> packMap) {
        List<List<String>> sccs = findSCCs(softDependencyGraph, validPacks);

        List<List<String>> sortedSCCs = sccs.stream()
                .map(scc -> {
                    List<String> sorted = new ArrayList<>(scc);
                    sorted.sort(String::compareTo);
                    return sorted;
                })
                .collect(Collectors.toList());

        Map<Integer, Set<Integer>> sccGraph = buildSCCGraph(sortedSCCs, softDependencyGraph);

        List<Integer> sccOrder = topologicalSortSCCs(sccGraph, sortedSCCs.size());

        List<Pack> result = new ArrayList<>();
        for (int sccIndex : sccOrder) {
            for (String packName : sortedSCCs.get(sccIndex)) {
                result.add(packMap.get(packName));
            }
        }

        return result;
    }

    private static List<List<String>> findSCCs(Map<String, Set<String>> graph, Set<String> nodes) {
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        for (String node : nodes) {
            if (!visited.contains(node)) {
                dfsFirstPass(graph, node, visited, stack);
            }
        }

        Map<String, Set<String>> reversedGraph = buildReversedGraph(graph, nodes);

        visited.clear();
        List<List<String>> sccs = new ArrayList<>();

        while (!stack.isEmpty()) {
            String node = stack.pop();
            if (!visited.contains(node)) {
                List<String> scc = new ArrayList<>();
                dfsSecondPass(reversedGraph, node, visited, scc);
                sccs.add(scc);

                if (scc.size() > 1) {
                    Map<String, Set<String>> cyclicSubgraph = new HashMap<>();
                    for (String sccNode : scc) {
                        cyclicSubgraph.put(sccNode, new HashSet<>());
                    }
                    for (String from : graph.keySet()) {
                        if (scc.contains(from)) {
                            for (String to : graph.get(from)) {
                                if (scc.contains(to)) {
                                    cyclicSubgraph.get(from).add(to);
                                }
                            }
                        }
                    }

                    findAndLogCycles(cyclicSubgraph, DependencyType.SOFT);
                }
            }
        }

        return sccs;
    }

    private static void findAndLogCycles(Map<String, Set<String>> graph, DependencyType dependencyType) {
        Set<String> visited = new HashSet<>();
        Set<String> recStack = new HashSet<>();
        List<String> path = new ArrayList<>();
        Set<String> processedCycles = new HashSet<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node) && !processedCycles.contains(node)) {
                detectCycle(node, graph, visited, recStack, path, processedCycles, dependencyType);
            }
        }
    }

    private static void detectCycle(String node, Map<String, Set<String>> graph,
                                    Set<String> visited, Set<String> recStack,
                                    List<String> path, Set<String> processedCycles,
                                    DependencyType dependencyType) {
        if (recStack.contains(node)) {
            int index = path.indexOf(node);
            List<String> cycle = new ArrayList<>(path.subList(index, path.size()));
            cycle.add(node);
            PackManager.packDependencyCycle(cycle, dependencyType);

            processedCycles.addAll(cycle);
            return;
        }

        if (visited.contains(node)) {
            return;
        }

        visited.add(node);
        recStack.add(node);
        path.add(node);

        if (graph.containsKey(node)) {
            for (String neighbor : graph.get(node)) {
                detectCycle(neighbor, graph, visited, recStack, path, processedCycles, dependencyType);
            }
        }

        recStack.remove(node);
        path.removeLast();
    }

    private static void dfsFirstPass(Map<String, Set<String>> graph, String node, Set<String> visited, Stack<String> stack) {
        visited.add(node);
        if (graph.containsKey(node)) {
            for (String neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfsFirstPass(graph, neighbor, visited, stack);
                }
            }
        }
        stack.push(node);
    }

    private static Map<String, Set<String>> buildReversedGraph(Map<String, Set<String>> graph, Set<String> nodes) {
        Map<String, Set<String>> reversedGraph = new HashMap<>();
        nodes.forEach(node -> reversedGraph.put(node, new HashSet<>()));

        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            String from = entry.getKey();
            for (String to : entry.getValue()) {
                reversedGraph.get(to).add(from);
            }
        }

        return reversedGraph;
    }

    private static void dfsSecondPass(Map<String, Set<String>> reversedGraph, String node, Set<String> visited, List<String> scc) {
        visited.add(node);
        scc.add(node);
        for (String neighbor : reversedGraph.get(node)) {
            if (!visited.contains(neighbor)) {
                dfsSecondPass(reversedGraph, neighbor, visited, scc);
            }
        }
    }

    private static Map<Integer, Set<Integer>> buildSCCGraph(List<List<String>> sccs, Map<String, Set<String>> originalGraph) {
        Map<Integer, Set<Integer>> sccGraph = new HashMap<>();
        Map<String, Integer> nodeToScc = new HashMap<>();

        for (int i = 0; i < sccs.size(); i++) {
            for (String node : sccs.get(i)) {
                nodeToScc.put(node, i);
            }
            sccGraph.put(i, new HashSet<>());
        }

        for (Map.Entry<String, Set<String>> entry : originalGraph.entrySet()) {
            String from = entry.getKey();
            int fromScc = nodeToScc.get(from);

            for (String to : entry.getValue()) {
                int toScc = nodeToScc.get(to);
                if (fromScc != toScc) {
                    sccGraph.get(fromScc).add(toScc);
                }
            }
        }

        return sccGraph;
    }

    private static List<Integer> topologicalSortSCCs(Map<Integer, Set<Integer>> sccGraph, int sccCount) {
        Map<Integer, Integer> inDegree = new HashMap<>();
        for (int i = 0; i < sccCount; i++) {
            inDegree.put(i, 0);
        }

        for (Set<Integer> neighbors : sccGraph.values()) {
            for (int neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < sccCount; i++) {
            if (inDegree.get(i) == 0) {
                queue.add(i);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);

            for (int neighbor : sccGraph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return result;
    }
}
