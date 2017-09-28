#version 150

in vec3 pass_normal;
in vec2 pass_textureCoordinates_1;
in vec2 pass_textureCoordinates_2;
in vec3 toLightVector[4];
in vec3 toCameraVector;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform vec3 lightColour[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 attenuation[4];
uniform float blend;

void main(void){

	vec3 unitNormal = normalize(pass_normal);
	vec3 unitVectorToCamera = normalize(toCameraVector);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	for (int i = 0; i < 4; i++) {
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDot1 = dot(unitNormal, unitLightVector);
		float brightness = max(nDot1, 0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColour[i]) / attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i]) / attFactor;
	}

	totalDiffuse = max(totalDiffuse, 0.3);

	vec4 textureColor1 = texture(modelTexture, pass_textureCoordinates_1);
	vec4 textureColor2 = texture(modelTexture, pass_textureCoordinates_1);

	vec4 textureColor = mix(textureColor1, textureColor2, blend);

	out_Color = vec4(totalDiffuse, textureColor.a) * textureColor + vec4(totalSpecular, textureColor.a);
}
