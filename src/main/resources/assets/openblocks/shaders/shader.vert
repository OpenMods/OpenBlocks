#version 150 compatibility

in vec3 aVertex;
in vec2 aTexCoord;
in vec4 aColor;
in vec3 aPosition;

uniform float uScale;

out vec2 vTexCoord;
out vec4 vColor;

const vec3 offset = vec3(0.5, 0, 0.5);

void main()
{
	vTexCoord = aTexCoord;
	vColor = aColor;
	gl_Position = gl_ModelViewProjectionMatrix * vec4(aVertex * uScale + aPosition + offset, 1);
}