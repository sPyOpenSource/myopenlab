# Mission: Understand MyOpenLab's Module Architecture

## Why
I want to navigate the MyOpenLab codebase with confidence — to trace how the system loads, runs, and connects elements, so I can reason about and eventually modify the architecture.

## Success looks like
- Explain what a module (element) is made of and how `definition.def` drives loading
- Trace the path from palette click → class loading → `init()` → pin setup → runtime `process()`
- Explain the event-driven execution model (wires queue → destination `process()`) and why it differs from flow-based tools
- Read any element's source and identify its pins, lifecycle methods, and data flow

## Constraints
- Java Swing-based desktop app; source is at `src/` with a compiled copy in `distribution/Elements/`
- Event-driven architecture, not flow-based
- Localized codebase: German comments, Spanish/English/German docs
- Learning happens in the `teaching/` workspace inside the repo

## Out of scope
- Writing new elements from scratch (that's the next step after understanding)
- Arduino/Raspberry Pi driver internals
- Building GUI/dashboard apps (previous mission — see learning record 0001)
