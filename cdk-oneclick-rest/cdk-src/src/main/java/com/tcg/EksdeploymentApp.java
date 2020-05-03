package com.tcg;

import com.tcg.stk.CodecommitStack;
import com.tcg.stk.CodepipelineStack;
import com.tcg.stk.EcrStack;
import com.tcg.stk.EcsFargateStack;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

public class EksdeploymentApp {
	public static void main(final String[] args) throws Exception {
        App app = new App();

        //Environment envEU = makeEnv(null, null);
        StackProps properties = StackProps.builder().env(makeEnv(null, null)).build();
        
        new CodecommitStack(app, "CodecommitStack");
        new EcrStack(app, "ECRStack");
        Stack codepipeline = new CodepipelineStack(app, "CodepipelineStack");
        
        //new VpcStack(app, "VpcStack");
        
        Stack fargate = new EcsFargateStack(app, "EcsFargateStack", properties);
        fargate.addDependency(codepipeline);
        app.synth();
    }

	// Helper method to build an environment
	static Environment makeEnv(String account, String region) {
		account = (account == null) ? System.getenv("CDK_DEFAULT_ACCOUNT") : account;
		region = (region == null) ? System.getenv("CDK_DEFAULT_REGION") : region;

		return Environment.builder().account(account).region(region).build();
	}
}
