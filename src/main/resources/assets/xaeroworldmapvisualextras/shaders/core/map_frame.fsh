#version 150

uniform sampler2D Sampler0;

uniform vec3 Tone;
uniform float Strength;
uniform float Shadow;
uniform float ShadowOpacity;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    color.rgb = mix(color.rgb, gray + Tone * min(gray * 4.0, color.a), Strength);
    vec2 shadowOffset = Shadow * (dFdy(texCoord0) - dFdx(texCoord0));
    float alpha = max(color.a, texture(Sampler0, texCoord0 + shadowOffset).a * ShadowOpacity);
    if (alpha == 0.0) {
        discard;
    }
    fragColor = vec4(color.rgb / alpha, alpha);
}
