#version 450 core

layout (location = 0) in vec2 pos;
layout (location = 2) in vec2 txc;
layout (location = 1) in vec3 clr;

out vec2 TexCoord;
out vec4 Color;

void main() {
#define SIZE 32
   float left     = -SIZE;
   float right    =  SIZE;
   float top      =  SIZE;
   float bottom   = -SIZE;
   float Znear    = -1;
   float Zfar     =  1;

   mat4 orthomatrix;
   orthomatrix[0].x = 2.0/(right-left);
   orthomatrix[0].y = 0;
   orthomatrix[0].z = 0;
   orthomatrix[0].w = 0;

   orthomatrix[1].x = 0;
   orthomatrix[1].y = 2.0/(top-bottom);
   orthomatrix[1].z = 0;
   orthomatrix[1].w = 0;

   orthomatrix[2].x = 0;
   orthomatrix[2].y = 0;
   orthomatrix[2].z = 2.0/(Zfar-Znear);
   orthomatrix[2].w = 0;

   orthomatrix[3].x = -(right+left)/(right-left);
   orthomatrix[3].y = -(top+bottom)/(top-bottom);
   orthomatrix[3].z = -(Zfar+Znear)/(Zfar-Znear);
   orthomatrix[3].w = 1;

   gl_Position = orthomatrix*vec4(pos, 0.0, 1.0);
   TexCoord = txc;
   Color = vec4(clr, 1.0);
};
