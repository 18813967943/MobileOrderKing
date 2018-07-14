#version 300 es
uniform mat4 uMVPMatrix; // 总矩阵
uniform mat4 uMVMatrix; // 模型与视图矩阵
uniform mat4 uNormalMatrix; // 法线矩阵
uniform mat4 uShadowProjMatrix; // 阴影投影矩阵
layout(location = 0) in vec3 aPosition; // 模型顶点
layout(location = 1) in vec4 aColor; // 模型使用颜色
layout(location = 2) in vec3 aNormal; // 模型顶点法线
layout(location = 3) in vec2 a_texCoord; // 模型或区域材质贴图纹理
layout(location = 4) in vec2 uv1Coord; // 模型法线贴图纹理
layout(location = 5) in vec3 a_LightPos; // 灯光位置
out vec3 vPosition; // 顶点
out vec4 vColor; // 颜色
out vec3 vNormal; // 法线
out vec4 vShadowCoord; // 阴影纹理
out vec2 uv0; // 传入碎片化纹理0
out vec2 uv1; // 传入碎片化纹理1
out vec3 uLightPos; // 灯光位置
void main(){
     gl_Position = uMVPMatrix * vec4(aPosition,1.0);
     vPosition = vec3(uMVMatrix * vec4(aPosition,1.0));
     vColor = aColor;
     vNormal = vec3(uNormalMatrix * vec4(aNormal, 0.0));
     vShadowCoord = uShadowProjMatrix * vec4(aPosition,1.0);
     uv0 = a_texCoord;
     uv1 = uv1Coord;
     uLightPos = a_LightPos;
}

