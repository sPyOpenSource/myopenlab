const fs = require('fs');

// Read assembled graph
const assembled = JSON.parse(fs.readFileSync('/Users/xuyi/Source/Java/myopenlab/.understand-anything/intermediate/assembled-graph.json', 'utf8'));

// Read layers and tour
const layers = JSON.parse(fs.readFileSync('/Users/xuyi/Source/Java/myopenlab/.understand-anything/intermediate/layers.json', 'utf8'));
const tour = JSON.parse(fs.readFileSync('/Users/xuyi/Source/Java/myopenlab/.understand-anything/intermediate/tour.json', 'utf8'));

// Build final knowledge graph
const knowledgeGraph = {
  version: "1.0.0",
  project: {
    name: "myopenlab",
    languages: ["java"],
    frameworks: ["swing"],
    description: "Visual Logic programming environment for creating visual programs with drag-and-drop elements, wires, and variables. Supports simulation, code generation, and package management.",
    analyzedAt: new Date().toISOString(),
    gitCommitHash: "unknown"
  },
  nodes: assembled.nodes,
  edges: assembled.edges,
  layers: layers,
  tour: tour
};

// Write final knowledge graph
fs.writeFileSync(
  '/Users/xuyi/Source/Java/myopenlab/.understand-anything/knowledge-graph.json',
  JSON.stringify(knowledgeGraph, null, 2)
);

console.log('Knowledge graph written successfully');
console.log(`Nodes: ${knowledgeGraph.nodes.length}`);
console.log(`Edges: ${knowledgeGraph.edges.length}`);
console.log(`Layers: ${knowledgeGraph.layers.length}`);
console.log(`Tour steps: ${knowledgeGraph.tour.length}`);
