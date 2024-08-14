# Changelog
[get-rich](https://github.com/paintparty/get-rich): Helps you quickly get rich text into your console printing. 



For a list of breaking changes, check [here](#breaking-changes)


## Unreleased
#### Added
- `get-rich.core/stack-trace-preview`
- `get-rich.core/safe-println`
- `get-rich.core/?sgr` and `get-rich.core/!?sgr`, for debugging.
- `:data?` and `:padding-left?` option to `get-rich.core/callout`.

#### Changed
- `:border-weight` option to `get-rich.core/callout` now expects one of the following:
  - `:thin` (default)
  - `:medium`
  - `:heavy`

#### Fixed
- 2-arity version of `?trace` macro false warnings.

#### Removed
- Removed :purple from built-in pallette.

<br>
<br>

## 0.2.0
2024-07-24

### Initial Release

<br>
<br>

## Breaking changes

### 0.3.0

