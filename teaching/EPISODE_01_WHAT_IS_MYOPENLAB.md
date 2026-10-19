# EPISODE 1 — What Is MyOpenLab? (And Why It's Not LabVIEW)

**Target length:** 6-10 minutes  
**Concrete win:** Viewer installs MyOpenLab, opens a project, places two elements, wires them, and runs a simulation.

---

## [0:00–0:45] HOOK

**VISUAL:** Split screen. Left: a blinking LED on an Arduino. Right: the MyOpenLab canvas with a Timer wired to a Counter wired to an LED — all running live.

**HOST (to camera):**
> "You want to control hardware — maybe an Arduino, a Raspberry Pi, some sensors — but you don't want to write hundreds of lines of C or Java. You want to draw your program like a diagram. That's MyOpenLab."
>
> "It looks a bit like LabVIEW. Drag blocks, connect them with wires, hit run. But under the hood it's completely different: it's Java, it's event-driven, and it's free and open source."
>
> "Today: install it, open your first project, and run a live simulation. By the end of this video you'll know what a VM is, what an element is, and how data actually flows."

**ON-SCREEN TEXT:** MyOpenLab — Visual Programming for Arduino & Raspberry Pi  
**ON-SCREEN TEXT:** Free · Open Source · Java-based

---

## [0:45–2:00] WHAT IS MYOPENLAB?

**VISUAL:** Screen recording of the myopenlab.de homepage, then the download page. Mouse clicks the Windows/Linux download.

**HOST (voiceover):**
> "MyOpenLab is a Java desktop application for graphical programming. You build programs by placing blocks — we call them *elements* — on a canvas and connecting them with *wires*."
>
> "It's popular for control applications: reading sensors, driving relays, logging data, building dashboards. If you've used LabVIEW, the workflow will feel familiar, but the architecture is not flow-based. MyOpenLab is *event-driven*, which matters a lot once you start debugging — we'll cover that in Episode 2."

**ON-SCREEN KEY TERM:** Element — a block with pins and logic  
**ON-SCREEN KEY TERM:** Wire — carries data between pins

---

## [2:00–3:30] INSTALLATION & FIRST LAUNCH

**VISUAL:** Screen recording. Download the ZIP, extract, run `start_Windows.bat` (or the shell script on Mac/Linux). The MyOpenLab splash screen appears, then `FrameMain`.

**HOST (voiceover):**
> "No installer required. Download, extract, run the start script. The first thing you see is the main window: a menu bar, a toolbar, a palette on the left with element categories, and a big empty tab area where your projects live."

**SCREEN ACTION:** Click File → New → Project. Name it "FirstSteps". Save.

**ON-SCREEN TEXT:** File → New → Project → Name it → Save

**HOST:**
> "A project holds one or more VMs. Think of a VM as a tab — a workspace. We'll make our first one in a moment."

---

## [3:30–5:30] THE VM: YOUR VISUAL CANVAS

**VISUAL:** Screen recording. Right-click in the tab area → New → VM. Name it "Circuit1". A blank canvas appears with a grid.

**HOST (to camera):**
> "This is the VM. Despite the name, it's *not* a Java Virtual Machine. It's a *visual canvas*. MyOpenLab has two kinds:"
>
> "A **Circuit VM** is where logic lives — timers, counters, comparators, math. Data flows through wires."
>
> "A **Front Panel VM** is your user interface — sliders, buttons, gauges, displays. That's what a human touches."

**ON-SCREEN DIAGRAM (simple ASCII):**
```
CIRCUIT VM (logic)              FRONT PANEL VM (UI)
[Timer] ──▶ [Counter] ──▶ [LED]     [Slider] ──▶ [Gauge]
  logic                         user touches
```

**HOST:**
> "Today we're building in a Circuit VM. Each element you drop here is a block with pins on its left (inputs) and right (outputs)."

**SCREEN ACTION:** Open the palette. Expand "Sources" → drag a "Timer" onto the canvas. Expand "Displays" → drag an "LED" onto the canvas.

---

## [5:30–7:30] ELEMENTS, PINS & PROPERTIES

**VISUAL:** Zoom in on the Timer element. Show its pins. Right-click → Properties.

**HOST (voiceover):**
> "Every element has three things: pins, properties, and code. Pins are connection points. The Timer has an output pin that emits a pulse. The LED has an input pin that turns it on or off."
>
> "Properties configure how the element behaves — the Timer's interval, the LED's color. You edit them in the properties dialog."

**SCREEN ACTION:** Timer properties → set interval to 1000 ms. LED properties → set color to red.

**ON-SCREEN KEY TERM:** Pin — input or output connection point  
**ON-SCREEN KEY TERM:** Property — configurable setting (interval, color, range…)

**HOST:**
> "Wires connect pins. Drag from the Timer's output pin to the LED's input pin. A wire appears."

**SCREEN ACTION:** Drag wire from Timer out → LED in.

---

## [7:30–9:00] RUN THE SIMULATION

**VISUAL:** Click the green Play / Run button in the toolbar.

**HOST (voiceover):**
> "Hit Run. The simulation starts. The Timer fires every second. Each time it fires, it sends a value down the wire. The LED receives it, toggles, and blinks on and off."

**SCREEN ACTION:** Show the LED blinking once per second. Then click Stop.

**HOST (to camera):**
> "That's the whole loop: element produces data → wire carries it → next element consumes it. In the next episode we'll look under the hood at exactly how that happens, because MyOpenLab doesn't push data the way you might expect."

---

## [9:00–10:00] RECAP & QUIZ

**VISUAL:** Recap slides with on-screen text.

**HOST:**
> "Quick recap: MyOpenLab is Java-based graphical programming. A project contains VMs. A VM is a canvas with elements connected by wires. Circuit VMs do logic; Front Panel VMs are your UI. Elements have pins, properties, and behavior."

**QUIZ (on-screen, 3 choices):**
> Q: What is a VM in MyOpenLab?  
> A) A Java Virtual Machine  
> B) A visual canvas for elements and wires  
> C) A compiler

**ANSWER (reveal):** B — A visual canvas. It's not a JVM.

---

## [10:00–11:00] TRY AT HOME + CTA

**HOST:**
> "Your homework: install MyOpenLab, make a project, add a Timer and an LED, wire them, and make the LED blink. Change the interval to 200 ms and watch it go faster."
>
> "If this helped, smash that like button and subscribe. Next episode: how data actually moves — pins, wires, and the event-driven process list. See you then."

**END SCREEN:** Subscribe button | Playlist: MyOpenLab Mastery | GitHub repo link

---

## Production cues for editor

| Time | Cue | Notes |
|------|-----|-------|
| 0:00 | Hook montage | LED blink + live canvas side-by-side |
| 0:45 | Key-term chips | "Element", "Wire" pop up |
| 3:30 | VM diagram | Simple ASCII or motion graphic |
| 5:30 | Zoom-ins | Timer pin, LED pin, properties dialog |
| 7:30 | Blink demo | Real-time LED toggling |
| 9:00 | Quiz card | 3-choice, auto-reveal answer B |
| 10:00 | CTA + end screen | Standard template |

**Code citations to flash on screen (optional):**  
`src/VisualLogic/VMObject.java` — the VM class  
`src/VisualLogic/Element.java` — element GUI wrapper  
`src/tools/JVSMain.java` — base class for element logic
