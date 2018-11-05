#version 300 es
precision highp float;
uniform mat4 uMVPMatrix;
layout(location = 0) in vec4 aShadowPosition;
out vec4 vPosition;
void main() {
	vPosition = uMVPMatrix * aShadowPosition;
	gl_Position = uMVPMatrix * aShadowPosition; 
}