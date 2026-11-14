# Design: Load circuit elements from remote GitHub repository

**Date:** 2026-11-14  
**Status:** Approved  
**Topic:** Remote element loading (replace local `distribution/Elements` with on-the-fly fetch from GitHub)

## 1. Goal

Load all elements under `distribution/Elements/` (CircuitElements, Drivers, FrontElements, etc.) from the remote GitHub repository `sPyOpenSource/myopenlab` on the fly, instead of relying on the local filesystem copy. The local `distribution/Elements` directory should no longer be a runtime dependency for element loading.

## 2. Decisions (already made)

| Decision | Choice |
|---|---|
| Scope | All elements under `distribution/Elements/` |
| Fetch mechanism | Individual files via GitHub raw URLs (`raw.githubusercontent.com`) |
| Caching | Cache-on-demand: fetch once, store locally under `.cache/remote-elements/`, serve from cache thereafter; re-fetch if missing |

Directory listings are obtained via the GitHub Contents API (`api.github.com/.../contents/...`) because raw URLs cannot list a directory. Contents API is used **only** for listings; file content comes from raw URLs.

## 3. Architecture

Insert a single indirection seam between the application and the filesystem so the rest of the app reads element files through `FileAccessService` instead of `java.io.File` directly.

- **`FileAccessService`** (new interface) — `String[] listFiles(String dir)`, `String readText(String path)`, `byte[] readBytes(String path)`, `BufferedImage readImage(String path)`, `boolean exists(String path)`.
- **`GitHubFileAccessService`** (new implementation) — serves everything from GitHub:
  - Directory listing → GitHub Contents API, returns file names + `download_url`s.
  - File content → raw.githubusercontent.com download URLs, fetched once and cached to disk at `.cache/remote-elements/<relative-path>`; served from cache thereafter; re-fetched only when missing.
  - `.class` files → `URLClassLoader` already accepts `http:`/`https:` URLs, so element classes load straight from remote (also mirrored to cache for offline reuse).
  - Icons → `ImageIO.read(URL)` instead of `Toolkit.getImage(file)`.
  - `definition.def` / `sort.def` → read as text via `readText`, parsed in memory.
- **`GitHubConfig`** (new POJO) — owner/repo/branch/base-path/cache-dir, loaded from `myopenlab.properties` with defaults pointing at sPyOpenSource/myopenlab, `master`, `distribution/Elements`, `.cache/remote-elements`.

Configuration is read once at startup from `myopenlab.properties`; all defaults keep the app working unchanged if the file is absent.

## 4. Components

### New files

| File | Responsibility |
|---|---|
| `VisualLogic/FileAccessService.java` | Interface defining file/list/text/bytes/image/exists operations. |
| `VisualLogic/GitHubFileAccessService.java` | GitHub-backed implementation: Contents API listings, raw-URL content, on-disk cache. |
| `VisualLogic/GitHubConfig.java` | Configuration POJO (owner, repo, branch, base path, cache dir) from `myopenlab.properties`. |

### Modified files (thin adapters only)

| File | Change |
|---|---|
| `VisualLogic/gui/ElementPalette.java` | `loadFolder` uses `listFiles` instead of `File.listFiles`; definition/sort properties and icons read via `readText`/`readImage`; button `filePath` becomes a logical path (`/CircuitElements/...`) rather than an absolute disk path. |
| `VisualLogic/Element.java` | `.class` loading builds `URL` objects from `FileAccessService` (HTTP URLs go straight into `URLClassLoader`); `definition.def` read via `readText`. |
| `VisualLogic/Loader.java` | `ladeClasse` gains an overload accepting a `URL` instead of a `File` (already accepts `URL[]` in `ladeClasseDriver`). |
| `VisualLogic/DriverManager.java` | Driver `.class`/`.jar` loaded via `FileAccessService.readBytes` into `URLClassLoader` (or HTTP URLs directly). |
| `VisualLogic/gui/FrameMain.java` | Constructs `GitHubFileAccessService` at startup and injects it into palettes/elements; keeps `elementPath` for backward compatibility where needed. |
| `VisualLogic/Tools.java` | `mapFile`, `getProertiesFromDefinitionFile`, `readFile` delegate logical element paths through `FileAccessService` where they serve element loading. |

### Scope note (out of scope)

User-defined elements under `2user-defined/` and element editing/creation (copy, delete, new directory) remain local-filesystem operations writing to the user's local element path — they are not remote operations.

## 5. Data Flow

### Palette open / folder browse (`ElementPalette.loadFolder`)

1. User navigates to a path such as `/CircuitElements/Analoge`.
2. `loadFolder` calls `fileAccess.listFiles(elementPath + path)`.
3. `GitHubFileAccessService` calls GitHub Contents API for `distribution/Elements/CircuitElements/Analoge` and receives file names plus `download_url`s.
4. For each entry, `definition.def` is fetched (raw URL, cached on first use) to obtain caption, icon filename, and class info.
5. Buttons are built with logical paths; icons are loaded via `readImage` (raw URL → cache → `ImageIO.read`).

### Element dropped onto canvas (`Element` → `Loader`)

1. `AddDualElement("/CircuitElements/Analoge/Generator", "bin", "Generator", ...)`.
2. `Element` reads `definition.def` via `readText` to determine `className`/`classPath`.
3. `Loader.ladeClasse` builds a `https://raw.githubusercontent.com/.../Generator.class` URL and loads the class through `URLClassLoader` (HTTP URLs are accepted natively; the bytes may also be mirrored to cache for offline reuse).
4. Instance is created via reflection; pins are initialized.

### Drivers (`DriverManager`)

Same pattern — driver `.class`/`.jar` fetched via raw URLs and loaded through `URLClassLoader`.

### Cache-on-demand

First request for any file downloads it to `.cache/remote-elements/<relative-path>`; subsequent reads hit disk. A missing/404 file makes that specific element unavailable without failing the whole palette.

## 6. Error Handling

| Situation | Behavior |
|---|---|
| Network unavailable at startup | App still launches; palette loads only cached elements (empty on first run). Non-fatal warning logged/shown; remote elements unreachable. |
| GitHub rate limit (60 unauthenticated req/hr) | Directory listings and file content cached aggressively; on 403/rate-limit, serve from cache and mark uncached elements unavailable — no crash. |
| 404 / missing file | That specific element is unavailable (omitted or grayed out in palette); rest of palette keeps working. |
| Corrupted cache entry | Detected on read (I/O exception), deleted, re-fetched from remote on next request. |
| `.class` load failure | Same as today — `Element` shows its existing "Error loading Element!" message and the element becomes a dummy. |
| Malformed `definition.def` | Parsed defensively; missing fields default to empty strings; element loads where possible. |

## 7. Testing

- **Unit tests** (existing `test/` uses JUnit): add tests for `GitHubFileAccessService` using a mock HTTP server or recorded responses — verify cache hit/miss, URL construction, Contents API parsing, and 404 handling without network.
- **Offline test:** run with network disabled; app launches and cached elements still load; uncached elements degrade gracefully.
- **Palette test:** open the element palette against a fresh (empty) cache; browse categories; confirm icons and captions appear from remote.
- **Element-in-circuit test:** drop a remote element (e.g. `Analoge/Generator`) onto the canvas, run a simulation; confirm the `.class` loads via HTTP `URLClassLoader` and the element functions.
- **Driver test:** confirm a driver loads from remote the same way.
- **Cache staleness:** delete a cached file manually; confirm it re-fetches on next palette load.

## 8. Assumptions

- GitHub `raw.githubusercontent.com` and `api.github.com` are reachable from the host running MyOpenLab.
- Public repository; no authentication required (subject to unauthenticated rate limits).
- `.class` files are valid for the JVM version running MyOpenLab (same assumption as today's local `.class` files).
- `myopenlab.properties` may be absent; defaults preserve current behavior.
