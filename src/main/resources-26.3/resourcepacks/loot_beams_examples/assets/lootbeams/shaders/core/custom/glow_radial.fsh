#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:globals.glsl>

uniform sampler2D Sampler0;

layout(location = 0) in vec2 texCoord0;
layout(location = 1) in vec2 uvCenter;
layout(location = 2) in vec2 uvSize;
layout(location = 3) in vec2 gradientBounds;
layout(location = 4) in vec4 vertexColor0;
layout(location = 5) in vec4 vertexColor1;

layout(location = 0) out vec4 fragColor;

float time = GameTime * 1200.0;

void main() {
    float startRadius = gradientBounds.x;
    float endRadius = gradientBounds.y;
    vec2 normDiff = (texCoord0 - uvCenter) / (uvSize * 0.5);
    float dist = length(normDiff);
    float gradientDist = clamp((dist - startRadius) / (endRadius - startRadius), 0.0, 1.0);

    vec4 gradientColor = mix(vertexColor0, vertexColor1, gradientDist);

    vec2 relativeCoord = normDiff;

    fragColor = vec4(gradientColor.rgb, vertexColor0.a * (1.0 - gradientDist) * 0.5) * ColorModulator;
}
