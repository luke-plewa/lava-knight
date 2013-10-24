precision mediump float;

uniform vec3 lightVec;
varying vec4 v_normal;
varying vec4 camPos;

void main() {
	vec4 L = vec4(normalize(lightVec),0.0);
    vec4 N = normalize(vec4(v_normal.x, v_normal.y, v_normal.z, 0.0));

    vec4 V = normalize(camPos);

    vec4 R = -reflect(L,N);
    R = normalize(R);

    float specular = max(dot(V,R),0.0);
    specular = pow(specular, 100.0);
    float diffuse = max(dot(N,L),0.0);
    float ambient = 0.3;
    float colorScale = diffuse+ambient+specular;
    
    vec4 vColor = vec4(colorScale, colorScale, colorScale, 1.0);
    gl_FragColor = vColor;
} 