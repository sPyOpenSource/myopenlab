# EPISODES 5–10 — Outlines

---

## EPISODE 5 — Data Types in Depth: Booleans, Numbers, Text

**Concrete win:** Viewer moves boolean, integer, double, and string values between elements and uses a converter when types differ.

**Segments:**
1. Hook: string display shows garbage because an integer was wired to it (type mismatch revisited).
2. The VSObject family refresher: VSBoolean, VSInteger, VSDouble, VSString — what each carries.
3. Demo: VSDouble temperature sensor → display with 2 decimals.
4. Demo: VSString text message → string display.
5. Converter elements: boolean→integer, integer→double, etc. Show where to find them in the palette.
6. The changed flag per type: setting the same double value twice does nothing.
7. Quiz: which type for a temperature? (VSDouble). Which for a switch? (VSBoolean).
8. Try at home: wire a random-number generator (double) → round → integer display.

**Code citations:** `src/VisualLogic/variables/VSDouble.java`, `VSString.java`, `VSBoolean.java`; `ExternalIF.java:38` C_* constants.

---

## EPISODE 6 — Clocks & Timers: Make Things Happen on Schedule

**Concrete win:** Viewer builds a blinking LED and a periodic sensor reader using `processClock()` and `jNotifyMeForClock()`.

**Segments:**
1. Hook: a circuit that should blink but only flashes once — missing clock registration.
2. Two ways to trigger: event-driven (wire arrives) vs. clock-driven (periodic).
3. `jNotifyMeForClock()` — how an element tells the VM "call me every tick."
4. `processClock()` — runs on a schedule, not on wire arrival.
5. Demo: Timer element (event-driven) vs. a clock-registered element blinking an LED every 500 ms.
6. The VM run loop: `processList` for events + `processClock()` for scheduled ticks.
7. Quiz: when does `processClock()` run? (periodically, if clock-registered).
8. Try at home: make an LED blink at 1 Hz and 10 Hz by changing the clock interval.

**Code citations:** `src/VisualLogic/VMObject.java:1685` processClock; JVSMain `jNotifyMeForClock()`; `src/VisualLogic/VMObject.java:1781` processList vs clock loop.

---

## EPISODE 7 — Build Your Own Custom Module

**Concrete win:** Viewer uses the New Java Component wizard to generate a custom element, writes `init()` and `process()`, compiles it, and drags it onto a canvas.

**Segments:**
1. Hook: no existing element does exactly what you want — time to build one.
2. The wizard: File → New → Java Component (DialogNewJavaComponentAssistent). Choose circuit/front, name, group, pins, properties.
3. What gets generated: `definition.def`, `src/YourElement.java` skeleton, `make.bat`, icon.
4. Anatomy of `definition.def`: ISDIRECTORY, classCircuit, classfront, icon, captions.
5. Anatomy of the Java class: extends JVSMain, `init()` declares pins/size/icon, `initInputPins()`/`initOutputPins()` bind VS fields, `process()` holds the logic.
6. Write logic: a "MultiplyByTwo" element — input integer, output = input × 2.
7. Compile: IDE compile button or `make.bat` (`javac -d ..\bin *.java`).
8. Use: refresh palette, drag onto canvas, wire it up.
9. Quiz: what does `definition.def` drive? (module loading/metadata).
10. Try at home: build a "Threshold" element — output true when input > setpoint.

**Code citations:** `FrameMain.java:3733` wizard entry; `definition.def` (Tools.java:641); `Element.java:653` class loading from bin; `src/tools/JVSMain.java` base class; `Tools.java:156` compile.

---

## EPISODE 8 — Debugging & Simulation

**Concrete win:** Viewer finds why a wire isn't firing using the Variable Watcher, Console, and test points.

**Segments:**
1. Hook: Circuit runs but output never changes. Silent failure.
2. Variable Watcher: open it, add pins, watch values and changed flags in real time.
3. Console output: `element.print()` / System.out style logging from element code.
4. Test points: FrmTestpoints — probe any wire mid-chain.
5. Common bugs: same value written twice (changed flag false), forgot `addToProcesslist()`, type mismatch, wrong VM type.
6. Step through: pause simulation, inspect, resume.
7. Quiz: if Display shows 0 but source is 42, first thing to check? (Variable Watcher on the wire).
8. Try at home: break a circuit on purpose (remove `addToProcesslist` equivalent), then fix it with the watcher.

**Code citations:** `src/VisualLogic/gui/DialogVariableWatcher.java`; `src/VisualLogic/gui/FrmTestpoints.java`; `VMObject.java:1843` process() as breakpoint target.

---

## EPISODE 9 — Arduino & Raspberry Pi Deployment

**Concrete win:** Viewer understands how MyOpenLab talks to hardware and what's involved in deploying to an Arduino or Pi.

**Segments:**
1. Hook: your circuit works on screen — now make it control a real LED.
2. MyOpenLab + Arduino: driver, serial connection, Firmata-like protocol, pin mapping.
3. MyOpenLab + Raspberry Pi: GPIO access, running the Java runtime on Pi, remote vs local.
4. The hardware elements in the palette: Arduino digital read/write, Pi GPIO, etc.
5. Demo: wire a button (Front Panel) → Arduino digital output → real LED on pin 13.
6. Deployment steps: connect board, select port, configure baud, upload/flash firmware if needed, run.
7. Scope note: driver internals and flashing are out of depth here — this episode covers the MyOpenLab side.
8. Quiz: what connects MyOpenLab to Arduino pins? (the Arduino driver / serial element).
9. Try at home: connect an Arduino, read a digital input in MyOpenLab, show it on a display.

**Code citations:** Hardware element sources under `distribution/Elements/`; driver-related code (out of scope detail).

---

## EPISODE 10 — Capstone: Automated Plant-Watering System

**Concrete win:** Viewer combines everything into one project: sensor read, threshold logic, UI dashboard, and hardware output.

**Segments:**
1. Project overview: read soil moisture, show on gauge, if below threshold → turn on pump (LED), manual override button.
2. Circuit VM: moisture sensor (double input) → Comparator → pump control (boolean output).
3. Front Panel: gauge shows moisture, slider sets threshold, button overrides pump, LED shows pump state.
4. Cross-VM wiring: UI slider sets the comparator threshold in real time.
5. Demo: run simulation, move moisture down, watch pump kick on.
6. Hardware note: swap the simulated sensor for an Arduino analog input (preview of Ep 9).
7. Outro: where to go next — writing modules (Ep 7 deep dive), contributing to MyOpenLab, community.
8. Final CTA: subscribe, share your capstone build, GitHub repo.

**Try at home:** Add a second pump, add a timer so the pump only runs 5 seconds even if moisture stays low.

---

## Production Checklist (every episode)

- [ ] Hook in first 45 seconds showing a live problem or result
- [ ] One concrete win stated at start
- [ ] On-screen key-term chips on first mention
- [ ] Code citation flashes (file:line) for developer credibility
- [ ] Screen recording at 1080p/60, canvas zoomed when wiring
- [ ] 3-choice quiz near minute 9 with answer reveal
- [ ] "Try at home" mini-challenge
- [ ] CTA + end screen with subscribe + playlist + GitHub
- [ ] English UI; translate German/Spanish source comments when quoted

**GitHub repo link to use in end screens:** the myopenlab repo (this working directory).
