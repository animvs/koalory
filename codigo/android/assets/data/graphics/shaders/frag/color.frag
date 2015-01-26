#ifdef GL_ES
    precision lowp float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float v_satIntensity;

void main() {
	vec4 color = v_color * texture2D(u_texture, v_texCoords);
	float saturation = (color.r + color.g + color.b) / 3.0;
	
	/*color.r = saturation * (1.0 - intensity);
	color.g = saturation * (1.0 - intensity);
	color.b = saturation * (1.0 - intensity);*/

	color = mix(vec4(saturation, saturation, saturation, color.a), color, v_satIntensity);
	
    gl_FragColor = color;
}
