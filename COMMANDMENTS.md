# COMMANDMENTS

Follow it and all will be ok.

- Don not commit changes, the one do it by himself.
- Do not run an aplication, if you need check only build it, the one do it by himself.

- Use COMMANDMENTS.md to understand what everything is for.
- If a plan is implemented, give a good middle-sized commit message in MD format.

- Start all private class members with `_`.
- Start all protected class members with `m` in camelCase.
- All "user facing" properties of class (that have explicit getters and setters) must have exact name (no underscore or "m")
- No trailing semicolons.
- No ternary operators, only explicit `if` statements.
- No expression-style `if (cond) a else b` only explicit `if` statements.
- "No ternary operators, only explicit if statements" not ban Elvis operator.
- No "short" if statements, always brace-enclosed.
- Use maximum verbose lambda syntax.
- Class declaration must preambule with three lines - commented quals sign lone, class name enclosed with equals signs line end enclosing equals signs line.

- This is an application that renders a scene.
- OpenGL is used as the rendering API.
- A scenegraph is what we render here.

- A unit of the scenegraph is a map.
- Place new maps alongside the others and name them `m{next number}.kt`.
- A map is a `.kt` source file with a DSL for the scenegraph.
- Good and human-readable scenes are a good path.

- **Scene** is the root.
- **Locale** is the origin of a subgraph.
- **StateGroup** holds all OpenGL state-related things — shaders, blend, depth, stencil, and other parameters are set
  here.
- **Drawable** is a draw call.
- **Light** is for lights.
- **Obstacles** are for viewer camera and other geometry collision detection.

Every frame performs several steps:

1. Traverse to collect collision detection information.
2. Traverse to obtain light information.
3. Traverse to perform rendering.

Every step is a visitor.

- When you create or update a map, add an artful description of what it is.
- The description must be full enough that any magical entity can reproduce it.

- When you import something use "star wildcard ended" import style, do not try to import entities by name.
