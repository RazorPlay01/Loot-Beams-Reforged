#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

layout(location = 0) in vec2 texCoord0;
layout(location = 1) in vec2 uvCenter;
layout(location = 2) in vec2 uvSize;
layout(location = 3) in vec2 gradientBounds;
layout(location = 4) in vec4 vertexColor0;
layout(location = 5) in vec4 vertexColor1;

layout(location = 0) out vec4 fragColor;

void main()
{
	vec4 texColor = texture(Sampler0, texCoord0);
	float brightness = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));

	float startRadius = gradientBounds.x;
	float endRadius = gradientBounds.y;
	float dist = distance(texCoord0, uvCenter);
	float scale = uvSize.x * 0.5;
	dist = clamp((dist - startRadius * scale) / (scale * (endRadius - startRadius)), 0.0, 1.0);


	vec4 gradientColor = mix(vertexColor0, vertexColor1, dist);

	vec4 blendedColor = vec4(gradientColor.rgb * brightness, texColor.a * gradientColor.a * brightness);

	if (blendedColor.a < 0.0039) {
		discard;
	}

	fragColor = blendedColor * ColorModulator;
}
