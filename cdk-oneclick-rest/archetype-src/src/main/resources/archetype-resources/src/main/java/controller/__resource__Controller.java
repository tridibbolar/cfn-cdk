package ${groupId}.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import ${groupId}.domain.${resource};
import ${groupId}.exception.ResourceException;
import ${groupId}.repository.${resource}Repository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class ${resource}Controller {
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(${resource}Controller.class);

	@Autowired
	private ${resource}Repository the${resource}Repository;

	@GetMapping("/${restpath}s")
	public List<${resource}> retrieveAll${resource}s() {
		LOG.debug("Retrieve all Resource -------");
		return the${resource}Repository.findAll();
	}

	@GetMapping("/${restpath}s/{id}")
	public Resource<${resource}> retrieve${resource}(@PathVariable long id) {
		Optional<${resource}> options = the${resource}Repository.findById(id);

		if (!options.isPresent()){
			LOG.error("Resource not found for the Id # {}",id);
			throw new ResourceException("Resource Not found with id-" + id);
		}

		Resource<${resource}> resource = new Resource<${resource}>(options.get());

		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAll${resource}s());

		resource.add(linkTo.withRel("all-students"));

		return resource;
	}

	@DeleteMapping("/${restpath}s/{id}")
	public ResponseEntity<String> delete${resource}(@PathVariable long id) {
		Optional<${resource}> student = the${resource}Repository.findById(id);

		if (!student.isPresent()){
			LOG.error("Resource not found for the Id # {}",id);
			throw new ResourceException("Delete Failed. Resource Not found with id-" + id);
		}

		the${resource}Repository.deleteById(id);
		return new ResponseEntity<>("Resource Deleted Successfully with Id # "+id, HttpStatus.OK);
	}

	@PostMapping("/${restpath}s")
	public ResponseEntity<String> create${resource}(@RequestBody ${resource} resource) {
		Optional<${resource}> std = the${resource}Repository.findById(resource.getId());

		if (std.isPresent()){
			LOG.error("Resource found for the Id # {}. New entry not possible.",resource.getId());
			throw new ResourceException("Resource found for the Id # "+resource.getId()+" New entry not possible.");
			//throw new Exception("${resource} found for the Id # "+resourse.getId()+" New entry not possible.");
		}
		${resource} savedResource = the${resource}Repository.save(resource);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedResource.getId()).toUri();

		//return ResponseEntity.created(location).build();

		return new ResponseEntity<>("Resource created with Id # "+savedResource.getId(), HttpStatus.OK);

	}

	@PutMapping("/${restpath}s/{id}")
	public ResponseEntity<String> update${resource}(@RequestBody ${resource} resource, @PathVariable long id) {

		Optional<${resource}> optional = the${resource}Repository.findById(id);

		if (!optional.isPresent()){
			LOG.error("Resource not found for the Id # {}",id);
			throw new ResourceException("Update Failed. Resource Not found with id-" + id);
			//return ResponseEntity.notFound().build();
		}

		resource.setId(id);
		the${resource}Repository.save(resource);
		//return ResponseEntity.noContent().build();
		return new ResponseEntity<>("Resource updated with Id # "+id, HttpStatus.OK);
	}
}
