#version 300 es
precision mediump float;
in vec3 vPosition; // 顶点
in vec4 vColor; // 颜色
in vec3 vNormal; // 法线
in vec4 vShadowCoord; // 阴影纹理
in vec2 uv0;
in vec2 uv1;
uniform vec3 uLightPos; // 灯光位置
uniform sampler2D uShadowTexture; // 阴影纹理通道编号
uniform sampler2D s_baseMap; // 正常模型或面的材质纹理通道编号
uniform float s_flags; // 颜色使用标志
uniform float n_flags; // 法线贴图标志
uniform float l_flags; // 光照使用标志
out vec4 outColor;
void main()
{
     bool needColor = (s_flags==1.0); // 是否只需要颜色填充
     bool hasLight = (l_flags==1.0); // 是否开启即时光影
     bool hasNormalMap = (n_flags==1.0); // 是否包含法线贴图
     if(needColor){
         outColor = vColor;
     }else{
         outColor = texture(s_baseMap, uv0);
         if(hasNormalMap){
            vec4 normalMap = texture(s_baseMap, uv1);
            outColor = outColor*normalMap + outColor*0.2;
         }
         if(hasLight){
            // 灯光至点的单位向量
            vec3 lightVec = uLightPos - vPosition;
            lightVec = normalize(lightVec);
            // 镜面光环境光
            float diffuseComponent = max(0.0,dot(lightVec, vNormal) );
            float ambientComponent = 0.3;
            // 阴影处理
            float shadow = 1.0;
            if(vShadowCoord.w > 0.0){
               vec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;
               float distanceFromLight = texture(uShadowTexture, shadowMapPosition.st).z;
               float bias = 0.0005;
               //1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map)
               //0.0 = in shadow
               shadow = float(distanceFromLight > shadowMapPosition.z - bias);
               shadow = (shadow * 0.8) + 0.2;
            }
            // 返回即时光影效果
            outColor = (outColor * (diffuseComponent + ambientComponent) * shadow);
         }
     }
}