package ${groupId}.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceException extends RuntimeException {

	public ResourceException(String exception) {
		super(exception);
	}

}
