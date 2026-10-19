# EPISODE 2 — Pins, Wires, and the Event-Driven Secret

**Target length:** 10–12 minutes  
**Concrete win:** Viewer understands that MyOpenLab is *event-driven* (not flow-based), traces a wire from source to destination `process()`, and explains the `changed` flag.

---

## [0:00–0:45] HOOK

**VISUAL:** Screen recording of a circuit that *doesn't work*: a Constant → wire → Display, but the Display shows 0 and never changes. Host looks puzzled.

**HOST (to camera):**
> "You wired two elements together. The source has a value. The destination shows nothing. Why?"
>
> "Here's the trap: MyOpenLab is *not* like a spreadsheet. It doesn't constantly push data down wires. It's *event-driven*. Data only moves when something changes — and only the element that receives the change wakes up and runs."
>
> "Today you'll learn how wires actually work, what the `process()` method is, and why 'changed' is the most important word in MyOpenLab."

**ON-SCREEN TEXT:** MyOpenLab is EVENT-DRIVEN — not flow-based  
**ON-SCREEN TEXT:** The `changed` flag gates everything

---

## [0:45–2:00] THE MENTAL MODEL: WATER PIPES VS. MESSAGES

**VISUAL:** Side-by-side animation. Left: water flowing continuously through pipes (flow-based, like LabVIEW/spreadsheet). Right: envelopes being handed from one person to the next only when something new arrives (event-driven).

**HOST (voiceover):**
> "In flow-based tools, values stream constantly. In MyOpenLab, think of a wire as a *message queue*. When an element writes a new value to an output pin, it drops an envelope — a wire object — into the VM's mailbox, called the `processList`."
>
> "The VM picks up that envelope, finds the destination element, and calls its `process()` method. Only then does the destination run."
>
> "No change? No message. No message? The destination stays asleep."

**ON-SCREEN KEY TERM:** `processList` — the VM's event queue of wires awaiting delivery  
**ON-SCREEN KEY TERM:** `process()` — runs on the destination element when new data arrives

---

## [2:00–3:30] PINS: INPUTS LEFT, OUTPUTS RIGHT

**VISUAL:** Screen recording. Drag a "Constant" element and a "Display" onto a Circuit VM canvas.

**HOST (voiceover):**
> "Look at any element. Pins on the left are inputs. Pins on the right are outputs. Each pin has a data type — boolean, integer, double, string — and a direction."
>
> "The Constant has one output pin. The Display has one input pin. Drag from Constant's output to Display's input. A wire is born."

**SCREEN ACTION:** Drag wire from Constant out(0) → Display in(0).

**ON-SCREEN KEY TERM:** Pin — connection point; left = input, right = output  
**ON-SCREEN KEY TERM:** Draht / wire — connects source pin to destination pin

---

## [3:30–5:00] WHAT ACTUALLY HAPPENS WHEN YOU WRITE A VALUE

**VISUAL:** Screen recording with zoom on the Constant's properties. Change its value from 0 to 42. Watch the Display update to 42.

**HOST (voiceover):**
> "Set the Constant to 42. Behind the scenes, MyOpenLab does three things:"
>
> "One: it creates a VS object — here a VSInteger — holds the value 42, and sets its `changed` flag to true."
>
> "Two: it calls `element.addToProcesslist(out)`, which queues the wire so the VM knows the Display needs to run."
>
> "Three: the VM's run loop pops that wire from the `processList`, finds the Display, and calls its `process()` method. The Display reads the input pin and shows 42."

**ON-SCREEN CODE BLOCK:**
```java
out.setValue(42);              // put data in the VS object
out.setChanged(true);          // mark it as fresh
element.addToProcesslist(out); // queue the wire → destination process()
```

**ON-SCREEN KEY TERM:** VSObject — base of all pin values (VSInteger, VSDouble, VSString, VSBoolean…)  
**ON-SCREEN KEY TERM:** `changed` flag — `isChanged()`; data only propagates when fresh

**Citation flash:** `src/VisualLogic/variables/VSObject.java:57` — `isChanged()` / `setValue()`

---

## [5:00–6:30] THE CHANGED FLAG: WHY YOUR WIRE MIGHT BE SILENT

**VISUAL:** Back to the broken circuit from the hook. Constant set to 42, but Display still 0.

**HOST (to camera):**
> "Here's why your wire might be silent: if the source writes the *same* value again, `setChanged()` may not fire, or you never called `addToProcesslist()`. No changed flag → no envelope in the mailbox → `process()` never runs on the destination."
>
> "So when debugging, always ask: did the source actually *change*? And did it `addToProcesslist()`?"

**SCREEN ACTION:** Demonstrate: set Constant to 42 (Display updates to 42), then set it to 42 *again* (Display does *not* update — no visual change because nothing changed).

**HOST:**
> "Same value, second time. Watch — nothing happens. The changed flag stayed false. That's by design: MyOpenLab only reacts to fresh data."

---

## [6:30–8:00] TRACING THE FLOW: processList → process()

**VISUAL:** Animated diagram over the screen recording.

```
[Constant] writes 42
        │
        ▼
   VSInteger(42), changed=true
        │
        ▼
   addToProcesslist(wire)  ──▶  VM.processList [ queue ]
        │
        ▼
   VM run loop pops wire
        │
        ▼
   destination Display.process() runs
        │
        ▼
   Display reads in(0) → shows 42
```

**HOST (voiceover):**
> "This is the heartbeat of every MyOpenLab simulation. The VM has a run loop. It pulls wires off the `processList`, finds each destination element, and invokes `process()`. That's why we call it event-driven: the *event* is 'new data on a wire', and the *response* is `process()`."

**ON-SCREEN KEY TERM:** `processList` pop → destination `process()` — the run loop

**Citation flash:** `src/VisualLogic/VMObject.java:1781` — processList  
`src/VisualLogic/VMObject.java:1843` — process()

---

## [8:00–9:00] INPUTS ON LEFT, OUTPUTS ON RIGHT — A MULTI-PIN EXAMPLE

**VISUAL:** Drag a "Comparator" element. It has two inputs (left) and one output (right). Wire two Constants into its inputs, wire its output to an LED.

**HOST (voiceover):**
> "Some elements have multiple pins. A Comparator takes two inputs, compares them, and outputs true or false. Wire Constant A to input 0, Constant B to input 1. When *either* input changes, the Comparator's `process()` runs, reads both inputs, and writes its output — which wakes up the LED."

**SCREEN ACTION:** Change Constant A. LED toggles. Explain: one changed input triggered the whole chain.

---

## [9:00–10:00] RECAP & QUIZ

**HOST:**
> "Recap: MyOpenLab is event-driven. Wires carry messages, not continuous streams. A source writes a value, sets the changed flag, and calls `addToProcesslist()`. The VM queues the wire, then calls the destination's `process()`. No change = no message = no work."

**QUIZ (on-screen):**
> Q: When does a destination element's `process()` run?  
> A) Every simulation tick, constantly  
> B) Only when a wire delivers new (changed) data to it  
> C) Only when you click Run

**ANSWER:** B — on changed data delivery.

---

## [10:00–11:00] TRY AT HOME + CTA

**HOST:**
> "Homework: build Constant → Comparator → LED. Make the LED turn on when Constant A is greater than Constant B. Then change A repeatedly — watch the LED flip. Notice it only reacts when a value actually changes."
>
> "Like and subscribe for Episode 3, where we configure pins and properties properly, and meet the full VSObject type family."

**END SCREEN:** Subscribe | Playlist | GitHub

---

## Production cues

| Time | Cue | Notes |
|------|-----|-------|
| 0:00 | Broken circuit | Constant → Display stuck at 0 |
| 0:45 | Pipe vs message animation | Motion graphic |
| 3:30 | Zoom | Constant out pin → Display in pin |
| 5:00 | Code block overlay | `setValue`, `setChanged`, `addToProcesslist` |
| 6:30 | processList animation | Queue → pop → process() diagram |
| 8:00 | Comparator demo | Multi-pin chain reaction |
| 9:00 | Quiz card | 3-choice, answer B |

**Code citations to flash:**  
`src/VisualLogic/variables/VSObject.java:57` — changed flag  
`src/VisualLogic/VMObject.java:1781` — processList  
`src/VisualLogic/VMObject.java:1843` — process()  
`src/tools/JVSMain.java` — base class with `element` and lifecycle
