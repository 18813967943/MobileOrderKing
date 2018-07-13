precision mediump float;
uniform vec3 uLightPos; // 灯光位置
uniform sampler2D uShadowTexture; // 阴影纹理材质

uniform sampler2D s_baseMap; // 贴图编号
uniform float s_flags; // 颜色使用标志
uniform float n_flags; // 法线贴图标志
uniform float l_flags; // 光照使用标志

in vec3 vPosition; // 顶点
in vec4 vColor; // 颜色
in vec3 vNormal; // 法线

int vec2 fmap; // 模型或铺砖面材质纹理
int vec2 smap; // 模型法线贴图材质纹理

out vec4 outColor; // 总输出效果结果

// 基础阴影
float shadowSimple()
{
	vec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;
	float distanceFromLight = texture2D(uShadowTexture, shadowMapPosition.st).z;
	//add bias to reduce shadow acne (error margin)
	float bias = 0.0005;
	//1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map)
	//0.0 = in shadow
	return float(distanceFromLight > shadowMapPosition.z - bias);
}

void main(){
     // 仅使用颜色着色
     bool needColor = (s_flags==1.0);
     bool hasLight = (l_flags==1.0);
     if(needColor){
        outColor = vColor;
     }
     // 使用贴图着色
     else{
        // 计算贴图材质
        vec4 tempOutColor = texture(s_baseMap, fmap);
        bool needUV1 = (n_flags==1.0);
        if(needUV1){
          	vec4 normalMap = texture(s_baseMap, smap);
          	tempOutColor = tempOutColor*normalMap + tempOutColor*0.2;
        }
        // 使用光线着色
        if(hasLight){
           // 光线单位向量
           vec3 lightVec = uLightPos - vPosition;
           lightVec = normalize(lightVec);
           // 镜面光、环境光着色
           float diffuseComponent = max(0.0,dot(lightVec, vNormal) );
           float ambientComponent = 0.3;
           // 阴影数值控制标志
           float shadow = 1.0;
           if (vShadowCoord.w > 0.0) {
                shadow = shadowSimple();
                //scale 0.0-1.0 to 0.2-1.0
                //otherways everything in shadow would be black
                shadow = (shadow * 0.8) + 0.2;
           }
           // 输出光栅化结果
           outColor = (tempOutColor * (diffuseComponent + ambientComponent) * shadow);
        }
        // 不使用光线着色，返回正常贴图
        else{
           outColor = tempOutColor;
        }
    }
}