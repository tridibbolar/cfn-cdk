package com.tcg.aws.poc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jetbrains.annotations.NotNull;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Aws;
import software.amazon.awscdk.core.CfnParameter;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.dynamodb.CfnTable;
import software.amazon.awscdk.services.elasticsearch.CfnDomain;
import software.amazon.awscdk.services.elasticsearch.CfnDomain.EBSOptionsProperty;
import software.amazon.awscdk.services.elasticsearch.CfnDomain.ElasticsearchClusterConfigProperty;
import software.amazon.awscdk.services.elasticsearch.CfnDomain.EncryptionAtRestOptionsProperty;
import software.amazon.awscdk.services.elasticsearch.CfnDomain.NodeToNodeEncryptionOptionsProperty;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.FromRoleArnOptions;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.PolicyDocument;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.CfnEventSourceMapping;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.CfnFunction.CodeProperty;
import software.amazon.awscdk.services.lambda.CfnFunction.EnvironmentProperty;
import software.amazon.awscdk.services.lambda.CfnPermission;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.CfnSubscriptionFilter;
import software.amazon.awscdk.services.logs.ILogGroup;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.sqs.CfnQueue;

public class LambdaLogsToESStack extends Stack {
	
	private static final Properties props = new Properties();
	
	private static final InputStream configStream = LambdaLogsToESStack.class.getClassLoader().getResourceAsStream("config.properties");
	
	public LambdaLogsToESStack(App app, String id) throws IOException {
		super(app, id);
		if (configStream != null) {
			props.load(configStream);
		} else {
			throw new FileNotFoundException("property file config.properties not found in the classpath");
		}
	}

	public void build() throws IOException {
		CfnParameter labEventTableName = CfnParameter.Builder.create(this, "LabEventTableNameParam").type("String")
				.defaultValue("lab-event-records").build();
		
		CfnParameter labEventQueueName = CfnParameter.Builder.create(this, "LabEventQueueNameParam").type("String")
				.defaultValue("lab-event-queue").build();
		
		CfnParameter labEventBucketName = CfnParameter.Builder.create(this, "LabEventBucketNameParam").type("String")
				.defaultValue("lab-lambda-code-bucket").build();
		
		CfnParameter labEventESName = CfnParameter.Builder.create(this, "LabEventESNameParam").type("String")
				.defaultValue("lab-logs-domain").build();
		
		CfnQueue labEventQueue = CfnQueue.Builder.create(this, "LabEventQueue").queueName(labEventQueueName.getValueAsString()).build();

		CfnTable labEventTable = CfnTable.Builder.create(this, "LabEventTableName").tableName(labEventTableName.getValueAsString())
				.keySchema(Arrays.asList(CfnTable.KeySchemaProperty.builder().attributeName("pk").keyType("HASH").build()
						,CfnTable.KeySchemaProperty.builder().attributeName("sk").keyType("RANGE").build()))
				.attributeDefinitions(Arrays.asList(CfnTable.AttributeDefinitionProperty.builder().attributeName("pk").attributeType("S").build()
						, CfnTable.AttributeDefinitionProperty.builder().attributeName("sk").attributeType("S").build()))
				.billingMode("PAY_PER_REQUEST")
				.build();
		labEventTable.addDependsOn(labEventQueue);
		
		
		
		Map<String, PolicyDocument> inlinePolicies = new HashMap<>();
		inlinePolicies.put("logs-and-ec2-permissions", PolicyDocument.Builder.create()
		        .statements(Arrays.asList(PolicyStatement.Builder.create()
				        .effect(Effect.ALLOW)
				        .resources(Arrays.asList(labEventTable.getAttrArn()))
				        .actions(Arrays.asList("dynamodb:*"))
				        .build(), 
				        PolicyStatement.Builder.create()
				        .effect(Effect.ALLOW)
				        .resources(Arrays.asList(labEventQueue.getAttrArn()))
				        .actions(Arrays.asList("sqs:*"))
				        .build(), 
				        PolicyStatement.Builder.create()
				        .effect(Effect.ALLOW)
				        .resources(Arrays.asList("arn:aws:es:*:*:*"))
				        .actions(Arrays.asList("es:*"))
				        .build(),
				        PolicyStatement.Builder.create()
				        .effect(Effect.ALLOW)
				        .resources(Arrays.asList("arn:aws:logs:*:*:*"))
				        .actions(Arrays.asList("logs:*"))
				        .build(),
				        PolicyStatement.Builder.create()
				        .effect(Effect.ALLOW)
				        .resources(Arrays.asList("arn:aws:ec2:*:*:*"))
				        .actions(Arrays.asList("ec2:CreateNetworkInterface", "ec2:DescribeNetworkInterfaces", "ec2:DeleteNetworkInterface"))
				        .build()))
		        .build());
		
		Role role = Role.Builder.create(this, "Role").assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
				.description("Role to access dynamodb from lambda").roleName("lab-lambda-dynamodb-role")
				.managedPolicies(Arrays.asList(ManagedPolicy.fromManagedPolicyArn(this, "AWSLambdaBasicExecutionRole", "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole")))
				.inlinePolicies(inlinePolicies)
				.build();
		
		Map<String, String> labEventDB = new HashMap<String, String>();
		labEventDB.put("TABLE_NAME", labEventTableName.getValueAsString());
		
		@NotNull
		IRole lambdaRole = Role.fromRoleArn(this, "lambda-role", role.getRoleArn(), FromRoleArnOptions.builder().mutable(false).build());
		CfnFunction labEventFn = CfnFunction.Builder.create(this, "LabEventLambda").functionName("lab-write-events")
				.code(CodeProperty.builder().s3Bucket(labEventBucketName.getValueAsString()).s3Key("lab-write-events.zip").build())
				.role(lambdaRole.getRoleArn())
				.handler("index.handler")
				.environment(EnvironmentProperty.builder().variables(labEventDB).build())
				.runtime(Runtime.NODEJS_12_X.getName())
				.build();
		labEventFn.addDependsOn(labEventTable);
		
		CfnEventSourceMapping.Builder.create(this, "labEventFnLabEventQueue")
				.functionName(labEventFn.getAttrArn()).eventSourceArn(labEventQueue.getAttrArn())
				.enabled(true).build();
		
		Map<String, String> advancedOptions = new HashMap<>();
		advancedOptions.put("rest.action.multi.allow_explicit_index", "true");
		
		CfnDomain labDomain = CfnDomain.Builder.create(this, "labDomain").domainName(labEventESName.getValueAsString()).elasticsearchVersion("7.4")
				.elasticsearchClusterConfig(ElasticsearchClusterConfigProperty.builder().dedicatedMasterEnabled(false).instanceType("r5.large.elasticsearch").instanceCount(1).build())
				.ebsOptions(EBSOptionsProperty.builder().volumeSize(10).volumeType("gp2").ebsEnabled(true).build())
				.nodeToNodeEncryptionOptions(NodeToNodeEncryptionOptionsProperty.builder().enabled(true).build())
				.encryptionAtRestOptions(EncryptionAtRestOptionsProperty.builder().kmsKeyId("arn:aws:kms:ap-southeast-1:086272791573:key/2ef59a28-907b-403e-9888-ffdb22440c0c").enabled(true).build())
				.accessPolicies(PolicyDocument.Builder.create().statements(Arrays.asList(PolicyStatement.Builder.create()
				        .effect(Effect.ALLOW)
				        .resources(Arrays.asList("arn:aws:es:ap-southeast-1:086272791573:domain/"+labEventESName.getValueAsString()+"/*")).actions(Arrays.asList("es:*"))
				        .principals(Arrays.asList(new AnyPrincipal())).build())).build())
				.build();
		labDomain.addDependsOn(labEventTable);
		
		Map<String, String> labElasticSearchEndpoint = new HashMap<String, String>();
		labElasticSearchEndpoint.put("ELASTIC_SEARCH_ENDPOINT", labDomain.getAttrDomainEndpoint());
		
		CfnFunction labElasticSearchFn = CfnFunction.Builder.create(this, "LabElasticSearchLambda").functionName("lab-elastic-search-event")
				.code(CodeProperty.builder().s3Bucket(labEventBucketName.getValueAsString()).s3Key("LogsToElasticsearch.zip").build())
				.role(lambdaRole.getRoleArn())
				.handler("index.handler")
				.environment(EnvironmentProperty.builder().variables(labElasticSearchEndpoint).build())
				.runtime(Runtime.NODEJS_12_X.getName())
				.build();
		
		labElasticSearchFn.addDependsOn(labDomain);
		
		@NotNull
		ILogGroup logGroup = LogGroup.fromLogGroupName(this, "logGrp", "/aws/lambda/" + labEventFn.getFunctionName());
		
		CfnPermission permission = CfnPermission.Builder.create(this, "lambdaInvokePermission").action("lambda:InvokeFunction").sourceAccount(Aws.ACCOUNT_ID)
				.functionName(labElasticSearchFn.getAttrArn()).principal("logs.ap-southeast-1.amazonaws.com").sourceArn(logGroup.getLogGroupArn() + ":*").build();
		
		CfnSubscriptionFilter filter = CfnSubscriptionFilter.Builder.create(this, "labSubscriptionFilter").logGroupName("/aws/lambda/" + labEventFn.getFunctionName())
			.destinationArn(labElasticSearchFn.getAttrArn()).filterPattern("").build();
		
		filter.addDependsOn(permission);
	}
}