COMMANDMENTS, follow it and all will be ok.

What is this - it is application that renders a scene. 
OpenGL is a as rendering api.
Scenegraph is what we render here.
A unit of scene graph - is a map.
Place new maps alongside with others and name it "m{next number}.kt"
A map is kt source file with DSL for scenegraph. 
Good and human readable scenes is a good path.

Follow scenegraph rules:
    Scene is a root.
    Locale is origin of a subgraph.
    StateGroup holds all OpenGL state related things - shaders, blend, depth, stencil and other parameters set here.
    Drawable is draw call.
    Light for lights.
    Obstacles for viewer camera and other geometry collision detection.

Every frame performed several steps:
    Traverse for collect a collision detection inforamtion.
    Traverse for obtain light information.
    Traverse to perform render.
    Every step is a visitor.

When you create or update a map - add artful description of what it is.
Description must be full enought to every magical entity can reproduce it.

