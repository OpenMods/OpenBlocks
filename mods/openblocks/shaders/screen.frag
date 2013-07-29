uniform sampler2D u_texture;

uniform vec2 resolution;


const float brightness = 0.0;
const float contrast = 2.0;

void main() {
	vec4 texColor = texture2D(u_texture, gl_TexCoord[0].st);

	float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));

	texColor.rgb = vec3(gray * 1, gray * 0.6, gray * 0.6);
	
	// Apply contrast.
	texColor.rgb = ((texColor.rgb - 0.5) * max(contrast, 0.0)) + 0.5;
	
	// Apply brightness.
	texColor.rgb += brightness;
	
	// Return final pixel color.
  	texColor.rgb *= texColor.a;
	gl_FragColor = texColor;

}

