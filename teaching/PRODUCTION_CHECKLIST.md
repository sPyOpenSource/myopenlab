# YouTube Production Checklist — MyOpenLab Series

## Before filming (per episode)
- [ ] Script finalized (this package)
- [ ] MyOpenLab freshly installed on recording machine (clean palette, no demo projects open)
- [ ] Project files ready for each demo (start state + end state)
- [ ] Screen resolution set to 1920×1080, desktop icons hidden
- [ ] Microphone tested; talking-head lighting ready (if using camera)
- [ ] Code citations verified against local source (src/VisualLogic/, src/tools/)

## During recording
- [ ] Hook filmed first (or recorded separately for best energy)
- [ ] Screen recording at 1080p60; canvas zoomed during wiring
- [ ] Mouse cursor highlighted (e.g., cursor spotlight)
- [ ] Key terms spoken clearly right when the on-screen chip appears
- [ ] Code snippets shown verbatim from local source (no typos)
- [ ] "Try at home" challenge demonstrated end-to-end

## After recording (edit)
- [ ] Intro animation (wires → MyOpenLab logo)
- [ ] Lower third: "MyOpenLab Mastery — Ep. N: Title"
- [ ] On-screen key-term chips (VM, Element, Pin, processList, process(), VSObject, changed flag, Front Panel, definition.def, JVSMain)
- [ ] Code block overlays with syntax highlighting
- [ ] Quiz card (3 choices, auto-reveal correct answer + 1-line explanation)
- [ ] Recap slide with bullet summary
- [ ] CTA + end screen: Subscribe | Playlist | GitHub repo
- [ ] Chapter markers for YouTube timestamps

## Publish
- [ ] Title: "Ep. N: Title — MyOpenLab Mastery"
- [ ] Description: summary + timestamps + GitHub link + related episode links
- [ ] Thumbnail: host face + circuit screenshot + big episode number
- [ ] Playlist: "MyOpenLab Mastery"
- [ ] Community post: "Try at home" challenge reminder

## Source references used across the series
- `src/VisualLogic/VMObject.java` — VM, processList (1781), process() (1843), processClock (1685)
- `src/VisualLogic/Element.java` — element GUI wrapper, class loading (653)
- `src/VisualLogic/ExternalIF.java` — PIN_INPUT/PIN_OUTPUT (69), C_* constants (38)
- `src/VisualLogic/variables/VSObject.java` — changed flag (57)
- `src/tools/JVSMain.java` — base class for module logic
- `src/VisualLogic/gui/DialogVariableWatcher.java` — debugging
- `src/VisualLogic/gui/FrmTestpoints.java` — test points
- `FrameMain.java:3733` — New Java Component wizard
- `definition.def` — module metadata (Tools.java:641)
- `Tools.java:156` — compile
