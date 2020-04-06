package com.tcg.aws.poc;

import java.io.IOException;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;

public class LambdaLogsToESApp {

	public static void main(String[] args) throws IOException {
		App app = new App();

		LambdaLogsToESStack lambdaLogsToESStack = new LambdaLogsToESStack(app, "labEventsApiStack");
		lambdaLogsToESStack.build();

		Environment.builder().account("086272791573").region("ap-southeast-1").build();

		app.synth();
	}

}
