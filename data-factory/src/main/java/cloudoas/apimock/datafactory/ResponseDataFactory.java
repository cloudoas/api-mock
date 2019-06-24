package cloudoas.apimock.datafactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cloudoas.apimock.common.FileInfo;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@SuppressWarnings("rawtypes")
public class ResponseDataFactory {
	private OpenAPI openAPI = null;;
	
	public OpenAPI load(String specName) throws Exception {
		File specFile = new File(specName);
		
		String ext = FileInfo.getExtension(specFile);
		if (StringUtils.equalsIgnoreCase(ext, FileInfo.JSON_EXT)) {
			openAPI = Json.mapper().readValue(specFile, OpenAPI.class);
		}else if (StringUtils.equalsIgnoreCase(ext, FileInfo.YML_EXT)||StringUtils.equalsIgnoreCase(ext, FileInfo.YAML_EXT)) {
			openAPI = Yaml.mapper().readValue(specFile, OpenAPI.class);
		}else {
			throw new UnsupportedOperationException("Unknow file format " + ext);
		}
		
		
		return openAPI;
	}
	
	public void makeData() {
		Paths paths = openAPI.getPaths();
		
		
		paths.forEach((key, pathItem)->{
			System.out.println("path:" + key);
			
			Map<HttpMethod, Operation> operations = pathItem.readOperationsMap();
			
			operations.forEach((method, operation) -> handleOperation(method, operation));
			
		});		
	}
	
	private Map<String, Object> handleOperation(HttpMethod method, Operation operation) {
		// method?
		Map<String, Object> mockResponses= new HashMap<>();
		
		ApiResponses responses = operation.getResponses();
		
		responses.forEach((name, response)->mockResponses.put(name,handleResponse(response)));
		
		System.out.println(mockResponses);
		
		return mockResponses;
	}
	
	private Map<String, Object> handleResponse(ApiResponse response) {
		Map<String, Object> mockResponse = new HashMap<>();
	
		Content content = response.getContent();
		
		if (null!=content) {
			content.forEach((nm, mediaType)->mockResponse.put(nm, handleSchema(mediaType)));
		}
		
		return mockResponse;
	}
	
	private Object handleSchema(MediaType mediaType) {
		Object example = mediaType.getExample();
		
		if (null!=example) {
			return example;
		}
		
		Map<String, Example> examples = mediaType.getExamples();
		
		
		if (null!=examples && !examples.isEmpty()) {
			return examples.entrySet().iterator().next();
		}
		
		
		return generateMockData(mediaType.getSchema());
	}
	
	private JsonNode generateMockData(Schema schema) {
		Schema expandedSchema = expandSchema(schema);
		Class schemaType = expandedSchema.getClass();
		
		if (ArraySchema.class.equals(schemaType)) {
			return generateMockArray((ArraySchema)expandedSchema);
		}
		
		if (ObjectSchema.class.equals(schemaType)) {
			return generateMockObject((ObjectSchema)expandedSchema);
		}
		
		if (IntegerSchema.class.equals(schemaType)) {
			return generateMockInteger((IntegerSchema)expandedSchema);
		}
		
		if (StringSchema.class.equals(schemaType)) {
			return generateMockString((StringSchema)expandedSchema);
		}
		
		return null;
	}	
	
	private JsonNode generateMockArray(ArraySchema schema) {
		Schema<?> itemSchema = expandSchema(schema.getItems());
		
		ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
		
		arrayNode.add(generateMockData(itemSchema));
		
		return arrayNode;
	}
	
	private JsonNode generateMockObject(ObjectSchema schema) {
		Map<String, Schema> properties = schema.getProperties();
		ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
		
		properties.forEach((name, s)->objectNode.set(name, generateMockData(s)));
		
		return objectNode;
	}
	
	private JsonNode generateMockInteger(IntegerSchema schema) {
		int randomInt = ThreadLocalRandom.current().nextInt(0, 10);
		return JsonNodeFactory.instance.numberNode(randomInt);
	}
	
	private JsonNode generateMockString(StringSchema schema) {
		int randomInt = ThreadLocalRandom.current().nextInt(3, 10);
		String randomString = RandomStringUtils.randomAlphabetic(randomInt);
		return JsonNodeFactory.instance.textNode(randomString);
	}	
	
	public Schema expandSchema(Schema schema) {
		Schema expandedSchema = schema;
		String ref = schema.get$ref();
		
		if (StringUtils.isNotBlank(ref)) {
			expandedSchema = (Schema) resolveRef(ref);
		}		
		
		return expandedSchema;
	}
	
	public Object resolveRef(String ref) {
		if (ref.startsWith(Constants.REF_SECTION_PREFIX)) {
			if (!ref.startsWith(Constants.REF_COMPONENTS)) {
				throw new UnknownError("Unknow ref format. ref="+ref);
			}
			
			String[] refParts = ref.split(Constants.REF_PATH_SEPARATOR);
			
			if (refParts.length != Constants.REF_LENGTH) {
				throw new UnknownError("Cannot parse ref. ref="+ref);
			}
			
			return resolveRef(refParts[2], refParts[3]);			
		}else {
			// handle cross file ref
		}
		
		return null;
	}
	
	public Object resolveRef(String category, String name) {
		switch(category) {
		case Constants.REF_COMPONENTS_SCHEMAS: 
			Map<String, Schema> schemas = this.openAPI.getComponents().getSchemas();
			if (null!=schemas) {
				return schemas.get(name);
			}
		
		default:
		}
		
		return null;
	}
}
