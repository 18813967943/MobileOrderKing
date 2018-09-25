// Based on http://blog.shayanjaved.com/2011/03/13/shaders-android/
// from Shayan Javed
#version 300 es
uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
uniform mat4 uNormalMatrix;

// the shadow projection matrix
uniform mat4 uShadowProjMatrix;	

// position and normal of the vertices
layout(location = 0) in  vec4 aPosition;
layout(location = 1) in  vec4 aColor;
layout(location = 2) in  vec3 aNormal;
layout(location = 3) in  vec2 a_texCoord;

// to pass on
out vec3 vPosition;
out vec4 vColor;
out vec3 vNormal;
out vec4 vShadowCoord;
out vec2 texcoord0;

void main() {
	// the vertex position in camera space
	vPosition = vec3(uMVMatrix * aPosition); 

	// the vertex color
	vColor = aColor;
	
	// the vertex normal coordinate in camera space
	vNormal = vec3(uNormalMatrix * vec4(aNormal, 0.0));
	
	vShadowCoord = uShadowProjMatrix * aPosition;
	texcoord0 = a_texCoord;
	gl_Position = uMVPMatrix * aPosition;                     
}