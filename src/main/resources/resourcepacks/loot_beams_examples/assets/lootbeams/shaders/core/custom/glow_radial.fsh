#version 150

uniform sampler2D Sampler0; // atlas texture
uniform sampler2D Sampler1;

uniform mat4 ProjMat;
uniform vec4 ColorModulator;
uniform float GameTime;

in vec2 texCoord0; // texture coordinate on atlas
in vec2 uvCenter; // center point of uv
in vec2 uvSize; // size of uv (for zone on atlas), x->width y->height
in vec2 gradientBounds; // gradient bounds (if specified)
in vec4 vertexColor0; // first gradient color
in vec4 vertexColor1; // second gradient color

out vec4 fragColor;

float time = GameTime * 1200.0; // 1 game cycle = 1/20 sec.

void main()
{
    float startRadius = gradientBounds.x;
    float endRadius = gradientBounds.y;
    vec2 normDiff = (texCoord0 - uvCenter) / (uvSize * 0.5);
    float dist = length(normDiff);
    float gradientDist = clamp((dist - startRadius) / (endRadius - startRadius), 0.0, 1.0);

    vec4 gradientColor = mix(vertexColor0, vertexColor1, gradientDist);

    vec2 relativeCoord = normDiff; // coord relative to uv

    fragColor = vec4(gradientColor.rgb, vertexColor0.a * (1.0 - gradientDist) * 0.5);
}
