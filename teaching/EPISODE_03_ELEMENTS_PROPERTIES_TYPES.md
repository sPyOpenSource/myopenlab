# EPISODE 3 — Elements Deep Dive: Pins, Properties & the VSObject Family

**Target length:** 10–12 minutes  
**Concrete win:** Viewer configures an element's pins and properties, and can name the VSObject types (boolean, integer, double, string).

---

## [0:00–0:45] HOOK

**VISUAL:** Screen recording. Host drags a "Math" element onto canvas. Tries to wire its output to a "Text Display" — a red error icon appears: type mismatch.

**HOST (to camera):**
> "You wired two elements together and got an error. What went wrong? In MyOpenLab, every pin has a data type, and wires can only connect matching types. An integer pin won't accept a boolean."
>
> "Today: how to read an element's pins, how to configure its properties, and the VSObject type family so you never mismatch a wire again."

**ON-SCREEN TEXT:** Type mismatch — pins must match  
**ON-SCREEN TEXT:** VSObject family: boolean, integer, double, string

---

## [0:45–2:00] READING AN ELEMENT'S PINS

**VISUAL:** Screen recording. Drag a "Timer" and a "Counter" onto the canvas. Right-click each → "Show Pin Info" (or hover/tooltip).

**HOST (voiceover):>
> "Every element exposes its pins. Inputs on the left, outputs on the right. Each pin has an index, a direction, and a type."
>
> "Timer: one output pin, type boolean — it pulses true/false."
>
> "Counter: one input pin (boolean, the clock), one input pin (reset), one output pin (integer, the current count)."

**ON-SCREEN KEY TERM:** Pin index — 0, 1, 2… the pin's address on the element  
**ON-SCREEN KEY TERM:** Direction — PIN_INPUT (left) / PIN_OUTPUT (right)

**Citation flash:** `src/VisualLogic/ExternalIF.java:69` — PIN_INPUT / PIN_OUTPUT constants

---

## [2:00–3:30] PROPERTIES: CONFIGURING BEHAVIOR

**VISUAL:** Screen recording. Right-click Timer → Properties. Show the dialog. Change interval from 1000 to 250 ms. Click OK.

**HOST (voiceover):**
> "Properties are the element's settings. Timer's interval, Counter's start value, LED's color. You edit them in the properties dialog, and they're saved with the project."
>
> "Some properties change how the element behaves at runtime; others are just cosmetic — color, label, size. You can open the properties dialog anytime by right-clicking an element."

**SCREEN ACTION:** Right-click Counter → Properties → set "Start value" to 0, "Step" to 1.

**ON-SCREEN KEY TERM:** Property — configurable setting; edited via property dialog

---

## [3:30–5:30] THE VSOBJECT TYPE FAMILY

**VISUAL:** Graphic overlay: a family tree.

```
VSObject (base)
├── VSBoolean   true / false
├── VSInteger   whole numbers
├── VSDouble    decimals
├── VSString    text
├── VSColor     colors
├── VSImage     images
└── arrays      1D / 2D arrays of the above
```

**HOST (voiceover):**
> "All values traveling through wires are VS objects. The base class is VSObject, and it has subclasses for every type you'll use:"
>
> "VSBoolean — on/off, true/false."
>
> "VSInteger — whole numbers, like a counter."
>
> "VSDouble — decimals, like a temperature reading."
>
> "VSString — text, like a sensor label or a log message."
>
> "There are also VSColor, VSImage, and arrays — but you'll use boolean, integer, double, and string 95% of the time."

**ON-SCREEN KEY TERM:** VSObject — base of every pin value; carries the `changed` flag  
**ON-SCREEN KEY TERM:** C_* type constants — C_BOOLEAN, C_INTEGER, C_DOUBLE, C_STRING — pin type IDs

**Citation flash:** `src/VisualLogic/variables/` — VSObject and subclasses  
`src/VisualLogic/ExternalIF.java:38` — C_* type constants

---

## [5:30–7:00] TYPE MATCHING WHEN WIRING

**VISUAL:** Screen recording. Try to wire Timer's boolean output to a VSInteger-only input — show the rejection (no wire forms, or error beep). Then wire Timer → LED (boolean→boolean, OK). Wire Counter's integer output → Display's integer input (OK).

**HOST (voiceover):**
> "MyOpenLab won't let you connect mismatched types. Boolean output to integer input? Blocked. Integer to integer? Good. String to string? Good."
>
> "If you need to convert — say, turn a boolean into an integer — you use a converter element, not a bare wire. We'll show converters in a later episode."

---

## [7:00–8:30] BUILD A MINI CIRCUIT: TIMER → COUNTER → DISPLAY

**VISUAL:** Screen recording. Assemble:
1. Timer (boolean output) → Counter's clock input (boolean)
2. Counter's integer output → Display's integer input

**HOST (voiceover):**
> "Let's wire it up. Timer pulses every second. Each pulse increments the Counter. The Counter outputs an integer. We wire that to an Integer Display, which shows the running count."

**SCREEN ACTION:** Run the simulation. Watch the Display count up: 1, 2, 3, 4…

**HOST:**
> "Notice the chain: Timer's changed boolean wakes up Counter. Counter's changed integer wakes up Display. One event, cascading through typed pins."

---

## [8:30–9:30] RECAP & QUIZ

**HOST:**
> "Recap: Elements have pins (typed, indexed, input left / output right) and properties (configurable settings). All values are VS objects — boolean, integer, double, string, and more. Wires only connect matching types."

**QUIZ (on-screen):**
> Q: Which VSObject type holds whole numbers?  
> A) VSBoolean  
> B) VSInteger  
> C) VSString

**ANSWER:** B — VSInteger.

---

## [9:30–11:00] TRY AT HOME + CTA

**HOST:**
> "Homework: build Timer → Counter → Integer Display. Change the Timer interval to 100 ms and watch the counter sprint. Then swap the Display for a String Display and try to wire it — confirm the type mismatch error."
>
> "Like and subscribe. Episode 4: Front Panels — building sliders, gauges, and buttons so a human can actually control your circuit."

**END SCREEN:** Subscribe | Playlist | GitHub

---

## Production cues

| Time | Cue | Notes |
|------|-----|-------|
| 0:00 | Type mismatch error | Red error icon on wire |
| 0:45 | Pin info tooltips | Hover/right-click on Timer & Counter |
| 2:00 | Properties dialog | Timer interval, Counter start/step |
| 3:30 | VSObject family tree | Motion graphic overlay |
| 5:30 | Type-mismatch demo | Boolean → integer blocked |
| 7:00 | Mini circuit build | Timer → Counter → Display, run & count |
| 8:30 | Quiz card | Answer B |

**Code citations to flash:**  
`src/VisualLogic/ExternalIF.java:69` — PIN_INPUT / PIN_OUTPUT  
`src/VisualLogic/ExternalIF.java:38` — C_* constants  
`src/VisualLogic/variables/VSObject.java` — base class  
`src/VisualLogic/variables/VSInteger.java`, `VSBoolean.java`, etc.
