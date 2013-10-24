precision highp float;

attribute vec4 a_position;
attribute vec4 a_normal;

uniform vec3 lightVec;
uniform vec3 uColor;
uniform mat4 mvpMatrix;
uniform mat4 mvMatrix; // normal matrix

varying vec4 v_normal;
varying vec4 camPos;

void main() {
	vec4 frame;
	frame = mvMatrix * vec4(a_normal.x, a_normal.y, a_normal.z, 1.0);
	v_normal = frame;
	
	frame = mvpMatrix *a_position;
	camPos = vec4(frame.x, frame.y, 0.0, 1.0);
	gl_Position = frame;
}