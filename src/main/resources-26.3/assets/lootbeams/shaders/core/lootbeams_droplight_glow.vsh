#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color0;
layout(location = 3) in vec4 Color1;
layout(location = 4) in vec2 CenterUV;
layout(location = 5) in vec2 SizeUV;
layout(location = 6) in vec4 GradientBounds;

layout(location = 0) out vec2 texCoord0;
layout(location = 1) out vec2 uvCenter;
layout(location = 2) out vec2 uvSize;
layout(location = 3) out vec2 gradientBounds;
layout(location = 4) out vec4 vertexColor0;
layout(location = 5) out vec4 vertexColor1;

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
