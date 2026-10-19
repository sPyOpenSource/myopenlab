# MyOpenLab YouTube Series — Series Bible

**Working title:** *MyOpenLab Mastery: Visual Programming for Arduino & Raspberry Pi*

**Format:** YouTube long-form series (8 min per episode), screen-recorded + talking-head host

**Target audience:** Makers, engineering students, hobbyists, Java-curious programmers who want to build control logic without writing everything from scratch.

**Tone:** Patient, hands-on, "build first, explain after." One concrete win per episode. No fluff.

**Host persona:** A friendly developer who uses MyOpenLab daily. Explains *why* something works by pointing at the code (`Element.java:653`, `definition.def`, etc.) but never gets bogged down.

---

## Series Overview (10 episodes)

| # | Title | Core win | Key concepts |
|---|-------|----------|--------------|
| 1 | What is MyOpenLab? | Install, open a project, run a simulation | VM, Circuit vs Front Panel, Elements |
| 2 | Your First Circuit | Build Timer → Counter → LED | Pins, wires, event-driven `process()` |
| 3 | Elements Deep Dive | Configure pins & properties | Input/output pins, VSObject types, properties dialog |
| 4 | Front Panels & GUIs | Build a slider → gauge UI | Front Panel VM, widgets, user interaction |
| 5 | Data Types & Variables | Move numbers, strings, booleans | VSInteger, VSDouble, VSString, VSBoolean, changed flag |
| 6 | Clocks & Timers | Make something happen periodically | `processClock()`, `jNotifyMeForClock`, timers |
| 7 | Build Your Own Module | Create a custom element from the wizard | `definition.def`, `JVSMain`, `init()`, `process()`, compile |
| 8 | Debugging & Simulation | Find why a wire isn't firing | Variable Watcher, Console, test points, processList |
| 9 | Arduino / Raspberry Pi | Deploy to hardware | Drivers, serial, pin mapping, flashing |
| 10 | Capstone Project | Automated plant-watering system | Everything combined |

---

## How to use this package

- `EPISODE_01_*.md` through `EPISODE_10_*.md` = full shoot-ready scripts.
- Each script includes: hook, objectives, scene-by-scene breakdown with **B-ROLL / SCREEN / ON-SCREEN TEXT** cues, host dialogue, code snippets to show, common pitfalls, recap quiz, CTA, and next-episode teaser.
- Code snippets reference real MyOpenLab structure so the host can point at actual files.

---

## Brand assets (suggested)

- **Intro animation:** wires snapping together into the MyOpenLab logo.
- **Lower third:** "MyOpenLab Mastery — Ep. [N]" + episode title.
- **Code font:** monospace, highlighted syntax (Java).
- **On-screen key terms:** bold, colored chips that pop up when a term is first mentioned (VM, Element, Pin, process()).
- **End screen:** Subscribe + playlist link to next episode + GitHub repo link.

---

## Production notes

- Record screen at 1080p, 60fps. Zoom canvas when placing/wiring elements.
- Use the English UI (lessons are in English; note source has German/Spanish docs — translate quotes when needed).
- Cite code liberally since the audience is developer-friendly: `src/VisualLogic/VMObject.java`, `definition.def`, `src/tools/JVSMain.java`.
- Every episode ends with a 30-second "try this at home" mini-challenge.
