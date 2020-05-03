package com.tcg.stk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.omg.PortableServer.ImplicitActivationPolicyOperations;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.CfnParameter;
import software.amazon.awscdk.core.Fn;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.codebuild.BuildEnvironment;
import software.amazon.awscdk.services.codebuild.BuildSpec;
import software.amazon.awscdk.services.codebuild.LinuxBuildImage;
import software.amazon.awscdk.services.codebuild.PipelineProject;
import software.amazon.awscdk.services.codecommit.IRepository;
import software.amazon.awscdk.services.codecommit.Repository;
import software.amazon.awscdk.services.codedeploy.EcsApplication;
import software.amazon.awscdk.services.codepipeline.Artifact;
import software.amazon.awscdk.services.codepipeline.Pipeline;
import software.amazon.awscdk.services.codepipeline.StageProps;
import software.amazon.awscdk.services.codepipeline.actions.CloudFormationCreateUpdateStackAction;
import software.amazon.awscdk.services.codepipeline.actions.CodeBuildAction;
import software.amazon.awscdk.services.codepipeline.actions.CodeCommitSourceAction;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcLookupOptions;
import software.amazon.awscdk.services.ecs.AddCapacityOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinition;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.EcsTarget;
import software.amazon.awscdk.services.ecs.PlacementConstraint;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.TaskDefinition;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.CfnParametersCode;

public class CodepipelineStack extends Stack {

	public CodepipelineStack(final App scope, final String id) {
		this(scope, id, null);
	}

	@SuppressWarnings("serial")
	public CodepipelineStack(final App scope, final String id, final StackProps props) {
		super(scope, id, props);

		CfnParameter ecrRepositoryName = CfnParameter.Builder.create(this, "coderepositoryname")
		        .description("Name of the code repository that can be passed in at deploy-time.")
		        .type("String").build();
		
		IRepository code = Repository.fromRepositoryName(this, "OneClickRepo", ecrRepositoryName.getValueAsString());

		PipelineProject dockerBuild = PipelineProject.Builder.create(this, "DockerBuild")
				.projectName("OneCllickProject").buildSpec(BuildSpec.fromSourceFilename("buildspec.yml"))
				.environment(
						BuildEnvironment.builder().buildImage(LinuxBuildImage.STANDARD_2_0).privileged(true).build())
				.build();

		dockerBuild.addToRolePolicy(
				PolicyStatement.Builder.create().resources(Arrays.asList("*")).actions(Arrays.asList("*")) // "ecr:GetAuthorizationToken","ecr:InitiateLayerUpload",
																											// "ecr:UploadLayerPart"
						.build());

		Artifact sourceOutput = new Artifact();
		Artifact imageOutput = new Artifact("ImageOutput");

		Pipeline.Builder.create(this, "OneClickPipeline").stages(Arrays.asList(
				StageProps.builder().stageName("OneClickSource")
						.actions(Arrays.asList(CodeCommitSourceAction.Builder
								.create().actionName("Source").repository(code).output(sourceOutput).build()))
						.build(),
				StageProps.builder().stageName("OneClickBuild")
						.actions(Arrays.asList(CodeBuildAction.Builder.create().actionName("DockerBuild")
								.project(dockerBuild).input(sourceOutput).outputs(Arrays.asList(imageOutput)).build()))
						.build()))
				// StageProps.builder()
				// .stageName("Deploy")
				// .actions(Arrays.asList(
				// CloudFormationCreateUpdateStackAction.Builder.create()
				// .actionName("Lambda_CFN_Deploy")
				// .templatePath(imageOutput.atPath("LambdaStack.template.json"))
				// .adminPermissions(true)
				// .parameterOverrides(lambdaCode.assign(lambdaBuildOutput.getS3Location()))
				// .extraInputs(Arrays.asList(lambdaBuildOutput))
				// .build())
				// .build()))
				.build();
	}
}