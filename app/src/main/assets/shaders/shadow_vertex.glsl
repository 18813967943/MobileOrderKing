precision highp float;
uniform mat4 uMVPMatrix;
layout(location = 0) in vec4 aShadowPosition;
void main(){
     gl_Position = uMVPMatrix * aShadowPosition;
}
