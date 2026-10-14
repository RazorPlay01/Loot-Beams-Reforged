#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:fog.glsl>
#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>

uniform sampler2D Sampler0;

layout(location = 0) in float sphericalVertexDistance;
layout(location = 1) in float cylindricalVertexDistance;
layout(location = 2) in vec2 texCoord0;
layout(location = 3) in vec4 vertexColor;

layout(location = 0) out vec4 fragColor;

vec4 overlay(vec4 base, vec4 blend) {
    float brightness = dot(base.rgb, vec3(0.299, 0.587, 0.114));
    vec3 condition = step(0.5, base.rgb);

    vec3 resultColor = mix(
        2.0 * base.rgb * blend.rgb,
        1.0 - 2.0 * (1.0 - base.rgb) * (1.0 - blend.rgb),
        condition
    );

    return vec4(resultColor, base.a * blend.a * brightness);
}

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0);
    vec4 color = overlay(baseColor, vertexColor);

    if (color.a < 0.0039) {
        discard;
    }

    fragColor = apply_fog(
        color * ColorModulator,
        sphericalVertexDistance,
        cylindricalVertexDistance,
        FogEnvironmentalStart,
        FogEnvironmentalEnd,
        FogRenderDistanceStart,
        FogRenderDistanceEnd,
        FogColor
    );
}
