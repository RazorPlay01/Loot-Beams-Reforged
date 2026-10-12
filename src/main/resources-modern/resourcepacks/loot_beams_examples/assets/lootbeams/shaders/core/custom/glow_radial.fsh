#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:globals.glsl>

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec2 uvCenter;
in vec2 uvSize;
in vec2 gradientBounds;
in vec4 vertexColor0;
in vec4 vertexColor1;

out vec4 fragColor;

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
