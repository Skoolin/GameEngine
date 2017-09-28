#version 150

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in ivec3 jointIndices;
in vec3 weights;

out vec3 pass_normal;
out vec2 pass_textureCoordinates;
out vec3 toLightVector[4];
out vec3 toCameraVector;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];

uniform float useFakeLightning;

uniform float numberOfRows;
uniform vec2 offset;

void main(void){

	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

	for(int i=0;i<MAX_WEIGHTS;i++){
		vec4 localPosition = jointTransforms[jointIndices[i]] * vec4(position, 1.0);
		totalLocalPos += localPosition * weights[i];
		vec4 worldNormal = jointTransforms[jointIndices[i]] * vec4(normal, 0.0);
		totalNormal += worldNormal * weights[i];
	}

	vec4 worldPosition = transformationMatrix * totalLocalPos;

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	pass_textureCoordinates = (textureCoordinates/numberOfRows) + offset;

	vec4 actualNormal = totalNormal;
	if(useFakeLightning > 0.5) {
		actualNormal = vec4(0.0, 1.0, 0.0, 0.0);
	}

	pass_normal = (transformationMatrix * actualNormal).xyz;
	for(int i = 0; i < 4; i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
}
