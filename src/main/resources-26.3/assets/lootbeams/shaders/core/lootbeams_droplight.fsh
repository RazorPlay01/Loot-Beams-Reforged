#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

layout(location = 0) in vec2 texCoord0;
layout(location = 1) in vec4 vertexColor0;
layout(location = 2) in vec4 vertexColor1;
layout(location = 3) in float alphaMultiplier;

layout(location = 0) out vec4 fragColor;

void main()
{
	vec4 color = texture(Sampler0, texCoord0);
	color *= ColorModulator;
	color.rgb = (color.a * vertexColor0.rgb) + (1.0 - color.a) * vertexColor1.rgb;
	color.a *= vertexColor0.a * vertexColor1.a * alphaMultiplier;
	fragColor = color;
}
