#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

vec4 overlay(vec4 base, vec4 blend) {
    float brightness = dot(base.rgb, vec3(0.299, 0.587, 0.114));
    vec3 condition = step(0.5, base.rgb);

    vec3 resultColor = mix(
        2.0 * base.rgb * blend.rgb,
        1.0 - 2.0 * (1.0 - base.rgb) * (1.0 - blend.rgb),
        condition
    );

    return vec4(resultColor, base.a);
}

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0);
    vec4 color = overlay(baseColor, vertexColor);

    if (color.a < 0.0039) {
        discard;
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
