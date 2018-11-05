#version 300 es
uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
uniform mat4 uNormalMatrix;
// the shadow projection matrix
uniform mat4 uShadowProjMatrix;
// position and normal of the vertices
layout(location = 0) in vec4 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec3 aNormal;
layout(location = 3) in vec2 aTexcoord;

// to pass on
out vec3 vPosition;
out vec4 vColor;
out vec3 vNormal;
out vec2 vTexcoord;
out vec4 vShadowCoord;

void main() {
	// the vertex position in camera space
	vPosition = vec3(uMVMatrix * aPosition); 

	// the vertex color
	vColor = aColor;
	
	// the vertex normal coordinate in camera space
	vNormal = vec3(uNormalMatrix * vec4(aNormal, 0.0));
	vTexcoord = aTexcoord;
	vShadowCoord = uShadowProjMatrix * aPosition;

	gl_Position = uMVPMatrix * aPosition;                     
}