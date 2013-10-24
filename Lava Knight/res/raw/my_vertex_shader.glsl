precision highp float;

attribute vec4 a_position;
attribute vec4 a_position2;
attribute vec4 a_normal;
attribute vec4 a_normal2;

uniform vec3 lightVec;
uniform float u_time;
uniform mat4 mvpMatrix;
uniform mat4 mvMatrix; // normal matrix

varying vec4 v_normal;
varying vec4 camPos;

void main() {
	vec4 frame;
	float x = a_position.x * u_time + a_position2.x*(1.0-u_time);
	float y = a_position.y * u_time + a_position2.y*(1.0-u_time);
	float z = a_position.z * u_time + a_position2.z*(1.0-u_time);
	frame = mvpMatrix *vec4(x, y, z, 1.0);
	camPos = vec4(frame.x, frame.y, 0.0, 1.0);
	gl_Position = frame;
	
	x = a_normal.x * u_time + a_normal2.x*(1.0-u_time);
	y = a_normal.y * u_time + a_normal2.y*(1.0-u_time);
	z = a_normal.z * u_time + a_normal2.z*(1.0-u_time);
	frame = mvMatrix * vec4(x, y, z, 1.0);
	v_normal = frame;
}