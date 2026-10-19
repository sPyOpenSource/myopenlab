# EPISODE 4 — Front Panels: Build a User Interface

**Target length:** 10–12 minutes  
**Concrete win:** Viewer builds a Front Panel VM with a slider controlling a gauge and a button toggling an LED, then wires it to a Circuit VM.

---

## [0:00–0:45] HOOK

**VISUAL:** Screen recording. A finished dashboard: a slider moves, a gauge needle sweeps, a button clicks and an LED lights up — all live, no code written by the viewer yet.

**HOST (to camera):**
> "So far everything's happened inside a Circuit VM — invisible logic. But real control systems need a human interface: knobs, buttons, readouts. That's the Front Panel VM."
>
> "Today: build your first UI. A slider, a gauge, a button, an LED. Wire them together. And connect your Front Panel to a Circuit VM so the UI can drive real logic."

**ON-SCREEN TEXT:** Front Panel VM = your user interface  
**ON-SCREEN TEXT:** Sliders, buttons, gauges, LEDs, displays

---

## [0:45–1:45] CIRCUIT VS FRONT PANEL — RECAP

**VISUAL:** Side-by-side canvases. Left: Circuit VM with Timer → Counter → LED. Right: Front Panel VM with Slider → Gauge.

**HOST (voiceover):**
> "A project can hold many VMs. Use Circuit VMs for logic that runs automatically. Use Front Panel VMs for widgets a person touches."
>
> "Front Panel elements are GUI components: sliders, buttons, text fields, combo boxes for input; displays, LEDs, gauges, graphs for output."

**ON-SCREEN KEY TERM:** Front Panel VM — UI canvas with interactive widgets

---

## [1:45–3:00] CREATE A FRONT PANEL

**VISUAL:** Screen recording. Right-click tab area → New → VM → choose "Front Panel". Name it "Dashboard". Blank white canvas appears.

**HOST (voiceover):**
> "Create a new VM and pick Front Panel. The canvas is white — this is your design surface. Now open the palette and look for 'Front Panel' or 'Controls'."

**SCREEN ACTION:** Drag a "Slider" onto the canvas. Drag a "Gauge" next to it. Drag a "Button" and an "LED".

**ON-SCREEN KEY TERM:** Front Panel elements — GUI widgets users interact with directly

---

## [3:00–4:30] CONFIGURE THE WIDGETS

**VISUAL:** Right-click Slider → Properties. Set min 0, max 100, default 50. Right-click Gauge → Properties. Set range 0–100. Right-click LED → Properties. Color red.

**HOST (voiceover):**
> "Configure each widget. The slider needs a min and max — here 0 to 100. The gauge matches so the needle sweeps the same range. The LED just needs a color."

**SCREEN ACTION:** Move the slider with the mouse. Watch the gauge needle move live (they're already linked visually in some configurations — if not, we wire them next).

---

## [4:30–6:00] WIRE SLIDER → GAUGE

**VISUAL:** Drag wire from Slider's output pin → Gauge's input pin. Both are integer type.

**HOST (voiceover):**
> "Wire the slider's output to the gauge's input. Same type — integer — so the wire forms. Now move the slider. The gauge follows, in real time."

**SCREEN ACTION:** Slide back and forth. Gauge needle sweeps.

**HOST:**
> "That's a complete UI loop with zero Java code. The slider is an input widget; the gauge is an output widget. The wire carries the value."

---

## [6:00–7:30] BUTTON → LED (DIGITAL IO)

**VISUAL:** Wire Button's output (boolean) → LED's input (boolean).

**HOST (voiceover):**
> "Now digital: button outputs boolean, LED takes boolean. Wire them. Click the button — the LED turns on. Click again — off."

**SCREEN ACTION:** Click button. LED toggles on/off.

**HOST:**
> "Same event-driven model: button click changes the output pin → changed flag → addToProcesslist → LED.process() runs → LED lights up."

---

## [7:30–8:45] CONNECT FRONT PANEL TO CIRCUIT LOGIC

**VISual:** Split canvas: Front Panel on left, Circuit VM on right. Wire the Front Panel slider's output → a Circuit VM integer input → Multiply by 2 → Display.

**HOST (voiceover):**
> "Here's the power move: wire a Front Panel widget to a Circuit VM element. The slider becomes a real control input for your logic."
>
> "Move the slider. The Circuit VM receives the new integer, processes it, and updates the display. Your UI now drives live logic."

**SCREEN ACTION:** Slider at 50 → Display shows 100. Slider at 80 → Display shows 160.

**ON-SCREEN KEY TERM:** Cross-VM wiring — Front Panel widget controlling Circuit VM logic

---

## [8:45–9:30] RECAP & QUIZ

**HOST:**
> "Recap: Front Panel VMs hold GUI widgets. Sliders, buttons = input. Gauges, LEDs, displays = output. Wire them by matching types. And you can wire a Front Panel widget into a Circuit VM so the user controls real logic."

**QUIZ (on-screen):**
> Q: What is a Front Panel VM used for?  
> A) Background logic only  
> B) The user interface — sliders, buttons, gauges  
> C) Compiling Java code

**ANSWER:** B — the user interface.

---

## [9:30–11:00] TRY AT HOME + CTA

**HOST:**
> "Homework: make a Dashboard Front Panel. Add a slider (0–100), a gauge, a button, and an LED. Wire slider→gauge and button→LED. Then create a Circuit VM, wire the slider into a math element, and show the result on a display."
>
> "Like and subscribe. Episode 5: data types in depth — moving numbers, text, and booleans between elements."

**END SCREEN:** Subscribe | Playlist | GitHub

---

## Production cues

| Time | Cue | Notes |
|------|-----|-------|
| 0:00 | Finished dashboard | Slider/gauge/button/LED live |
| 0:45 | Circuit vs Front Panel | Side-by-side canvases |
| 1:45 | New VM dialog | Choose "Front Panel" |
| 3:00 | Properties dialogs | Slider range, gauge range, LED color |
| 4:30 | Slider→Gauge wire | Live needle sweep |
| 6:00 | Button→LED wire | Toggle demo |
| 7:30 | Cross-VM wiring | Slider drives Circuit VM math |
| 8:45 | Quiz card | Answer B |

**Code citations to flash:**  
`src/VisualLogic/VMObject.java` — VM can be circuit or front panel  
`src/VisualLogic/Element.java` — element GUI wrapper (JPanel)
