precision highp float;
attribute vec4 a_position;
attribute vec4 a_normal;

uniform vec3 lightVec;
uniform vec3 uColor;
uniform mat4 mvpMatrix;
uniform mat4 mvMatrix;
attribute vec2 a_TexCoordinate;

varying vec4 v_normal;
varying vec4 camPos;
varying vec2 v_TexCoordinate; // Interpolated texture coordinate per fragment.

void main() {
	vec4 frame;
	frame = mvpMatrix *a_position;
	camPos = vec4(a_position.x, a_position.y, 0.0, 1.0);
	gl_Position = frame;
	
	frame = mvMatrix * vec4(a_normal.x, a_normal.y, a_normal.z, 1.0);
	v_normal = frame;
	
	v_TexCoordinate = a_TexCoordinate;
}