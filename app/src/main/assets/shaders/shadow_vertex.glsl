#version 300 es
uniform mat4 uMVPMatrix;
layout(location = 0) in vec3 aShadowPosition;
void main(){
     gl_Position = uMVPMatrix * vec4(aShadowPosition,1.0);
}
