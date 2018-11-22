#version 300 es
precision highp float;
in vec4 vPosition;
out vec4 outColor;
void main() {
	// the depth
	float normalizedDistance  = vPosition.z / vPosition.w;
	// scale -1.0;1.0 to 0.0;1.0 
	normalizedDistance = (normalizedDistance + 1.0) / 2.0;
	// pack value into 32-bit RGBA texture
	const vec4 bitSh = vec4(256.0 * 256.0 * 256.0,
    							256.0 * 256.0,
    							256.0,
    							1.0);
    const vec4 bitMsk = vec4(0,
    							1.0 / 256.0,
    							1.0 / 256.0,
    							1.0 / 256.0);
    vec4 comp = fract(normalizedDistance * bitSh);
    comp -= comp.xxyz * bitMsk;
	outColor = comp;
}