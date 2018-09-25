// Based on http://blog.shayanjaved.com/2011/03/13/shaders-android/
// from Shayan Javed
// And dEngine source from Fabien Sanglard
#version 300 es
//precision highp float;
precision mediump float;

// The position of the light in eye space.
uniform vec3 uLightPos;       	
  
// Texture variables: depth texture
uniform sampler2D uShadowTexture;

// This define the value to move one pixel left or right
uniform float uxPixelOffset;
// This define the value to move one pixel up or down
uniform float uyPixelOffset;

uniform sampler2D s_baseMap; // 正常模型或面的材质纹理通道编号
uniform float texture_flags; // 颜色使用标志
  
// from vertex shader - values get interpolated
in vec3 vPosition;
in vec4 vColor;
in vec3 vNormal;
  
// shadow coordinates
in vec4 vShadowCoord;

in vec2 texcoord0;

out vec4 outColor;

void main()                    		
{        
	vec3 lightVec = uLightPos - vPosition;
	lightVec = normalize(lightVec);
   	
   	// Phong shading with diffuse and ambient component
	float diffuseComponent = max(0.0,dot(lightVec, vNormal) );
	float ambientComponent = 0.3;

 	// Shadow
   	float shadow = 1.0;

	//if the fragment is not behind light view frustum
	if (vShadowCoord.w > 0.0) {

		 vec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;

         float distanceFromLight = texture(uShadowTexture, shadowMapPosition.st).z;

         //add bias to reduce shadow acne (error margin)
         float bias = 0.0005;

         //1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map)
         //0.0 = in shadow
		 shadow = float(distanceFromLight > shadowMapPosition.z - bias);
			
		 //scale 0.0-1.0 to 0.2-1.0
		 //otherways everything in shadow would be black
		 shadow = (shadow * 0.8) + 0.2;
	}
	// Final output color with shadow and lighting
	bool use_texture = (texture_flags==1.0);
	if(use_texture){
	  vec4 toutColor = texture(s_baseMap,texcoord0);
	   // outColor = (vColor * (diffuseComponent + ambientComponent) * shadow);
	  // outColor = toutColor*outColor;
	  outColor = toutColor*(diffuseComponent + ambientComponent)*shadow;
	}else{
       outColor = (vColor * (diffuseComponent + ambientComponent) * shadow);
    }

}                                                                     	
