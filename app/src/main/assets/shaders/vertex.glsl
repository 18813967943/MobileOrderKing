precision mediump float;
// 输入数据
uniform mat4 uMVPMatrix; // 总矩阵
uniform mat4 uMVMatrix; // 模型与视图矩阵
uniform mat4 uNormalMatrix; // 法线矩阵

uniform mat4 uShadowProjMatrix; // 阴影投影矩阵

layout(location = 0) in vec4 aPosition; // 模型顶点
layout(location = 1) in vec4 aColor; // 模型使用颜色
layout(location = 2) in vec3 aNormal; // 模型顶点法线
layout(location = 3) in vec2 a_fmap; // 模型或区域材质贴图纹理
layout(location = 4) in vec2 a_smap; // 模型法线贴图纹理

// 输出数据
out vec3 vPosition; // 顶点
out vec4 vColor; // 颜色
out vec3 vNormal; // 法线
out vec4 vShadowCoord; // 阴影纹理
out vec2 fmap;
out vec2 smap;

void main(){
     vPosition = vec3(uMVMatrix * aPosition);
     vColor = aColor;
     fmap = a_fmap;
     smap = a_smap;
     vNormal = vec3(uNormalMatrix * vec4(aNormal, 0.0));
     vShadowCoord = uShadowProjMatrix * aPosition;
     gl_Position = uMVPMatrix * aPosition;
}

