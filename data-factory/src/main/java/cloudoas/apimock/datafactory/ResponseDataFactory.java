package cloudoas.apimock.datafactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cloudoas.apimock.common.file.FileInfo;
import cloudoas.apimock.common.file.Format;
import cloudoas.apimock.datafactory.model.APIData;
import cloudoas.apimock.datafactory.model.OperationData;
import cloudoas.apimock.datafactory.model.PathData;
import cloudoas.apimock.datafactory.model.ResponseData;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
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
	private String source = null;
	private OpenAPI openAPI = null;;
	
	public OpenAPI loadSpec(File specFile) throws Exception {
		String ext = FileInfo.getExtension(specFile);
		if (StringUtils.equalsIgnoreCase(ext, Format.JSON.name())) {
			openAPI = Json.mapper().readValue(specFile, OpenAPI.class);
		}else if (StringUtils.equalsIgnoreCase(ext, Format.YML.name())||StringUtils.equalsIgnoreCase(ext, Format.YAML.name())) {
			openAPI = Yaml.mapper().readValue(specFile, OpenAPI.class);
		}else {
			throw new UnsupportedOperationException("Unknow file format " + ext);
		}
		
		this.source=specFile.getName();
		
		return openAPI;
	}
	
	public OpenAPI loadSpec(String specContent, String format) throws Exception{
		if (StringUtils.equalsIgnoreCase(format, Format.JSON.name())) {
			openAPI = Json.mapper().readValue(specContent, OpenAPI.class);
		}else if (StringUtils.equalsIgnoreCase(format, Format.YML.name())||StringUtils.equalsIgnoreCase(format, Format.YAML.name())) {
			openAPI = Yaml.mapper().readValue(specContent, OpenAPI.class);
		}else {
			throw new UnsupportedOperationException("Unknow format " + format);
		}
		
		this.source=null;
		
		return openAPI;		
	}
	
	public APIData makeData() {
		Paths paths = openAPI.getPaths();
		APIData apiData = new APIData();
		apiData.setSpecName(getSpecName());
		apiData.setVersion(getVersion());
		
		paths.forEach((path, pathItem)->{
			PathData pathData = new PathData();
			apiData.addPathData(path, pathData);
			
			Map<HttpMethod, Operation> operations = pathItem.readOperationsMap();
			
			operations.forEach((method, operation) -> pathData.add(method, handleOperation(operation)));
			
		});	
		
		return apiData;
	}
	
	protected String getSpecName() {
		String title = null;
		Info info = openAPI.getInfo();
		
		if (null!=info) {
			title = info.getTitle();
		}
		
		if (StringUtils.isNotBlank(title)) {
			return title;
		}
		
		if (StringUtils.isNotBlank(this.source)) {
			return FileInfo.getName(new File(this.source));
		}
		
		return null;
	}
	
	protected String getVersion() {
		String version = null;
		Info info = openAPI.getInfo();
		
		if (null!=info) {
			version = info.getVersion();
		}
		
		return StringUtils.trimToEmpty(version);
	}
	
	protected OperationData handleOperation(Operation operation) {
		OperationData operationData = new OperationData();
		
		ApiResponses responses = operation.getResponses();
		
		responses.forEach((name, response)->operationData.add(name, handleResponse(response)));
		
		return operationData;
	}
	
	protected ResponseData handleResponse(ApiResponse response) {
		ResponseData mockResponse = new ResponseData();
	
		Content content = response.getContent();
		
		if (null!=content) {
			content.forEach((contentType, mediaType)->mockResponse.add(contentType, handleSchema(mediaType)));
		}
		
		return mockResponse;
	}
	
	protected Object handleSchema(MediaType mediaType) {
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
	
	protected JsonNode generateMockData(Schema schema) {
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
	
	protected JsonNode generateMockArray(ArraySchema schema) {
		Schema<?> itemSchema = expandSchema(schema.getItems());
		
		ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
		
		arrayNode.add(generateMockData(itemSchema));
		
		return arrayNode;
	}
	
	protected JsonNode generateMockObject(ObjectSchema schema) {
		Map<String, Schema> properties = schema.getProperties();
		ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
		
		properties.forEach((name, s)->objectNode.set(name, generateMockData(s)));
		
		return objectNode;
	}
	
	protected JsonNode generateMockInteger(IntegerSchema schema) {
		int randomInt = ThreadLocalRandom.current().nextInt(0, 10);
		return JsonNodeFactory.instance.numberNode(randomInt);
	}
	
	protected JsonNode generateMockString(StringSchema schema) {
		int randomInt = ThreadLocalRandom.current().nextInt(3, 10);
		String randomString = RandomStringUtils.randomAlphabetic(randomInt);
		return JsonNodeFactory.instance.textNode(randomString);
	}	
	
	protected Schema expandSchema(Schema schema) {
		Schema expandedSchema = schema;
		String ref = schema.get$ref();
		
		if (StringUtils.isNotBlank(ref)) {
			expandedSchema = (Schema) resolveRef(ref);
		}		
		
		return expandedSchema;
	}
	
	protected Object resolveRef(String ref) {
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
	
	protected Object resolveRef(String category, String name) {
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
