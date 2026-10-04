#version 150

in vec3 Position;
in vec2 UV0;
in vec2 CenterUV;
in vec2 SizeUV;
in vec2 GradientBounds;
in vec4 Color0;
in vec4 Color1;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;

out vec2 texCoord0;
out vec2 uvCenter;
out vec2 uvSize;
out vec2 gradientBounds;
out vec4 vertexColor0;
out vec4 vertexColor1;

void main()
{
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    texCoord0 = UV0;
    uvCenter = CenterUV.xy;
    uvSize = SizeUV.xy;
    gradientBounds = GradientBounds.xy;
    vertexColor0 = Color0;
    vertexColor1 = Color1;
}
