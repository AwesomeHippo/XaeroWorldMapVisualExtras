#version 150

uniform sampler2D Sampler0;

uniform float Brightness;
uniform int WithLight;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    ivec2 size = textureSize(Sampler0, 0);
    vec2 texel = texCoord0 * vec2(size);
    ivec2 corner = ivec2(floor(texel - 0.5));
    // must iterate the 4 close pixels to prevent having dark line on edges when zoomed out..
    for (int i = 0; i < 4; i++) {
        if (texelFetch(Sampler0, clamp(corner + ivec2(i % 2, i / 2), ivec2(0), size - 1), 0).rgb == vec3(0.0)) {
            color = texelFetch(Sampler0, ivec2(texel), 0);
        }
    }
    if (color.rgb == vec3(0.0)) {
        discard;
    }
    float light = WithLight == 0 ? Brightness : max(color.a, Brightness);
    fragColor = vec4(color.rgb * light, 1.0);
}
