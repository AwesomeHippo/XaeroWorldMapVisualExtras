#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec3 Tone;
uniform float Strength;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    color.rgb = mix(color.rgb, gray + Tone * min(gray * 4.0, color.a), Strength);
    fragColor = color * ColorModulator;
    if (fragColor.a == 0.0) {
        discard;
    }
}
