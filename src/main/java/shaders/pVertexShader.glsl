#version 150

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec3 pass_normal;
out vec2 pass_textureCoordinates_1;
out vec2 pass_textureCoordinates_2;
out vec3 toLightVector[4];
out vec3 toCameraVector;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];

uniform float useFakeLightning;

uniform float numberOfRows;
uniform vec2 offset_1;
uniform vec2 offset_2;

void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position,1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	pass_textureCoordinates_1 = (textureCoordinates/numberOfRows) + offset_1;
	pass_textureCoordinates_2 = (textureCoordinates/numberOfRows) + offset_2;


	vec3 actualNormal = normal;
	if(useFakeLightning > 0.5) {
		actualNormal = vec3(0.0, 1.0, 0.0);
	}

	pass_normal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	for(int i = 0; i < 4; i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
}

