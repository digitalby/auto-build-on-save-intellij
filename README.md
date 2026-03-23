# Auto Build on Save

Automatically triggers an incremental project build every time you save a file in IntelliJ IDEA or Android Studio.

## Features

- Runs `Make Project` (incremental) on every file save
- Optional error guard: skips the build if the IDE reports linter errors
- Configurable debounce delay to avoid redundant builds on rapid saves

## Installation

`Settings / Preferences` > `Plugins` > `Marketplace` > search **Auto Build on Save** > Install.

## Configuration

`Settings / Preferences` > `Tools` > `Auto Build on Save`

| Setting | Default | Description |
|---|---|---|
| Enable | On | Master switch |
| Only build with zero linter errors | On | Skip build when IDE reports errors |
| Debounce delay (ms) | 1500 | Minimum gap between builds |

## Compatibility

IntelliJ IDEA / Android Studio builds 241 (2024.1) through 251 (2025.1). Requires the Java plugin.

## License

[MIT](LICENSE) - Yury Vashchylau / [yuryv.info](https://yuryv.info)
