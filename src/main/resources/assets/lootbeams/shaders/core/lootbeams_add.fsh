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

void main() {
//    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
//    if (color.a < 0.0039) {
//        discard;
//    }
//    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);

    vec4 texColor = texture(Sampler0, texCoord0);
    float brightness = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    vec4 blendedColor = vec4(vertexColor.rgb * brightness, texColor.a * vertexColor.a * brightness);

    if (blendedColor.a < 0.0039) {
        discard;
    }

    fragColor = linear_fog(blendedColor, vertexDistance, FogStart, FogEnd, FogColor);
}
