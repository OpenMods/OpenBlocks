#version 150

uniform float uHasTexture;
uniform float uHasBrightness;
uniform float uHasColor;
uniform sampler2D uDefaultTexture;
uniform sampler2D uLightmapTexture;
uniform vec3 uColor;

in vec2 vTexCoord;
in vec4 vColor;
in vec2 vBrightnessCoord;

#define SCALE 1.0/256.0
#define TRANSLATE 8.0

mat2 tex = mat2(SCALE + TRANSLATE, 0, SCALE + TRANSLATE, 0);

void main()
{
	vec4 color = vec4(uColor, 1.0);
	color *= 1.0 - (1.0 - texture2D(uDefaultTexture, vTexCoord)) * uHasTexture;
	color *= 1.0 - (1.0 - vColor) * uHasColor;
//	color *= vec4(1.0 - (1.0 - texture2D(uLightmapTexture, vec2(0.0, 1.0))).xyz) * uHasBrightness, 1.0);
	gl_FragColor = color;
}