# Resources: MyOpenLab Module Architecture

## Knowledge

- [Local: `src/` source tree](src/)
  The authoritative primary source for how modules work. Use for: `Loader.java` (reflection loading), `Element.java` (wrapper), `ExternalIF.java` + `ElementIF.java` (the two-sided contract), `tools/JVSMain.java` (module base class), `SearchElement.java` + `ElementPalette.java` (palette building), `VMObject.java` (event loop / process list).
- [Local: `distribution/Elements/`](distribution/Elements/)
  The compiled module tree — every element's `definition.def`, `src/`, `bin/`. Use for: studying real module anatomy (e.g. `Extras/Starter/`).
- [Local: `distribution/Elements/Documentations/` — Spanish PDF guides](distribution/Elements/Documentations/)
  Authoritative user guides shipped with the app: *Guia de Usuario de MyOpenlab 3.0.8.4.pdf*, *Datos_MyOpenLab.pdf*, *Canvas.pdf*. Use for: vocabulary and concept grounding (Spanish).
- [GitHub: sPyOpenSource/myopenlab](https://github.com/sPyOpenSource/myopenlab)
  This repo's origin (13 stars, active). Use for: issue tracking, commit history, the `teaching/` folder.
- [GitHub: MyLibreLab/MyLibreLab](https://github.com/MyLibreLab/MyLibreLab)
  Active fork/continuation of MyOpenLab (39 stars, updated 2025). Use for: newer fixes, cross-checking architecture decisions.

## Wisdom (Communities)

- [GitHub Issues: sPyOpenSource/myopenlab](https://github.com/sPyOpenSource/myopenlab/issues)
  The project's own issue tracker — low traffic but where contributors actually talk.
- Note: `myopenlab.de` (official site, forum, wiki) is currently **down** — removed as sources. Revisit later; it may return.

## Gaps

- No English developer documentation on the module architecture exists in-repo or online that we've verified. The `teaching/` lessons are filling this gap.
