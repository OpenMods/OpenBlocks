#version 150

uniform float uHasTexture;
uniform float uHasColor;
uniform sampler2D uDefaultTexture;
uniform vec3 uColor;

in vec2 vTexCoord;
in vec4 vColor;

void main()
{
	vec4 color = vec4(uColor, 1.0);
	color *= 1.0 - (1.0 - texture2D(uDefaultTexture, vTexCoord)) * uHasTexture;
	color *= 1.0 - (1.0 - vColor) * uHasColor;
	gl_FragColor = color;
}