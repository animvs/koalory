#ifdef GL_ES
    precision lowp float;
#endif

varying vec4 v_color;
varying vec2 vUV;
uniform sampler2D u_texture;

void main() {
    gl_FragColor = v_color * texture2D(u_texture, vUV);
    //gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}