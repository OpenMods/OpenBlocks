//combined projection and view matrix
uniform mat4 u_projView;
 
void main() {
	gl_TexCoord[0]=gl_MultiTexCoord0;
	gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;
}